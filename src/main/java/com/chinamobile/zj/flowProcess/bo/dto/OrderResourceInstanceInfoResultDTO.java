package com.chinamobile.zj.flowProcess.bo.dto;

import lombok.Data;

@Data
public class OrderResourceInstanceInfoResultDTO {

    /**
     * 步骤实例的唯一标识
     */
    private String resourceInstanceUuid;

    /**
     * 步骤定义标识
     */
    private String flowResourceDefinitionKey;

    /**
     * 步骤定义中文描述
     */
    private String flowResourceDefinitionKeyDesc;

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
     * 是否有效：true（有效）/ false（无效，被驳回需要重新执行）
     */
    private Boolean valid;
    /**
     * 操作结果描述
     * todo zj 由 userTask  service 自动组装信息后，进行赋值
     */
    private String operationOutputDesc;
}
