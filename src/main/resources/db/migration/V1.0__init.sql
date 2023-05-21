CREATE TABLE public.tbl_docs (
    id uuid NOT NULL,
    created_by varchar(255) NOT NULL,
    created_date timestamp NOT NULL,
    updated_by varchar(255) NULL,
    updated_date timestamp NULL,
    case_id varchar(255) NOT NULL,
    template_name varchar(255) NOT NULL,
    field_data_list json NOT NULL,
    requester_info json NOT NULL,
    doc_props json NOT NULL,
    doc_ref_id varchar(255) NULL,
    metadata json NULL,
    third_party_app_doc_id varchar(255) NULL,
    status varchar(255) NULL,
    is_purged varchar(1) NOT NULL,
    CONSTRAINT tbl_docs_pk PRIMARY KEY (id),
    CONSTRAINT tbl_docs_un1 UNIQUE (case_id),
    CONSTRAINT tbl_docs_un2 UNIQUE (doc_ref_id),
    CONSTRAINT tbl_docs_un3 UNIQUE (third_party_app_doc_id)
);

CREATE TABLE public.shedlock (
    name varchar(64) NOT NULL,
    lock_until timestamp(3) NULL,
    locked_at timestamp(3) NULL,
    locked_by varchar(255) NOT NULL,
    CONSTRAINT shedlock_pk PRIMARY KEY (name)
);