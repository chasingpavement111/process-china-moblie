package com.chinamobile.zj.flowProcess.service.busi.interfaces;

import com.baomidou.mybatisplus.service.IService;
import com.chinamobile.zj.flowProcess.bo.dto.CreateFlowOrderDTO;
import com.chinamobile.zj.flowProcess.bo.dto.OrderInfoResultDTO;
import com.chinamobile.zj.flowProcess.bo.dto.StartFlowOrderDTO;
import com.chinamobile.zj.flowProcess.bo.input.CancelResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.CompleteResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrder;

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
     *
     * @param createOrderDTO
     * @return orderUuid
     */
    String create(CreateFlowOrderDTO createOrderDTO);

    String start(StartFlowOrderDTO startOrderDTO);

    OrderInfoResultDTO info(String orderUuid, Boolean includeInvalidInstance);

    /**
     * 强制取消步骤实例（保证在执行到某个操作后，才可以进行强制取消动作。需检查用户权限、execute执行后设置流转控制变量）。
     * 实例取消成功后，整个工单也被同步强制取消
     *
     * @param inputBO
     * @return
     */
    String cancelResourceInstance(CancelResourceInputBO inputBO);

    String reviewResourceInstance(ReviewResourceInputBO inputBO);

    String completeResourceInstance(CompleteResourceInputBO inputBO);
}
