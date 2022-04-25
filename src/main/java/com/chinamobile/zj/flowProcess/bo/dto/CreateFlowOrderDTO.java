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
//    {
//        // inputVariablesMap 入参设置样例。必须包含key=preCheckApplication, value的class类型为 PreCheckApplication.class 的元素。
//        // 原因见 com.chinamobile.zj.hdict.entity.PreCheckApplication 注释
//        // com.chinamobile.zj.hdict.entity.PreCheckApplication 哪些属性必填，见注解。
//        PreCheckApplication preCheckApplication = new PreCheckApplication();
//        preCheckApplication.setAreaId3("84");
//        preCheckApplication.setCreatorId("??");
//        inputVariablesMap.put("preCheckApplication", preCheckApplication);
//    }
}
