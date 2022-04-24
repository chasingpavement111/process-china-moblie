package com.chinamobile.zj.flowProcess.mapper;

import com.chinamobile.zj.flowProcess.entity.WbFlowDefinition;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 流程定义表 Mapper 接口
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
public interface WbFlowDefinitionMapper extends BaseMapper<WbFlowDefinition> {

    List<WbFlowDefinition> listByFlowDefinitionKey(@Param("flowDefinitionKey") String flowDefinitionKey);
}
