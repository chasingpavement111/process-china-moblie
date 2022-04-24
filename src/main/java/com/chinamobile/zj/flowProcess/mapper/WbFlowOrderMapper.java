package com.chinamobile.zj.flowProcess.mapper;

import com.chinamobile.zj.flowProcess.entity.WbFlowOrder;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
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

    WbFlowOrder getByUuid(@Param("orderUuid") String orderUuid);

    int updateOrderAfterFinishInstance(@Param("entity") WbFlowOrder updateOrderEntity);

    List<WbFlowOrder> getUnfinishedOrder();
}
