package com.chinamobile.zj.flowProcess.service.resource;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.util.SpELUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class BaseSequenceFlowService extends BaseResourceService{

    public ResourceDefinitionBO getDirectNextOutGoingResource(ResourceDefinitionBO conditionSequenceDefinitionBO, Map<String, Object> outputVariablesMap) {
        Boolean spELVal = getConditionResult(conditionSequenceDefinitionBO, outputVariablesMap);
        if (Objects.nonNull(spELVal) && Boolean.FALSE.equals(spELVal)) {
            // 条件不成立，不走该分支
            return null;
        }
        // 报错原因：流程定义有问题，连线未定义出口。需修改流程，为该连线添加下一跳目的资源。可能流程看着是对的，但是连线没连上。
        InternalException.isTrue(CollectionUtils.isEmpty(conditionSequenceDefinitionBO.getOutGoing()), // todo zj 报错增加 flowDefinitionId=%s -> Please contact the administrator to check flow definition[flowDefinitionId=%s]
                String.format("outgoing of resourceType=[%s] is empty, but it should have one and only one outgoing resource. Please contact the administrator to check flow definition", conditionSequenceDefinitionBO.getStencilEnum().getId()));
        return conditionSequenceDefinitionBO.getOutGoing().get(0);
    }

    private Boolean getConditionResult(ResourceDefinitionBO sequenceDefinitionBO, Map<String, Object> outputVariablesMap) {
        String spEL;
        if (Objects.isNull(sequenceDefinitionBO.getProperties().getConditionsequenceflow())) {
            spEL = null;
        } else {
            spEL = sequenceDefinitionBO.getProperties().getConditionsequenceflow().getExpression().getStaticValue();
        }
        return SpELUtil.getBooleanValue(spEL, outputVariablesMap);
    }

}
