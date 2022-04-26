package com.chinamobile.zj.flowProcess.service.busi.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.entity.user.WgUserInfo;
import com.chinamobile.zj.flowProcess.bo.dto.*;
import com.chinamobile.zj.flowProcess.bo.input.CompleteResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.entity.WbFlowDefinition;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrder;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrderDO;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.OrderStatusEnum;
import com.chinamobile.zj.flowProcess.mapper.WbFlowOrderMapper;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowDefinitionService;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowOrderService;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowResourceInstanceService;
import com.chinamobile.zj.flowProcess.service.definition.FlowDefinitionResourceService;
import com.chinamobile.zj.flowProcess.service.definition.FlowService;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.service.interfaces.user.WgUserInfoService;
import com.chinamobile.zj.util.DateUtil;
import com.chinamobile.zj.util.JsonConvertUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程工单表 服务实现类
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
@Service
@Primary
public class WbFlowOrderServiceImpl extends ServiceImpl<WbFlowOrderMapper, WbFlowOrder> implements WbFlowOrderService {

    @Autowired
    private WbFlowOrderMapper orderMapper;

    @Autowired
    private WbFlowDefinitionService definitionService;

    @Autowired
    private WbFlowResourceInstanceService instanceService;

    @Autowired
    private WgUserInfoService userInfoService;

    @Autowired
    private ApplicationContext context;

    @Resource(name = "flowDefinitionServiceMap")
    private Map<String, FlowDefinitionResourceService> flowDefinitionServiceMap;

    /**
     * 服务重启后，手动注入的bean丢失。需手动重新注入
     * 所有未结束工单的flowService\BaseUserTaskService
     * <p>
     * todo zj 性能优化：工单在一定时间不结束，由系统主动结束。用户可重新开启工单
     */
    @PostConstruct
    public void injectBeans() {
        List<WbFlowOrder> orderEntityList = getUnfinishedOrder();
        if (CollectionUtils.isEmpty(orderEntityList)) {
            return;
        }
        orderEntityList.forEach(orderEntity -> createFlowServiceBean(orderEntity));
        instanceService.injectBeans(orderEntityList);
    }

    private List<WbFlowOrder> getUnfinishedOrder() {
        return orderMapper.getUnfinishedOrder();
    }

    @Transactional
    @Override
    public String create(CreateFlowOrderDTO createOrderDTO, boolean startNow) {
        ParamException.isTrue(Objects.isNull(createOrderDTO), "inputParam[createOrderDTO] should not be null!");
        // 所有人都可以创建工单。只校验用户有效性
        Optional<WgUserInfo> userInfoEntityOpt = userInfoService.getByUserCRMId(createOrderDTO.getCreatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(userInfoEntityOpt.isPresent()),
                String.format("invalid creatorId: [%s], user not found!", createOrderDTO.getCreatorId()));

        // 创建总工单
        WbFlowOrderDO orderEntityDO = new WbFlowOrderDO();
        BeanUtils.copyProperties(createOrderDTO, orderEntityDO);

        orderEntityDO.setOrderUuid(UUID.randomUUID().toString());
        orderEntityDO.setCreateTime(DateUtil.format(new Date(), DateUtil.DATE_TIME_REGEX));
        orderEntityDO.setStatus(OrderStatusEnum.READY.getNameEn());

        WbFlowDefinition flowDefinitionEntity = definitionService.getNewestByFlowDefinitionKey(createOrderDTO.getFlowDefinitionKey());
        orderEntityDO.setFlowDefinitionVersion(flowDefinitionEntity.getFlowDefinitionVersion());

        if (MapUtils.isEmpty(orderEntityDO.getInputVariablesMap())) {
            // 避免影响后面代码的NPE报错。要求入参非空，自己内部设置好了，不对外部输入做非空限制
            orderEntityDO.setInputVariablesMap(Collections.EMPTY_MAP);
        }

        orderMapper.insert(orderEntityDO);

        createFlowServiceBean(orderEntityDO); // todo zj 下面代码若报错，则删除.. 先test确定下面跑错后，不会生成order记录

        // 创建流程节点
        instanceService.create(orderEntityDO);

        return orderEntityDO.getOrderUuid();
    }

    @Override
    public String start(StartFlowOrderDTO startOrderDTO) {
        ParamException.isTrue(Objects.isNull(startOrderDTO), "inputParam[startOrderDTO] should not be null!");
        // 只校验用户有效性
        Optional<WgUserInfo> userInfoEntityOpt = userInfoService.getByUserCRMId(startOrderDTO.getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(userInfoEntityOpt.isPresent()),
                String.format("invalid creatorId: [%s], user not found!", startOrderDTO.getOperatorId()));

        WbFlowOrderDO orderEntityDO = getByUuid(startOrderDTO.getOrderUuid());
        // 只有工单处于非终态时，允许被启动。processing时允许重复启动
        ParamException.isTrue(BooleanUtils.isNotTrue(OrderStatusEnum.UNFINISHED_STATUS_NAME_EN_LIST.contains(orderEntityDO.getStatus())),
                String.format("order[orderUuid=%s] status is [%s], only in [%s] status allowed to be started",
                        orderEntityDO.getOrderUuid(), orderEntityDO.getStatus(),
                        OrderStatusEnum.UNFINISHED_STATUS_NAME_EN_LIST.stream().collect(Collectors.joining(","))));

        // 只有工单创建人，可以启动工单。
        ParamException.isTrue(!orderEntityDO.getCreatorId().equals(startOrderDTO.getOperatorId()),
                String.format("user[CRMId=%s] don't have operation right, order can only be started by its creator[CRMId=%s]",
                        startOrderDTO.getOperatorId(), orderEntityDO.getCreatorId()));

        updateOrderAfterOperation(orderEntityDO, startOrderDTO.getOperatorId(), startOrderDTO.getInputVariablesMap(), OrderStatusEnum.PROCESSING);
        return startOrderDTO.getOrderUuid();
    }

    @Override
    public OrderInfoResultDTO info(String orderUuid) {
        OrderInfoResultDTO resultDTO = new OrderInfoResultDTO();

        WbFlowOrder orderEntity = getByUuid(orderUuid);
        BeanUtils.copyProperties(orderEntity, resultDTO);

        List<OrderResourceInstanceInfoResultDTO> instanceList = instanceService.getExecutionHistoryByOrderUuid(orderUuid);
        resultDTO.setInstanceExecutionHistory(instanceList);
        if (instanceList.size() > 0) {
            List<OrderResourceInstanceInfoResultDTO> unfinishedInstanceList = instanceList.stream()
                    .filter(instanceDTO -> OrderInstanceStatusEnum.UNFINISHED_STATUS_NAME_EN_LIST.contains(instanceDTO.getStatus())) // todo zj 待区别 ready、processing,, 还是应该只过了出processing状态的步骤实例
                    .collect(Collectors.toList());
            List<String> roleNameList = new ArrayList<>();
            unfinishedInstanceList.forEach(instanceDTO -> {
                BaseUserTaskService userTaskServiceBean = instanceService.getResourceInstanceBean(instanceDTO.getResourceInstanceUuid());
                List<String> instanceRoleNameList = new ArrayList<>(userTaskServiceBean.supportedOperatorRoleMap().values());
                roleNameList.addAll(instanceRoleNameList);
            });
            resultDTO.setCurrentOperatorRoleList(roleNameList);
        }
        return resultDTO;
    }

    @Transactional
    @Override
    public String reviewResourceInstance(ReviewResourceInputBO inputBO) {
        // todo zj 可以与 completeResourceInstance 合并
        ParamException.isTrue(Objects.isNull(inputBO), "inputParam[executionResourceInputBO] should not be null!");
        WbFlowOrderDO orderEntityDO = getByUuid(inputBO.getOrderUuid());
        // 仅有“processing”状态的工单，允许进行变更操作。若工单状态为ready, 可以startOrder成功后重试；若工单状态为终态（finished\cancelled），这不再支持任何变更操作、
        ParamException.isTrue(!OrderStatusEnum.PROCESSING.getNameEn().equals(orderEntityDO.getStatus()),
                String.format("order status is [%s], not allowed to do change operation.", orderEntityDO.getStatus()));

        FinishResourceResultDTO resultDTO = instanceService.review(orderEntityDO, inputBO);
        updateOrderAfterOperation(orderEntityDO, inputBO.getOperatorId(), resultDTO.getOutputVariablesMap(), null);
        // todo zj 工单结束，更新工单状态为finished
        return resultDTO.getInstanceUuid();
    }

    @Transactional
    @Override
    public String completeResourceInstance(CompleteResourceInputBO inputBO) {
        ParamException.isTrue(Objects.isNull(inputBO), "inputParam[executionResourceInputBO] should not be null!");
        WbFlowOrderDO orderEntityDO = getByUuid(inputBO.getOrderUuid());
        // 仅有“processing”状态的工单，允许进行变更操作。若工单状态为ready, 可以startOrder成功后重试；若工单状态为终态（finished\cancelled），这不再支持任何变更操作、
        ParamException.isTrue(!OrderStatusEnum.PROCESSING.getNameEn().equals(orderEntityDO.getStatus()),
                String.format("order status is [%s], not allowed to do change operation.", orderEntityDO.getStatus()));

        FinishResourceResultDTO resultDTO = instanceService.complete(orderEntityDO, inputBO); // todo zj 不同入参的完成接口，可以拆分成具体业务接口方便限制入参？？？
        updateOrderAfterOperation(orderEntityDO, inputBO.getOperatorId(), resultDTO.getOutputVariablesMap(), null);
        // todo zj 工单结束
        return resultDTO.getInstanceUuid();
    }

    private void updateOrderAfterOperation(WbFlowOrder orderEntity, String operatorId, Map<String, Object> outputVariablesMapOfInstance, OrderStatusEnum orderStatusEnum) {
        WbFlowOrder updateOrderEntity = new WbFlowOrder();
        updateOrderEntity.setOrderUuid(orderEntity.getOrderUuid());
        updateOrderEntity.setUpdateOperatorId(operatorId);
        updateOrderEntity.setUpdateTime(DateUtil.format(new Date(), DateUtil.DATE_TIME_REGEX));
        if (Objects.nonNull(orderStatusEnum)) {
            updateOrderEntity.setStatus(orderStatusEnum.getNameEn());
        }

        Map<String, Object> variablesMap = JsonConvertUtil.parseToParameterizedType(orderEntity.getInputVariables(), new TypeReference<Map<String, Object>>() {
        });
        if (MapUtils.isNotEmpty(outputVariablesMapOfInstance)) {
            variablesMap.putAll(outputVariablesMapOfInstance);
        }
        updateOrderEntity.setInputVariables(JsonConvertUtil.toJsonString(variablesMap));

        int updateCount = orderMapper.updateOrderAfterOperation(updateOrderEntity);
        InternalException.isTrue(1 != updateCount,
                String.format("updateCount=[%s], should be number [1]. please contact the administrator", updateCount));
    }

    private WbFlowOrderDO getByUuid(String orderUuid) {
        ParamException.isTrue(StringUtils.isBlank(orderUuid), "inputParam[orderUuid] should not be blank!");
        WbFlowOrderDO entity = orderMapper.getByUuid(orderUuid);
        ParamException.isTrue(Objects.isNull(entity),
                String.format("invalid orderUuid: [%s], order not found!", orderUuid));
        return entity;
    }

    /**
     * 为每个工单，创建一个flowService Bean, 便于统一管理（获取、销毁）
     */
    private void createFlowServiceBean(WbFlowOrder orderEntity) {
        FlowDefinitionResourceService flowDefinitionService = flowDefinitionServiceMap.get(orderEntity.getFlowDefinitionKey());
        FlowService service = new FlowService(flowDefinitionService, new HashMap<>());
        String beanName = "orderUuid-" + orderEntity.getOrderUuid();


        // 获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        // 动态注册bean
        // todo zj 等待order 执行结束，主动开启线程，异步销毁bean（销毁不成功不影响结果。所以启动线程执行）
        defaultListableBeanFactory.registerSingleton(beanName, service);
        defaultListableBeanFactory.autowireBean(service); // 为Bean注入对象
        System.out.printf("inject Bean[%s]\n", beanName); // todo zj 待刪除
    }
}
