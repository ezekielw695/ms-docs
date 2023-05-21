package com.ezekielwong.ms.docs.constant;

public class AppConstants {

    // Request status
    public static final String THIRD_PARTY_APP_REQUEST_SENT = "TPA_REQ_SENT";
    public static final String THIRD_PARTY_APP_REQUEST_SENT_ERROR = "TPA_REQ_SENT_ERROR";
    public static final String THIRD_PARTY_APP_REQUEST_NOT_SENT = "TPA_REQ_NOT_SENT";

    public static final String FILENET_UPLOADED = "FILENET_UPLOADED";
    public static final String FILENET_UPLOADED_ERROR = "FILENET_UPLOADED_ERROR";
    public static final String FILENET_NOT_UPLOADED = "FILENET_NOT_UPLOADED";

    public static final String KAFKA_MESSAGE_PUBLISHED = "KAFKA_MSG_PUBLISHED";
    public static final String KAFKA_MESSAGE_NOT_PUBLISHED = "KAFKA_MSG_NOT_PUBLISHED";

    public static final String PURGE_REQUEST_SENT = "PURGE_REQ_SENT";
    public static final String PURGE_REQUEST_SENT_ERROR = "PURGE_REQ_SENT_ERROR";

    // ClientWorkflow response message
    public static final String START_WORKFLOW_SUCCESS = "Workflow started";
    public static final String START_WORKFLOW_FAILURE = "Workflow failed to start";

    // Third party app UploadDocument response message
    public static final String UPLOAD_SUCCESS = "Uploads completed";
    public static final String UPLOAD_FAILED = "Uploads failed";

    // StandardResponse<T> status
    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failure";

    // Others
    public static final String STATUS_UPDATED = "Client request status updated";
    public static final String NULL_RESPONSE = "Null response";
    public static final String UNKNOWN_RESPONSE = "Unknown response";
}
