package com.chinamobile.zj.flowProcess.bo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class StartFlowOrderDTO {
    /**
     * 流程工单的唯一标识
     */
    @NotBlank
    private String orderUuid;
    /**
     * 操作人CRM编号
     */
    @NotBlank
    private String operatorId;
    /**
     * 入参
     */
    private Map<String, Object> inputVariablesMap;

}
