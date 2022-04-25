create table WJJ_HDICT_ACTIVI_LIST
(
    TASK_ID      VARCHAR(20) not null,
    EXPL_REQ_ID  VARCHAR(20) not null,
    LINE_ID      VARCHAR(20) not null,
    HANDLE_MAN   VARCHAR(20),
    CHECK_STATUS VARCHAR(20),
    CHECK_DATE   VARCHAR(20),
    ASSIGN_MAN   VARCHAR(20),
    CHECK_REMARK VARCHAR(200)
)

comment 'hdict流程步骤流转信息表'
;

