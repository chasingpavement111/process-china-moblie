package com.chinamobile.zj.flowProcess.bo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

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
     * 入参
     */
    private Map<String, Object> inputVariablesMap;

}
