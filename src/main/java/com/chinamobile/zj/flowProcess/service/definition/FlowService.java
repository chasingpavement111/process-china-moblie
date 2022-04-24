package com.chinamobile.zj.flowProcess.service.definition;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.flowProcess.bo.definition.FlowProcessDefinitionBO;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.util.JsonConvertUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowService {

    private FlowDefinitionResourceService flowDefinitionService;

    /**
     * 该工单对应的所有userTask bean对象。便于统一销毁
     */
    private Map<String, BaseUserTaskService> userTaskServiceMap;

    public FlowService(FlowDefinitionResourceService flowDefinitionService, Map<String, BaseUserTaskService> userTaskServiceMap) {
        this.flowDefinitionService = flowDefinitionService;
        this.userTaskServiceMap = userTaskServiceMap;
    }

    public FlowDefinitionResourceService getFlowDefinitionService() {
        return flowDefinitionService;
    }

    public Map<String, BaseUserTaskService> getUserTaskServiceMap() {
        return userTaskServiceMap;
    }
}
