package com.ezekielwong.ms.docs.entity;

import com.ezekielwong.ms.docs.domain.request.client.common.FieldData;
import com.ezekielwong.ms.docs.domain.request.client.common.RequesterInfo;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
@Table(name = "tbl_docs")
public class Docs extends BaseEntity {

    /**
     * Unique client workflow request case identifier
     */
    @Column(name = "case_id", unique = true, updatable = false, nullable = false)
    private String caseId;

    /**
     * Name of the workflow
     */
    @Column(name = "template_name", updatable = false, nullable = false)
    private String templateName;

    /**
     * List of workflow data
     */
    @Type(JsonType.class)
    @Column(name = "field_data_list", nullable = false)
    private List<FieldData> fieldDataList;

    /**
     * Requester details
     */
    @Type(JsonType.class)
    @Column(name = "requester_info", nullable = false)
    private RequesterInfo requesterInfo;

    /**
     * Document properties required by filenet
     */
    @Type(JsonType.class)
    @Column(name = "doc_props", nullable = false)
    private Map<String, String> docPropsMap;

    /**
     * GUID of the document stored in filenet
     */
    @Column(name = "doc_ref_id", unique = true)
    private String docRefId;

    /**
     * Document metadata in JSON format
     */
    @Type(JsonType.class)
    @Column(name = "metadata")
    private Map<String, Map<String, String>> metadata;

    /**
     * Third party app document id
     */
    @Column(name = "third_party_app_doc_id", unique = true)
    private String thirdPartyAppDocId;

    /**
     * Current status of the request
     */
    @Column(name = "status")
    private String status;

    /**
     * Flag to check if document has been purged from the third party app
     */
    @Column(name = "is_purged", length = 1, nullable = false)
    private String isPurged = "N";
}
