package com.chinamobile.zj.flowProcess.bo.dto;

import lombok.Data;

@Data
public class OrderResourceInstanceInfoResultDTO {

    private String resourceInstanceUuid;

    private String flowResourceDefinitionKey;

    /**
     * 操作人CRM编号
     */
    private String operatorId;
    /**
     * 更新时间，格式：yyyy-MM-dd HH24:mi:ss
     */
    private String operateTime;
    /**
     * 状态
     */
    private String status;
    /**
     * 操作结果描述
     * todo zj 由 userTask  service 自动组装信息后，进行赋值
     */
    private String operationOutputDesc;
}
