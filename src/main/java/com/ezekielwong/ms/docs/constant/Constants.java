package com.ezekielwong.ms.docs.constant;

public class Constants {

    // Request status
    public static final String THIRD_PARTY_APP_REQUEST_SENT = "TPA_REQ_SENT";
    public static final String THIRD_PARTY_APP_REQUEST_SENT_ERROR = "TPA_REQ_SENT_ERROR";
    public static final String THIRD_PARTY_APP_REQUEST_NOT_SENT = "TPA_REQ_NOT_SENT";

    public static final String DATA_LAKE_UPLOADED = "DL_UPLOADED";
    public static final String DATA_LAKE_UPLOADED_ERROR = "DL_UPLOADED_ERROR";
    public static final String DATA_LAKE_NOT_UPLOADED = "DL_NOT_UPLOADED";

    public static final String KAFKA_MESSAGE_PUBLISHED = "KAFKA_MSG_PUBLISHED";
    public static final String KAFKA_MESSAGE_NOT_PUBLISHED = "KAFKA_MSG_NOT_PUBLISHED";

    // StandardResponse<T> status
    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failure";

    // ClientWorkflow response message
    public static final String START_WORKFLOW_SUCCESS = "Workflow started";
    public static final String START_WORKFLOW_FAILURE = "Workflow failed to start";

    // Third party app UploadDocument response message
    public static final String UPLOAD_SUCCESS = "Uploads completed";
    public static final String UPLOAD_FAILED = "Uploads failed";

    // Others
    public static final String STATUS_UPDATED = "Client request status updated";
    public static final String NULL_RESPONSE = "Null response";
    public static final String UNKNOWN_RESPONSE = "Unknown response";
}
