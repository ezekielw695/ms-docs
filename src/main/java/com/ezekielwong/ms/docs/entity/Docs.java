package com.ezekielwong.ms.docs.entity;

import com.ezekielwong.ms.docs.domain.request.client.common.FieldData;
import com.ezekielwong.ms.docs.domain.request.client.common.RequesterInfo;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "tbl_docs")
public class Docs extends BaseEntity {

    /**
     * Unique client workflow request case identifier
     */
    @NotBlank
    @Column(name = "case_id", unique = true, updatable = false)
    private String caseId;

    /**
     * Name of the workflow
     */
    @NotBlank
    @Column(name = "template_name", updatable = false)
    private String templateName;

    /**
     * List of workflow data
     */
    @NotEmpty
    @Type(JsonType.class)
    @Column(name = "field_data_list")
    private List<FieldData> fieldDataList;

    /**
     * Requester details
     */
    @NotNull
    @Type(JsonType.class)
    @Column(name = "requester_info")
    private RequesterInfo requesterInfo;

    /**
     * GUID of the document stored in the data lake
     */
    @Column(name = "doc_ref_id", unique = true)
    private String docRefId;

    /**
     * Document metadata in XML format
     */
    @Column(name = "metadata")
    private String metadata;

    /**
     * Current status of the request
     */
    @Column(name = "status")
    private String status;
}
