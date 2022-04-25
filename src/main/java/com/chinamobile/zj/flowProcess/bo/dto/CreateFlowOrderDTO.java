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
//    private String inputVariables; // 本属性不对架构外部外暴露。只能通过inputVariablesMap赋值 todo zj test
//
//    public void setInputVariablesMap(Map<String, Object> inputVariablesMap) {
//        if(MapUtils.isEmpty(inputVariablesMap)){
//            this.inputVariablesMap = Collections.EMPTY_MAP; //
//            setInputVariables(this.inputVariablesMap);
//            return;
//        }
//        this.inputVariablesMap = inputVariablesMap;
//        setInputVariables(this.inputVariablesMap);
//    }
//
//    private void setInputVariables(Map<String, Object> inputVariablesMap) {
//        this.inputVariables = JsonConvertUtil.toJsonString(inputVariablesMap); // todo zj null
//    }
//
//    private void setInputVariables(String inputVariables) {
//        // 不允许外部赋值
//    }
}
