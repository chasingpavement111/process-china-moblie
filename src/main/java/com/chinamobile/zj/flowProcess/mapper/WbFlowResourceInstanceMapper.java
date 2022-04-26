package com.chinamobile.zj.flowProcess.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.chinamobile.zj.flowProcess.entity.WbFlowResourceInstance;
import com.chinamobile.zj.flowProcess.entity.WbFlowResourceInstanceDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

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

    List<WbFlowResourceInstanceDO> getInstanceByOrderUuidList(@Param("orderUuidList") List<String> orderUuidList);

    int invalidateInstance(@Param("instanceUuidList") Set<String> setOfInvalidInstanceUuid);
}
