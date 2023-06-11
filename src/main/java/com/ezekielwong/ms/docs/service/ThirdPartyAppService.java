package com.ezekielwong.ms.docs.service;

import com.ezekielwong.ms.docs.domain.request.thirdpartyapp.UploadDocumentRequest;
import com.ezekielwong.ms.docs.domain.request.thirdpartyapp.dto.UploadDocumentDto;
import com.ezekielwong.ms.docs.entity.Docs;
import com.ezekielwong.ms.docs.filenet.response.FilenetResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.xml.bind.JAXBException;

public interface ThirdPartyAppService {

    UploadDocumentDto extractDocDataList(UploadDocumentRequest uploadRequest);

    Docs retrieveDocs(String caseId, String name);

    FilenetResponse uploadDocumentIntoFilenet(UploadDocumentDto uploadDocumentDto) throws JAXBException, JsonProcessingException;

    Docs updateDocs (Docs docsDB, UploadDocumentDto uploadDocumentDto, String docRefId);

    void publishKafkaMessage(Docs docsUpdated) throws JsonProcessingException;
}
