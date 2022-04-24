package com.chinamobile.zj.flowProcess.bo.definition;

import lombok.Data;

import java.util.List;

/**
 * 流程定义
 */
@Data
public class FlowProcessDefinitionBO {

    private String resourceId;

    private ProcessPropertiesBO properties;

    private List<ResourceDefinitionBO> childShapes;

}
