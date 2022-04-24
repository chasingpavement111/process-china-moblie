package com.chinamobile.zj.flowProcess.service.resource;

import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;

import java.util.Map;

public interface UniqueNextOutGoing {

    ResourceDefinitionBO getDirectNextOutGoingResource(ResourceDefinitionBO resourceDefinitionBO, Map<String, Object> outputVariablesMap);
}
