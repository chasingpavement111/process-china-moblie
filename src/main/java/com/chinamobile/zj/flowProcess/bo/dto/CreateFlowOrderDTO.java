package com.chinamobile.zj.flowProcess.bo.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
@Data
public class CreateFlowOrderDTO {
    /**
     * 流程定义的唯一标识
     */
    @NotBlank
    private String flowDefinitionKey;
    /**
     * 创建人CRM编号
     */
    @NotBlank
    private String creatorId;
    /**
     * 入参json串
     */
    private String inputVariables;

}
