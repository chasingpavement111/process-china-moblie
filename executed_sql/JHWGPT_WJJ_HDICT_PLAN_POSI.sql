create table WJJ_HDICT_PLAN_POSI
(
    PLAN_ID          VARCHAR(20) not null,
    EXPL_POSITION_ID VARCHAR(20) not null,
    POSITION_ID      VARCHAR(20) not null,
    ENTRANCE_IMG     VARCHAR(20) not null,
    EXIT_IMG         VARCHAR(200),
    REMARK           VARCHAR(200),
    CREATE_USER      VARCHAR(20),
    CREATE_DATE      DATE
)
/

comment '预勘方案点位清单';