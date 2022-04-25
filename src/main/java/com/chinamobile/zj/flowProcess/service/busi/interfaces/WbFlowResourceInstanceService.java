package com.chinamobile.zj.flowProcess.service.busi.interfaces;

import com.baomidou.mybatisplus.service.IService;
import com.chinamobile.zj.flowProcess.bo.dto.FinishResourceResultDTO;
import com.chinamobile.zj.flowProcess.bo.dto.OrderResourceInstanceInfoResultDTO;
import com.chinamobile.zj.flowProcess.bo.input.CompleteResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrder;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrderDO;
import com.chinamobile.zj.flowProcess.entity.WbFlowResourceInstance;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;

import java.util.List;

/**
 * <p>
 * 流程步骤实例表 服务类
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
public interface WbFlowResourceInstanceService extends IService<WbFlowResourceInstance> {

    void injectBeans(List<WbFlowOrder> orderEntityList);

    FinishResourceResultDTO complete(WbFlowOrderDO orderEntity, CompleteResourceInputBO inputBO);

    List<OrderResourceInstanceInfoResultDTO> getExecutionHistoryByOrderUuid(String orderUuid);

    BaseUserTaskService getResourceBeanByResourceDefinitionKey(String resourceDefinitionKey);

    List<String> create(WbFlowOrderDO orderEntity);

    String review(WbFlowOrderDO orderEntity, ReviewResourceInputBO inputBO);
}
