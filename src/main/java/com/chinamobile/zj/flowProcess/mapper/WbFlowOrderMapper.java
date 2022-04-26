package com.chinamobile.zj.flowProcess.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrder;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrderDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 流程工单表 Mapper 接口
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
public interface WbFlowOrderMapper extends BaseMapper<WbFlowOrder> {

    @Override
    Integer insert(@Param("entity") WbFlowOrder wbFlowOrder);

    WbFlowOrderDO getByUuid(@Param("orderUuid") String orderUuid);

    int updateOrderAfterOperation(@Param("entity") WbFlowOrder updateOrderEntity);

    List<WbFlowOrder> getUnfinishedOrder();
}
