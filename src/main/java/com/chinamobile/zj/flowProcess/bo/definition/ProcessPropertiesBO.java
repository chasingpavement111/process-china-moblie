package com.chinamobile.zj.flowProcess.bo.definition;

import lombok.Data;

@Data
public class ProcessPropertiesBO {

    /**
     * 流程定义的唯一标识符
     * flowDefinitionKey
     */
    private String processId;

    private String name;

    private String documentation;
}
