package com.chinamobile.zj.flowProcess.service.busi.interfaces;

import com.chinamobile.zj.flowProcess.bo.input.CompleteResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.dto.CreateFlowOrderDTO;
import com.chinamobile.zj.flowProcess.bo.dto.OrderInfoResultDTO;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrder;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 流程工单表 服务类
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
public interface WbFlowOrderService extends IService<WbFlowOrder> {

    /**
     * 创建流程工单
     * @param createOrderDTO
     * @param startNow 流转是否立即开始执行： true(立即启动)\false(不，需要调用启动接口，使流程开始执行)
     * @return orderUuid
     */
    String create(CreateFlowOrderDTO createOrderDTO, boolean startNow);

    OrderInfoResultDTO info(String orderUuid);

    String completeResourceInstance(CompleteResourceInputBO inputBO);

    String reviewResourceInstance(ReviewResourceInputBO inputBO);
}
