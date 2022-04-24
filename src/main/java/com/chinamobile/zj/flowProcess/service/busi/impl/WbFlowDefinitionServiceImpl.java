package com.chinamobile.zj.flowProcess.service.busi.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.entity.WbFlowDefinition;
import com.chinamobile.zj.flowProcess.mapper.WbFlowDefinitionMapper;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowDefinitionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 流程定义表 服务实现类
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
@Service
@Primary
public class WbFlowDefinitionServiceImpl extends ServiceImpl<WbFlowDefinitionMapper, WbFlowDefinition> implements WbFlowDefinitionService {

    @Autowired
    private WbFlowDefinitionMapper definitionMapper;

    @Override
    public WbFlowDefinition getNewestByFlowDefinitionKey(String flowDefinitionKey) {
        ParamException.isTrue(StringUtils.isBlank(flowDefinitionKey), "inputParam[flowDefinitionKey] should not be blank!");
        List<WbFlowDefinition> flowDefinitionEntityList = definitionMapper.listByFlowDefinitionKey(flowDefinitionKey); // todo zj sql改成只获取第一个
        ParamException.isTrue(CollectionUtils.isEmpty(flowDefinitionEntityList),
                String.format("invalid flowDefinitionKey: [%s], flowDefinition not found!", flowDefinitionKey));
        return flowDefinitionEntityList.get(0);
    }
}
