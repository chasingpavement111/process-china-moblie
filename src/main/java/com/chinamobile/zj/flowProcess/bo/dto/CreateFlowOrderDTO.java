package com.chinamobile.zj.flowProcess.bo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class CreateFlowOrderDTO {

    /**
     * 是否立刻启动工单。
     * 流转是否立即开始执行： true(立即启动， 默认值)\false(不，此时需要手动启动，使流程开始执行)
     * <p>
     * 对于预勘工单，应该设置为false. 待提交预勘申请时，再启动工单+完成步骤
     */
    private Boolean startNow;

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
