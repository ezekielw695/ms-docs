package com.ezekielwong.ms.docs.service.impl;

import com.ezekielwong.ms.docs.domain.request.thirdpartyapp.UploadDocumentRequest;
import com.ezekielwong.ms.docs.domain.request.thirdpartyapp.common.DocData;
import com.ezekielwong.ms.docs.domain.request.thirdpartyapp.common.Document;
import com.ezekielwong.ms.docs.domain.request.thirdpartyapp.dto.UploadDocumentDto;
import com.ezekielwong.ms.docs.entity.Docs;
import com.ezekielwong.ms.docs.exception.FilenetCallErrorException;
import com.ezekielwong.ms.docs.exception.FilenetNullResponseBodyException;
import com.ezekielwong.ms.docs.exception.common.GenericBadException;
import com.ezekielwong.ms.docs.exception.common.GenericSuccessException;
import com.ezekielwong.ms.docs.filenet.FilenetBody;
import com.ezekielwong.ms.docs.filenet.FilenetContext;
import com.ezekielwong.ms.docs.filenet.FilenetEnvelope;
import com.ezekielwong.ms.docs.filenet.mapping.DefaultNamespacePrefixMapper;
import com.ezekielwong.ms.docs.filenet.request.FilenetRequest;
import com.ezekielwong.ms.docs.filenet.response.FilenetResponse;
import com.ezekielwong.ms.docs.repository.DocsRepository;
import com.ezekielwong.ms.docs.service.ClientService;
import com.ezekielwong.ms.docs.service.ThirdPartyAppService;
import com.ezekielwong.ms.docs.utils.KafkaUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.ezekielwong.ms.docs.constant.AppConstants.*;
import static com.ezekielwong.ms.docs.constant.ExceptionEnum.*;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.FILENET_CALL_ERROR_MSG;
import static com.ezekielwong.ms.docs.constant.ExceptionMessages.MSDB_CASE_ID_NOT_FOUND_MSG;

/**
 * Service for third party app requests
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThirdPartyAppServiceImpl implements ThirdPartyAppService {

    private final DocsRepository docsRepository;

    private final DefaultNamespacePrefixMapper defaultNamespacePrefixMapper;

    private final OkHttpClient okHttpClient;

    private final ClientService clientService;

    private final KafkaUtils kafkaUtils;

    @Value("${filenet.hostname}")
    private String hostname;

    @Value("${filenet.documentclass}")
    private String documentclass;

    @Value("${filenet.objectstore}")
    private String objectstore;

    @Value("${filenet.checkin.url}")
    private String checkinUrl;

    @Value("${filenet.checkin.soap-action.url}")
    private String soapActionUrl;

//    @Value("${}")
    private String topic;

    @Value("#{'${filenet.doc-prop.prop-name-list}'.split(',')}")
    private List<String> propNameList;

    /**
     * Extract Filenet properties required for document upload from the Third Party App upload document API request
     *
     * @param uploadRequest {@link UploadDocumentRequest}
     * @return uploadDocumentDto {@link UploadDocumentDto}
     */
    @Override
    public UploadDocumentDto extractDocDataList(UploadDocumentRequest uploadRequest) {

        log.debug("Extracting document metadata");
        Document document = uploadRequest.getDocument();

        // Convert list of metadata to a nested linked hashmap
        Map<String, Map<String, String>> docDataMap;

        try {
            docDataMap = document.getDocDataList().stream()
                    .collect(Collectors.groupingBy(DocData::getGroup, LinkedHashMap::new,
                            Collectors.toMap(DocData::getField, DocData::getValue,
                                    (v1, v2) -> {
                                        String errMsg = String.format("Duplicate key found (attempted merging values [ %s ] and [ %s ])", v1, v2);
                                        log.error(errMsg);
                                        throw new IllegalStateException(errMsg);
                                    }, LinkedHashMap::new)));
        } catch (IllegalStateException e) {

            String errMsg = e.getMessage();
            throw new GenericBadException(REQUEST_VALIDATION_ERROR, errMsg);
        }

        UploadDocumentDto uploadDocumentDto = UploadDocumentDto.builder()
                .correlationId(uploadRequest.getCorrelationId())
                .dateTime(uploadRequest.getDateTime())
                .updatedChannel(uploadRequest.getUpdatedChannel())
                .documentId(document.getId())
                .name(document.getName())
                .caseId(document.getCaseId())
                .metadata(docDataMap)
                .docContent(document.getDocContent())
                .build();

        log.debug(uploadDocumentDto.toString());

        return uploadDocumentDto;
    }

    /**
     * Retrieve existing Docs
     *
     * @param caseId Unique client workflow request case identifier
     * @param name Name of the document generated by the third party app
     * @return Existing {@link Docs} in MSDB with updated document properties map
     */
    @Override
    public Docs retrieveDocs(String caseId, String name) {

        log.debug("Retrieving Docs from MSDB");
        Optional<Docs> docsDB = Optional.ofNullable(docsRepository.findByCaseId(caseId));

        if (docsDB.isPresent()) {

            Docs docs = docsDB.get();
            Map<String, String> docPropsMap = docs.getDocPropsMap();

            // Update document title with name generated by the third party app
            docPropsMap.put("DocumentTitle", name);
            docs.setDocPropsMap(docPropsMap);

            return docs;

        } else {

            log.error(MSDB_CASE_ID_NOT_FOUND_MSG);
            throw new GenericSuccessException(MSDB_CASE_ID_NOT_FOUND);
        }
    }

    /**
     * Upload document into Filenet
     *
     * @param uploadDocumentDto {@link UploadDocumentDto}
     * @return {@link FilenetResponse}
     * @throws JAXBException Encountered while processing (parsing, generating) invalid XML content
     * @throws JsonProcessingException Encountered when processing (parsing, generating) invalid JSON content
     */
    @Override
    public FilenetResponse uploadDocumentIntoFilenet(UploadDocumentDto uploadDocumentDto) throws JAXBException, JsonProcessingException {

        log.debug("Sending upload document request to Filenet");

        // SOAP request identifiers
        String envelopUuid = UUID.randomUUID().toString();
        String attachmentUuid = UUID.randomUUID().toString();
        String boundaryUuid = UUID.randomUUID().toString();

        // Build SOAP request header
        Headers soapHeader = buildSoapRequestHeader(envelopUuid, boundaryUuid);

        // Build SOAP request body
        byte[] soapBody = buildSoapRequestBody(uploadDocumentDto, envelopUuid, attachmentUuid, boundaryUuid);

        // Upload document
        try {
            RequestBody requestBody = RequestBody.create(soapBody, null);
            Request request = (new Request.Builder()).url(checkinUrl).headers(soapHeader).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();

            // Response is null
            if (response.body() == null) {

                log.error(NULL_RESPONSE);

                // Update status to FILENET_UPLOADED_ERROR
                clientService.updateStatus(uploadDocumentDto.getCaseId(), FILENET_UPLOADED_ERROR);
                log.info(STATUS_UPDATED);

                throw new FilenetNullResponseBodyException(UNKNOWN_ERROR, "Filenet return null response body");
            }

            // Response is not null
            FilenetEnvelope responseEnvelope = deserializeSoapResponse(response.body().string());
            log.info("SOAP response body deserialized");

            return responseEnvelope.getFilenetbody().getFilenetresponse();

        } catch (IOException exception) {

            log.error(FILENET_CALL_ERROR_MSG);

            // Update status to FILENET_NOT_UPLOADED
            clientService.updateStatus(uploadDocumentDto.getCaseId(), FILENET_NOT_UPLOADED);
            log.info(STATUS_UPDATED);

            Throwable cause = exception.getCause();
            String errMsg = FILENET_CALL_ERROR_MSG + ": [ " + (cause != null ? cause.getMessage() : exception.getMessage() + " ]");

            throw new FilenetCallErrorException(FILENET_CALL_ERROR, errMsg);
        }
    }

    /**
     * Update existing Docs with docRefId and metadata
     *
     * @param docs The existing {@link Docs}
     * @param uploadDocumentDto {@link UploadDocumentDto}
     * @param docRefId GUID of the document stored in Filenet
     * @return The updated {@link Docs}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Docs updateDocs(Docs docs, UploadDocumentDto uploadDocumentDto, String docRefId) {

        log.debug("Updating Docs");

        Map<String, Map<String, String>> metadata = uploadDocumentDto.getMetadata();
        metadata.get("Document").put("CaseId", uploadDocumentDto.getCaseId());
        metadata.get("Document").put("DocRefId", docRefId);

        docs.setUpdatedBy(uploadDocumentDto.getUpdatedChannel());
        docs.setDocRefId(docRefId);
        docs.setMetadata(metadata);
        docs.setThirdPartyAppDocId(uploadDocumentDto.getDocumentId());
        docs.setStatus(FILENET_UPLOADED);

        return docsRepository.saveAndFlush(docs);
    }

    /**
     * Publish Kakfa message asynchronously
     *
     * @param docsUpdated The updated {@link Docs}
     * @throws JsonProcessingException Encountered when process (parsing, generating) invalid JSON content
     */
    @Override
    @Async("asyncExecutor")
    public void publishKafkaMessage(Docs docsUpdated) throws JsonProcessingException {

        log.debug("Publishing Kafka message asynchronously");
        String correlationId = UUID.randomUUID().toString();
        String message = kafkaUtils.createMessage(docsUpdated.getMetadata(), topic, correlationId);

        kafkaUtils.sendMessage(topic, correlationId, message, docsUpdated.getCaseId());
    }

    /**
     * Build the SOAP request header
     *
     * @param envelopUuid SOAP enveloper identifier
     * @param boundaryUuid SOAP boundary identifier
     * @return SOAP request header
     */
    private Headers buildSoapRequestHeader(String envelopUuid, String boundaryUuid) {

        log.debug("Building SOAP request header");
        String contentType = "multipart/related; type=\"text/xml\"; start=\"<" + envelopUuid +
                ">\"; boundary=\"----=_boundary_" + boundaryUuid + "\"";

        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("SoapAction", soapActionUrl);
        headersMap.put(HttpHeaders.CONTENT_TYPE, contentType);

        Headers headers = Headers.of(headersMap);
        log.debug("SOAP request header\n" + headers);

        return headers;
    }


    private byte[] buildSoapRequestBody(UploadDocumentDto uploadDocumentDto, String envelopUuid,
                                        String attachmentUuid, String boundaryUuid) throws JAXBException {

        log.debug("Building SOAP request body");

        // Build SOAP envelope
        FilenetContext filenetContext = FilenetContext.builder()
                .messageid(uploadDocumentDto.getCorrelationId())
                .hostname(hostname)
                .timestamp(uploadDocumentDto.getDateTime())
                .userid(uploadDocumentDto.getUpdatedChannel())
                .build();

        // Extract document properties
        FilenetRequest.Docprops docprops = extractDocProps(uploadDocumentDto.getDocPropsMap());

        FilenetRequest filenetRequest = FilenetRequest.builder()
                .documentclass(documentclass)
                .objectstore(objectstore)
                .docprops(docprops)
                .build();

        FilenetBody filenetBody = FilenetBody.builder()
                .filenetcontext(filenetContext)
                .filenetrequest(filenetRequest)
                .build();

        FilenetEnvelope filenetEnvelope = FilenetEnvelope.builder()
                .filenetbody(filenetBody)
                .build();

        // Marshal SOAP envelope into XML string
        JAXBContext jaxbContext = JAXBContext.newInstance(FilenetEnvelope.class);
        Marshaller marshaller = jaxbContext.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", defaultNamespacePrefixMapper);

        StringWriter sw = new StringWriter();
        marshaller.marshal(filenetEnvelope, sw);
        String envelope = sw.toString();

        // Build SOAP message and attach document
        byte[] bodyPrefix
                = ("------=_boundary_" + boundaryUuid + "\r\n"
                + "Content-Type: text/xml; charset=UTF-8\r\n"
                + "Content-Transfer-Encoding: 8bit\r\n"
                + "Content-ID: <" + envelopUuid + ">\r\n"
                + "\r\n" + envelope + "\r\n"
                + "------=_boundary_" + boundaryUuid + "\r\n"
                + "Content-Type: text/plain\r\n"
                + "Filename: " + uploadDocumentDto.getName() + "\r\n"
                + "Content-Transfer-Encoding: binary\r\n"
                + "Content-ID: <" + attachmentUuid + ">\r\n"
                + "\r\n").getBytes(StandardCharsets.US_ASCII);

        byte[] bodyAttachment = uploadDocumentDto.getDocContent().getBytes(StandardCharsets.US_ASCII);

        byte[] bodyPostfix = ("\r\n------=_boundary_" + boundaryUuid + "--\r\n").getBytes(StandardCharsets.US_ASCII);

        int bodyPrefixLength = bodyPrefix.length;
        int bodyAttachmentLength = bodyAttachment.length;
        int bodyPostfixLength = bodyPostfix.length;

        byte[] body = new byte[bodyPrefixLength + bodyAttachmentLength + bodyPostfixLength];
        System.arraycopy(bodyPrefix, 0, body, 0, bodyPrefixLength);
        System.arraycopy(bodyAttachment, 0, body, bodyPrefixLength, bodyAttachmentLength);
        System.arraycopy(bodyPostfix, 0, body, bodyPrefixLength + bodyAttachmentLength, bodyPostfixLength);

        log.debug("SOAP request body\n" + new String(body));

        return body;
    }

    /**
     * Extract Filenet document properties required for document upload
     *
     * @param docPropsMap Properties required for document upload
     * @return {@link FilenetRequest.Docprops}
     */
    private FilenetRequest.Docprops extractDocProps(Map<String, String> docPropsMap) {

        log.debug("Extracting document properties");
        List<FilenetRequest.DocProp> docPropList = new ArrayList<>();

        for (String propName : propNameList) {

            FilenetRequest.DocProp docProp = FilenetRequest.DocProp.builder()
                    .propname(propName)
                    .proptype("String")
                    .propvalue(docPropsMap.get(propName))
                    .build();

            log.debug("Property added: " + docProp.toString());
            docPropList.add(docProp);
        }

        return new FilenetRequest.Docprops(docPropList);
    }

    /**
     * Deserialize the SOAP response
     *
     * @param soapResponse XML string of SOAP response
     * @return {@link FilenetEnvelope}
     * @throws JAXBException Encountered when processing (parsing, generating) invalid XML content
     */
    private FilenetEnvelope deserializeSoapResponse(String soapResponse) throws JAXBException {

        log.debug("SOAP response body\n" + soapResponse);

        // Unmarshall XML string into SOAP envelope
        JAXBContext jaxbContext = JAXBContext.newInstance(FilenetEnvelope.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        FilenetEnvelope response = (FilenetEnvelope) unmarshaller.unmarshal(new StringReader(soapResponse));
        log.debug("Filenet envelope: " + response.toString());

        return response;
    }
}
