package com.chinamobile.zj.flowProcess.service.resource;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class BaseExclusiveGatewayService extends BaseResourceService {

    @Autowired
    private BaseSequenceFlowService baseSequenceFlowService;

    public ResourceDefinitionBO getDirectNextOutGoingResource(ResourceDefinitionBO exclusiveGatewayResourceDefinitionBO, Map<String, Object> outputVariablesMap) {
        ResourceDefinitionBO finalNextOutGoing = null;
        for (ResourceDefinitionBO conditionSequenceDefinitionBO : super.getResourceDefinitionBO().getOutGoing()) {// 连线元素应该有且只有一个出口，错误可能原因：json文件被恶意篡改（图上也画不出两个出口，故由图导出的结果不会有问题）
            // 遍历每一种分支是否可行
            ResourceDefinitionBO nextOutGoing = baseSequenceFlowService.getDirectNextOutGoingResource(conditionSequenceDefinitionBO, outputVariablesMap);
            if (Objects.nonNull(nextOutGoing)) {
                // 校验是否有唯一的下一跳（若多个一样的流转条件，则下一跳无法确定 --> 这是流程定义的问题）
                InternalException.isTrue(Objects.nonNull(finalNextOutGoing),
                        String.format("invalid flow definition, cannot determine next step. Please contact the administrator to check flow definition!"));
                finalNextOutGoing = nextOutGoing;
            }
        }
        // 所有条件都不成立时，finalNextOutGoing==null. 可能情况：有多个入口，存在一到多个入口还未执行完成，故还不能确定本网关的下一跳出口
        return finalNextOutGoing;
    }

}
