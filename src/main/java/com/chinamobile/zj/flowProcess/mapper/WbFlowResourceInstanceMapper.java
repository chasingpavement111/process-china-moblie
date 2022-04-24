package com.chinamobile.zj.flowProcess.mapper;

import com.chinamobile.zj.flowProcess.entity.WbFlowResourceInstance;
import com.chinamobile.zj.flowProcess.entity.WbFlowResourceInstanceDO;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 流程步骤实例表 Mapper 接口
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
public interface WbFlowResourceInstanceMapper extends BaseMapper<WbFlowResourceInstance> {

    @Override
    Integer insert(@Param("entity") WbFlowResourceInstance entity);

    int updateForCompletion(@Param("entity") WbFlowResourceInstance updateInstanceEntity);

    int updateLatterInstance(@Param("entity") WbFlowResourceInstance updateInstanceEntity);

    List<WbFlowResourceInstanceDO> getInstanceByOrderUuid(@Param("orderUuid") String orderUuid, @Param("instanceStatus") String instanceStatus);

    WbFlowResourceInstanceDO getInstanceByInstanceUuid(@Param("resourceInstanceUuid") String resourceInstanceUuid);

    List<WbFlowResourceInstance> getInstanceByOrderUuidList(@Param("orderUuidList") List<String> orderUuidList);
}
