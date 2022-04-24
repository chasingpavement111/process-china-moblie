package com.chinamobile.zj.flowProcess.bo.definition;

import lombok.Data;

import java.util.List;

@Data
public class FlowProcessOrderedBO {

    private String resourceId;

    private ProcessPropertiesBO properties;

    private List<ResourceDefinitionBO> orderedChildShapes;

}
