package com.chinamobile.zj.flowProcess.service.busi.interfaces;

import com.chinamobile.zj.flowProcess.entity.WbFlowDefinition;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 流程定义表 服务类
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
public interface WbFlowDefinitionService extends IService<WbFlowDefinition> {

    WbFlowDefinition getNewestByFlowDefinitionKey(String flowDefinitionKey);
}
