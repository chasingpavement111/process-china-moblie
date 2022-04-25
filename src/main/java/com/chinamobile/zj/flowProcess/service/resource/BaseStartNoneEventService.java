package com.chinamobile.zj.flowProcess.service.resource;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BaseStartNoneEventService extends BaseUserTaskService implements MultipleNextOutGoing {

    @Override
    public String getDefinitionKey() {
        return StencilEnum.START_NONE_EVENT.getId();
    }

    @Override
    public String getDefinitionKeyDesc() {
        return StencilEnum.START_NONE_EVENT.getName();
    }

    @Override
    public ExecutionResult execute() {
        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public String getOperationOutputDesc() {
        return null;
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.EMPTY_MAP; // 空集合，标识支持所有类型的用户
    }

    @Override
    public List<ResourceDefinitionBO> getNextOutGoingExecutionResource(ResourceDefinitionBO resourceDefinitionBO, Map<String, Object> outputVariablesMap) {
        setResourceDefinitionBO(resourceDefinitionBO); // todo zj
//        resourceDefinitionBO = getResourceDefinitionBO(); // todo zj
        List<ResourceDefinitionBO> nextOutGoingList = super.getNextOutGoingExecutionResource(resourceDefinitionBO, outputVariablesMap);
        // 所有条件都不成立时，nextOutGoingList==null. 可能情况：不应该！开始节点的连线，不应该是带条件判断的连线，必定是一到多个连线指示有一到多个下一跳
        InternalException.isTrue(CollectionUtils.isEmpty(nextOutGoingList),
                String.format("startNoneEvent must have at least valid one outGoing, but go none! Please contact the administrator to check flow definition[flowDefinitionKey=%s]", getFlowDefinitionKey()));
        return nextOutGoingList;
    }

}
