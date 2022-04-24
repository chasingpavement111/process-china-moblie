package com.chinamobile.zj.flowProcess.service.resource;

import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;

import java.util.List;
import java.util.Map;

public interface MultipleNextOutGoing {

    List<ResourceDefinitionBO> getNextOutGoingExecutionResource(ResourceDefinitionBO resourceDefinitionBO, Map<String, Object> outputVariablesMap);
}
