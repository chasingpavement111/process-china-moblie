package com.chinamobile.zj.flowProcess.service.resource;

import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.entity.WbFlowResourceInstanceDO;
import lombok.Data;

@Data
public class BaseResourceService {

    private String flowDefinitionKey;

    private ResourceDefinitionBO resourceDefinitionBO;

    private WbFlowResourceInstanceDO resourceInstance;

//    public BaseResourceService(String flowDefinitionKey, ResourceDefinitionBO resourceDefinitionBO) { todo zj 待取消注释
//        ParamException.isTrue(StringUtils.isBlank(flowDefinitionKey), "inputParam[flowDefinitionKey] should not be blank!");
//        ParamException.isTrue(Objects.isNull(resourceDefinitionBO), "inputParam[resourceDefinitionBO] should not be null!");
//        this.flowDefinitionKey = flowDefinitionKey;
//        this.resourceDefinitionBO = resourceDefinitionBO;
//    }

    //    public BaseResourceService(ResourceDefinitionBO resourceDefinitionBO) {
//        this.resourceDefinitionBO = resourceDefinitionBO;
//    }
//
//    public ResourceDefinitionBO getResourceDefinitionBO() {
//        return resourceDefinitionBO;
//    }
//
//    public WbFlowResourceInstance getResourceInstance() {
//        return resourceInstance;
//    }
//
//    public void setResourceInstance(WbFlowResourceInstance resourceInstance) {
//        this.resourceInstance = resourceInstance;
//    }
}
