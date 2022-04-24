package com.chinamobile.zj.flowProcess.service.resource;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public List<String> supportedOperatorRoleList() {
        return null;
    }

    @Override
    public List<ResourceDefinitionBO> getNextOutGoingExecutionResource(ResourceDefinitionBO resourceDefinitionBO, Map<String, Object> outputVariablesMap) {
        // todo zj 换掉！！！ 应该和 userTaskService  的实现一致
        setResourceDefinitionBO(resourceDefinitionBO); // todo zj
//        resourceDefinitionBO = getResourceDefinitionBO(); // todo zj
        List<ResourceDefinitionBO> nextOutGoingList = super.getNextOutGoingExecutionResource(resourceDefinitionBO, outputVariablesMap);
//        List<ResourceDefinitionBO> nextOutGoingList = new ArrayList<>();
//        for (ResourceDefinitionBO sequenceDefinitionBO : resourceDefinitionBO.getOutGoing()) {// 连线元素应该有且只有一个出口，错误可能原因：json文件被恶意篡改（图上也画不出两个出口，故由图导出的结果不会有问题）
//            // 遍历每一种分支是否可行
//            ResourceDefinitionBO nextOutGoing = baseSequenceFlowService.getDirectNextOutGoingResource(sequenceDefinitionBO, outputVariablesMap);
//            if (Objects.nonNull(nextOutGoing)) {
//                nextOutGoingList.add(nextOutGoing);
//            }
//        }
        // 所有条件都不成立时，nextOutGoingList==null. 可能情况：不应该！开始节点的连线，不应该是带条件判断的连线，必定是一到多个连线指示有一到多个下一跳
        InternalException.isTrue(CollectionUtils.isEmpty(nextOutGoingList),
                String.format("startNoneEvent must have at least valid one outGoing, but go none! Please contact the administrator to check flow definition[flowDefinitionKey=%s]", getFlowDefinitionKey()));
        return nextOutGoingList;
    }

}
