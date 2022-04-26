package com.chinamobile.zj.flowProcess.service.busi.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.entity.user.WgUserInfo;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.bo.dto.FinishResourceResultDTO;
import com.chinamobile.zj.flowProcess.bo.dto.OrderResourceInstanceInfoResultDTO;
import com.chinamobile.zj.flowProcess.bo.input.BaseOperateResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.CompleteResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrder;
import com.chinamobile.zj.flowProcess.entity.WbFlowOrderDO;
import com.chinamobile.zj.flowProcess.entity.WbFlowResourceInstance;
import com.chinamobile.zj.flowProcess.entity.WbFlowResourceInstanceDO;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import com.chinamobile.zj.flowProcess.mapper.WbFlowResourceInstanceMapper;
import com.chinamobile.zj.flowProcess.service.LimitOperatorRole;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowResourceInstanceService;
import com.chinamobile.zj.flowProcess.service.definition.FlowDefinitionResourceService;
import com.chinamobile.zj.flowProcess.service.definition.FlowService;
import com.chinamobile.zj.flowProcess.service.resource.BaseStartNoneEventService;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.service.interfaces.user.WgUserInfoService;
import com.chinamobile.zj.util.DateUtil;
import com.chinamobile.zj.util.JsonConvertUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程步骤实例表 服务实现类
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
@Service
@Primary
public class WbFlowResourceInstanceServiceImpl extends ServiceImpl<WbFlowResourceInstanceMapper, WbFlowResourceInstance> implements WbFlowResourceInstanceService {

    @Autowired
    private WbFlowResourceInstanceMapper instanceMapper;

    @Autowired
    private WgUserInfoService userInfoService;

    @Autowired
    private ApplicationContext context;

    @Resource(name = "flowDefinitionServiceMap")
    private Map<String, FlowDefinitionResourceService> flowDefinitionServiceMap;

//    @Resource(name = "userTaskServiceMap")
//    private Map<String, BaseUserTaskService> userTaskServiceMap;

    @Resource(name = "userTaskServiceClassMap")
    private Map<String, Class<? extends BaseUserTaskService>> userTaskServiceClassMap;

    @Autowired
    private BaseStartNoneEventService startNoneEventService;

    @Override
    public void injectBeans(List<WbFlowOrder> orderEntityList) {
        if (CollectionUtils.isEmpty(orderEntityList)) {
            return;
        }
        List<String> orderUuidList = orderEntityList.stream().map(WbFlowOrder::getOrderUuid).collect(Collectors.toList());
        List<WbFlowResourceInstanceDO> instanceDOList = getInstanceByOrderUuidList(orderUuidList);
        Map<String, List<WbFlowResourceInstanceDO>> orderUuidToInstanceDOList = instanceDOList.stream().collect(Collectors.groupingBy(WbFlowResourceInstance::getOrderUuid));
        orderUuidToInstanceDOList.forEach((orderUuid, subInstanceDOList) -> {
            subInstanceDOList.forEach(instanceDO -> {
                // 若步骤未执行结束，outputVariables为空。只能通过入参inputVariables进行初始化。否则应根据outputVariables, 因为包含了所有信息
                String variablesStr = StringUtils.isNotBlank(instanceDO.getOutputVariables()) ? instanceDO.getOutputVariables() : instanceDO.getInputVariables();
                createAndInjectUserTaskServiceBean(orderUuid, variablesStr, Collections.singletonList(instanceDO));
            });
        });
    }

    @Transactional
    @Override
    public List<String> create(WbFlowOrderDO orderEntityDO) {
        ParamException.isTrue(Objects.isNull(orderEntityDO), "inputParam[orderEntityDO] should not be null!");
        ParamException.isTrue(StringUtils.isBlank(orderEntityDO.getOrderUuid()), "inputParam[orderEntityDO.orderUuid] should not be blank!");

        List<ResourceDefinitionBO> resourceDefinitionBOList = getStartingUserTaskResource(orderEntityDO, orderEntityDO.getInputVariablesMap());
        List<String> uuidListOfOutGoingInstanceEntity = createOutGoings(orderEntityDO, null, resourceDefinitionBOList, orderEntityDO.getInputVariablesMap());

        return uuidListOfOutGoingInstanceEntity;
    }

    @Transactional
    @Override
    public FinishResourceResultDTO review(WbFlowOrderDO orderEntity, ReviewResourceInputBO inputBO) {
        FinishResourceResultDTO resultDTO = operation(orderEntity, inputBO);
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(inputBO.getOperationResult())) {
            // 若为审核驳回，需要更新相关前序instance的valid属性
            List<String> instanceUuidOfInstanceToBeReplacedList = resultDTO.getOutGoingInstanceUuidList();

//
//            Map<String, BaseUserTaskService> processingInstanceDefinitionKeyToBean = new HashMap<>();
//            List<WbFlowResourceInstanceDO> instanceDOList = getInstanceByOrderUuid(orderEntity.getOrderUuid(), null);
//            instanceDOList.forEach(instanceDO -> {
//                if (instanceUuidOfInstanceToBeReplacedList.contains(instanceDO.getResourceInstanceUuid())) {
//                    BaseUserTaskService userTaskServiceBean = getResourceInstanceBean(instanceDO.getResourceInstanceUuid());
//                    processingInstanceDefinitionKeyToBean.put(instanceDO.getFlowResourceDefinitionKey(), userTaskServiceBean);
//
//                }
//            });
//
//            List<WbFlowResourceInstance> invalidInstanceList = new ArrayList<>();
//            instanceDOList.forEach(instanceDO -> {
//                BaseUserTaskService processingUserTaskServiceBean = processingInstanceDefinitionKeyToBean.get(instanceDO.getFlowResourceDefinitionKey());
//                if (Objects.nonNull(processingUserTaskServiceBean) &&
//                        BooleanUtils.isNotTrue(instanceDO.getResourceInstanceUuid().equals(processingUserTaskServiceBean.getResourceInstance().getResourceInstanceUuid()))) {
//                    // 找到需要"直接"废弃的步骤实例
//                    instanceDO.setValid(false);
//                    invalidInstanceList.add(instanceDO);
//                }
//            });

            Map<String, WbFlowResourceInstanceDO> definitionKeyToInstanceDOOfOutGoings = new HashMap<>();
            List<WbFlowResourceInstanceDO> instanceDOListOfOrder = getInstanceByOrderUuid(orderEntity.getOrderUuid(), null);
            instanceDOListOfOrder.forEach(instanceDO -> {
                if (instanceUuidOfInstanceToBeReplacedList.contains(instanceDO.getResourceInstanceUuid())) {
                    definitionKeyToInstanceDOOfOutGoings.put(instanceDO.getFlowResourceDefinitionKey(), instanceDO);

                }
            });

            // 找到需要"直接"废弃的步骤实例
            List<WbFlowResourceInstanceDO> invalidDirectInstanceDOList = new ArrayList<>();
            Map<String, String> instanceUuidOfInvalidStartToValidOutGoing = new HashMap<>();

            instanceDOListOfOrder.forEach(instanceDO -> {
                if (!instanceDO.getValid()) {
                    // 忽略已废弃实例
                    return;
                }
                WbFlowResourceInstanceDO outGoingInstanceDO = definitionKeyToInstanceDOOfOutGoings.get(instanceDO.getFlowResourceDefinitionKey());
                if (Objects.nonNull(outGoingInstanceDO) &&
                        BooleanUtils.isNotTrue(instanceDO.getResourceInstanceUuid().equals(outGoingInstanceDO.getResourceInstanceUuid()))) {
                    instanceDO.setValid(false);
                    invalidDirectInstanceDOList.add(instanceDO);
                    instanceUuidOfInvalidStartToValidOutGoing.put(instanceDO.getResourceInstanceUuid(), outGoingInstanceDO.getResourceInstanceUuid());
                }
            });
            // 找到“中间环节”的需要废弃的实例：直接废弃的步骤实例 -> 新创建的outGoing步骤实例 之间的节点都将被废弃
            Set<String> setOfInvalidInstanceUuid = new HashSet<>(instanceUuidOfInvalidStartToValidOutGoing.keySet());
            Map<String, WbFlowResourceInstanceDO> resourceUuidToSelfOfOrder = instanceDOListOfOrder.stream().collect(Collectors.toMap(WbFlowResourceInstanceDO::getResourceInstanceUuid, Function.identity()));
            invalidDirectInstanceDOList.forEach(invalidDirectInstanceDO -> {
                String instanceUuidOfEndValidOutGoing = instanceUuidOfInvalidStartToValidOutGoing.get(invalidDirectInstanceDO.getResourceInstanceUuid());

                Set<String> subSet = getAllInstanceUuidInBetween(resourceUuidToSelfOfOrder, invalidDirectInstanceDO.getResourceInstanceUuid(), instanceUuidOfEndValidOutGoing);
                setOfInvalidInstanceUuid.addAll(subSet);
            });

//            setOfInvalidInstanceUuid.forEach(invalidInstanceUuid -> {
////                destroyInstanceBean(invalidInstanceUuid); // todo zj 暂时不做，可能bean 还有用
//
//            });

            updateInvalidInstance(setOfInvalidInstanceUuid);
        }
        return resultDTO;
    }

    /**
     * 获取 startInstanceUuid -> endInstanceUuid 之间的所有实例，不包括start\end实例
     *
     * @param resourceUuidToSelfOfOrder 整个工单下的所有步骤实例。不区分执行状态、有效状态
     * @param startInstanceUuid         start实例
     * @param endInstanceUuid           end实例
     */
    private Set<String> getAllInstanceUuidInBetween(Map<String, WbFlowResourceInstanceDO> resourceUuidToSelfOfOrder, String startInstanceUuid, String endInstanceUuid) {
        WbFlowResourceInstanceDO startInstanceDo = resourceUuidToSelfOfOrder.get(startInstanceUuid);
        if (Objects.isNull(startInstanceDo)) {
            return Collections.EMPTY_SET;
        }
        if (CollectionUtils.isEmpty(startInstanceDo.getLatterResourceInstanceUuidList())) {
            // 并行步骤。可能存在没有闭环到 endInstanceUuid, 但是需要被invalid的节点
            return Collections.EMPTY_SET;
        }
        Set<String> setOfInstanceUuidInBetween = new HashSet<>();
        startInstanceDo.getLatterResourceInstanceUuidList().forEach(latterInstanceUuid -> {
            WbFlowResourceInstanceDO latterInstanceDO = resourceUuidToSelfOfOrder.get(latterInstanceUuid);
            if (Objects.nonNull(latterInstanceDO) && BooleanUtils.isNotTrue(endInstanceUuid.equals(latterInstanceDO.getResourceInstanceUuid()))) {
                setOfInstanceUuidInBetween.add(latterInstanceDO.getResourceInstanceUuid());

                // 往后找，直到与新创建的有效outGoingInstance闭环
                Set<String> subSet = getAllInstanceUuidInBetween(resourceUuidToSelfOfOrder, latterInstanceUuid, endInstanceUuid);
                setOfInstanceUuidInBetween.addAll(subSet);
            }
        });
        return setOfInstanceUuidInBetween;
    }

    @Transactional
    @Override
    public FinishResourceResultDTO complete(WbFlowOrderDO orderEntity, CompleteResourceInputBO inputBO) {
        return operation(orderEntity, inputBO);
    }

    public FinishResourceResultDTO operation(WbFlowOrderDO orderEntity, BaseOperateResourceInputBO inputBO) {
        Optional<WgUserInfo> userInfoEntityOpt = userInfoService.getByUserCRMId(inputBO.getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(userInfoEntityOpt.isPresent()),
                String.format("invalid operatorId: [%s], user not found!", inputBO.getOperatorId()));
        String instanceUuid = inputBO.getResourceInstanceUuid();
        WbFlowResourceInstanceDO processingInstanceEntity = getInstanceByInstanceUuid(instanceUuid);
        ParamException.isTrue(BooleanUtils.isNotTrue(OrderInstanceStatusEnum.UNFINISHED_STATUS_NAME_EN_LIST.contains(processingInstanceEntity.getStatus())),
                String.format("instance[instanceUuid=%s] status is [%s], only in [%s] status allowed to be completed",
                        processingInstanceEntity.getResourceInstanceUuid(), processingInstanceEntity.getStatus(),
                        OrderInstanceStatusEnum.UNFINISHED_STATUS_NAME_EN_LIST.stream().collect(Collectors.joining(","))));

        // complete -> status
        BaseUserTaskService concreteUserTaskService = getResourceInstanceBean(processingInstanceEntity.getResourceInstanceUuid());
        BeanUtils.copyProperties(inputBO, concreteUserTaskService); // 可直接覆盖。inputBO的属性集合就是为了赋值到 BaseUserTaskService中
//        concreteUserTaskService.setOperatorId(inputBO.getOperatorId());
//        concreteUserTaskService.setOperationSnapshot(inputBO.getOperationSnapshot());
//        concreteUserTaskService.setOperationResult(inputBO.getOperationResult()); // 注意com.chinamobile.zj.flowProcess.bo.input.CompleteResourceInputBO.operationResult默认值
//        concreteUserTaskService.setOperationMessage(inputBO.getOperationMessage()); // 仅review需要赋值的属性
        {
            // 重要。用户触发后自动执行。todo zj 节点实例创建后，不会执行。目前没有执行必要
            if (concreteUserTaskService instanceof LimitOperatorRole) {
                // 若限制操作人权限，则需要做权限校验
                ((LimitOperatorRole) concreteUserTaskService).checkOperatorAccessRight();
            }
            ExecutionResult executionResult = concreteUserTaskService.execute(); // 异常直接抛出 todo zj 返回结果作用？
        }

        updateAfterExecution(instanceUuid, concreteUserTaskService);

        // update variables to order 方法返回到order后进行
        // todo zj 更新到order: inputVariable\operatorId\operatorTime 是否可利用回调函数
        Map<String, Object> inputVariablesMapOfOrder = JsonConvertUtil.parseToParameterizedType(orderEntity.getInputVariables(), new TypeReference<Map<String, Object>>() {
        });
        if (Objects.isNull(inputVariablesMapOfOrder)) {
            inputVariablesMapOfOrder = new HashMap<>();
        }
        Map<String, Object> outputVariablesMapOfInstance = concreteUserTaskService.currentUniqueOutputVariables();
        inputVariablesMapOfOrder.putAll(outputVariablesMapOfInstance);

        // enable next [one or more] userTask resource into processing status: 本节点执行完成后，可能会创建多个并行执行的下一步执行节点 todo zj 应该放在这里创建吗？ 还是异步创建？
        List<ResourceDefinitionBO> outGoingResourceDefinitionBOList = getNextOutGoingExecutionResource(orderEntity, processingInstanceEntity, inputVariablesMapOfOrder);
        List<String> uuidListOfOutGoingInstanceEntity = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(outGoingResourceDefinitionBOList)) {
            uuidListOfOutGoingInstanceEntity = createOutGoings(orderEntity, processingInstanceEntity.getResourceInstanceUuid(), outGoingResourceDefinitionBOList, inputVariablesMapOfOrder);
            // update order
            updateLatterInstance(instanceUuid, uuidListOfOutGoingInstanceEntity);
        }

        FinishResourceResultDTO resultDTO = new FinishResourceResultDTO();
        resultDTO.setInstanceUuid(instanceUuid);
        resultDTO.setOutGoingInstanceUuidList(uuidListOfOutGoingInstanceEntity);
        resultDTO.setOutputVariablesMap(outputVariablesMapOfInstance);
        return resultDTO;
    }

    @Override
    public List<OrderResourceInstanceInfoResultDTO> getExecutionHistoryByOrderUuid(String orderUuid) {
        List<WbFlowResourceInstanceDO> instanceEntityList = getInstanceByOrderUuid(orderUuid, null);
        List<OrderResourceInstanceInfoResultDTO> instanceDTOList = new ArrayList<>();
        instanceEntityList.forEach(entity -> {
            OrderResourceInstanceInfoResultDTO dto = new OrderResourceInstanceInfoResultDTO();
            BeanUtils.copyProperties(entity, dto);
            BaseUserTaskService userTaskService = getResourceInstanceBean(entity.getResourceInstanceUuid());
            dto.setFlowResourceDefinitionKeyDesc(userTaskService.getDefinitionKeyDesc());
            dto.setOperationOutputDesc(userTaskService.getOperationOutputDesc());
            instanceDTOList.add(dto);
        });
        return instanceDTOList;
    }

    public BaseUserTaskService getResourceInstanceBean(String resourceInstanceUuid) {
        // todo zj instanceDTO --获取实例-> userTask
        String instanceBeanName = "instanceUuid-" + resourceInstanceUuid;
        try {
            BaseUserTaskService userTaskService = context.getBean(instanceBeanName, BaseUserTaskService.class);
            return userTaskService;
        } catch (NoSuchBeanDefinitionException ex) {
            // bean丟失，重启下服务，重启时项目主动注入。若还是找不到，需排查代码逻辑
            String msg = String.format("BaseUserTaskService bean of instance[instanceUuid=%s] not found! Please contact the administrator!", resourceInstanceUuid);
            throw new InternalException(msg, ex);
        }
    }

    private List<ResourceDefinitionBO> getStartingUserTaskResource(WbFlowOrderDO orderEntity, Map<String, Object> inputVariablesMap) {
        ParamException.isTrue(Objects.isNull(orderEntity), "inputParam[orderEntity] should not be null!");

        String flowDefinitionKey = orderEntity.getFlowDefinitionKey();
        ParamException.isTrue(StringUtils.isBlank(flowDefinitionKey), "inputParam[flowDefinitionKey] should not be blank!");

        FlowDefinitionResourceService flowDefinitionService = flowDefinitionServiceMap.get(flowDefinitionKey);
        // 不存在flowDefinitionKey为输入值的流程定义。检查定义是否存在，或者传入值【flowDefinitionKey】有问题
        InternalException.isTrue(Objects.isNull(flowDefinitionService), "invalid flowDefinitionKey[%s], flowDefinitionService not found! Please contact the administrator!");
        ResourceDefinitionBO startNoneEventResourceBO = flowDefinitionService.getFlowProcessBo().getChildShapes().stream()
                .filter(resourceDefinitionBO -> StencilEnum.START_NONE_EVENT.equals(resourceDefinitionBO.getStencilEnum()))
                .findFirst()
                // 流程定义有误。必须定义起始节点
                .orElseThrow(() -> new InternalException(String.format("flow doesn't have any startNoneEvent! Please contact the administrator to check flow definition![flowDefinitionKey=%s]", flowDefinitionKey)));
//        BaseStartNoneEventService resourceService = userTaskServiceMap.get(StencilEnum.START_NONE_EVENT.getId()); todo zj
        List<ResourceDefinitionBO> resourceDefinitionBOList = startNoneEventService.getNextOutGoingExecutionResource(startNoneEventResourceBO, inputVariablesMap);

        return resourceDefinitionBOList;


    }

    private List<ResourceDefinitionBO> getNextOutGoingExecutionResource(WbFlowOrderDO orderEntity, WbFlowResourceInstanceDO resourceInstanceDO, Map<String, Object> inputVariablesMap) {
        ParamException.isTrue(Objects.isNull(orderEntity), "inputParam[orderEntity] should not be null!");
        ParamException.isTrue(Objects.isNull(resourceInstanceDO), "inputParam[resourceInstanceDO] should not be null!");

        String resourceDefinitionKey = resourceInstanceDO.getFlowResourceDefinitionKey();
        ParamException.isTrue(StringUtils.isBlank(resourceDefinitionKey), "inputParam[resourceDefinitionKey] should not be blank!");

        String flowDefinitionKey = orderEntity.getFlowDefinitionKey();
        ParamException.isTrue(StringUtils.isBlank(flowDefinitionKey), "inputParam[flowDefinitionKey] should not be blank!");

        FlowDefinitionResourceService flowDefinitionService = flowDefinitionServiceMap.get(flowDefinitionKey);
        // 不存在flowDefinitionKey为输入值的流程定义。检查定义是否存在，或者传入值【flowDefinitionKey】有问题
        InternalException.isTrue(Objects.isNull(flowDefinitionService), "invalid flowDefinitionKey[%s], flowDefinitionService not found! Please contact the administrator!");
        ResourceDefinitionBO userTaskResourceBO = flowDefinitionService.getFlowProcessBo().getChildShapes().stream()
                .filter(resourceDefinitionBO -> resourceDefinitionKey.equals(resourceDefinitionBO.getResourceDefinitionKey()))
                .findFirst()
                // 流程定义有误，指定流程中找不到指定的步骤。可能：创建流程工单后、完成流程工单前，流程定义的步骤key有修改、或被删除。
                .orElseThrow(() -> new InternalException(String.format("flow[flowDefinitionKey=%s] doesn't have the resource[resourceDefinitionKey=%s]! Please contact the administrator to check flow definition!", flowDefinitionKey, resourceDefinitionKey)));
        BaseUserTaskService resourceService = getResourceInstanceBean(resourceInstanceDO.getResourceInstanceUuid());
        List<ResourceDefinitionBO> resourceDefinitionBOList = resourceService.getNextOutGoingExecutionResource(userTaskResourceBO, inputVariablesMap);

        return resourceDefinitionBOList;
    }

    /**
     * 根据当前执行完成的userTask实例，创建下一跳或多跳的待执行userTask实例
     *
     * @param orderEntity                      步骤实例所属工单对象
     * @param resourceInstanceUuid             当前执行完成的userTask实例的唯一标识
     * @param outGoingResourceDefinitionBOList 下一跳或多跳的待执行userTask定义
     * @param inputVariablesMap                当前执行完成的userTask实例的执行结果出参
     * @return 下一跳或多跳的待执行userTask的instanceUuid集合
     */
    private List<String> createOutGoings(WbFlowOrderDO orderEntity, String resourceInstanceUuid,
                                         List<ResourceDefinitionBO> outGoingResourceDefinitionBOList, Map<String, Object> inputVariablesMap) {
        String orderUuid = orderEntity.getOrderUuid();
        List<WbFlowResourceInstance> outGoingInstanceEntity = new ArrayList<>();

        String inputVariablesStr = Objects.isNull(inputVariablesMap) ? null : JsonConvertUtil.toJsonString(inputVariablesMap);
        String createTimeStr = DateUtil.format(new Date(), DateUtil.DATE_TIME_REGEX);

        outGoingResourceDefinitionBOList.forEach(resourceDefinitionBO -> {
            WbFlowResourceInstanceDO instanceEntity = new WbFlowResourceInstanceDO();
            instanceEntity.setResourceInstanceUuid(UUID.randomUUID().toString());
            instanceEntity.setOrderUuid(orderUuid);
            instanceEntity.setFlowResourceDefinitionKey(resourceDefinitionBO.getResourceDefinitionKey());

            instanceEntity.setCreateTime(createTimeStr);
            instanceEntity.setOperateTime(createTimeStr);
            instanceEntity.setFormerResourceInstanceUuid(resourceInstanceUuid);
            instanceEntity.setInputVariables(inputVariablesStr);
            instanceEntity.setStatus(OrderInstanceStatusEnum.PROCESSING.getNameEn()); // 直接启动。todo zj 准备在异步流程中，判断userTask设置的自启动属性，判断是否自动设置为processing....目前processing和ready没区别
            instanceEntity.setValid(true);

            instanceMapper.insert(instanceEntity);
            outGoingInstanceEntity.add(instanceEntity);
        });

        createAndInjectUserTaskServiceBean(orderEntity.getOrderUuid(), inputVariablesStr, outGoingInstanceEntity);

        return outGoingInstanceEntity.stream().map(WbFlowResourceInstance::getResourceInstanceUuid).collect(Collectors.toList());
    }

    /**
     * 为每个工单的userTask步骤实例，创建一个userTaskService Bean, 并注入到对应工单的flowService Bean中，便于统一管理（获取、销毁）
     */
    private void createAndInjectUserTaskServiceBean(String orderUuid, String inputVariablesStr, List<WbFlowResourceInstance> outGoingInstanceEntity) {
        outGoingInstanceEntity.forEach(instanceEntity -> {
            Class<? extends BaseUserTaskService> clazz = userTaskServiceClassMap.get(instanceEntity.getFlowResourceDefinitionKey());
            BaseUserTaskService concreteUserTaskService = JsonConvertUtil.parseToObject(inputVariablesStr, clazz);
            if (Objects.isNull(concreteUserTaskService)) {
                try {
                    concreteUserTaskService = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new InternalException(ex);
                }
            }
//            WbFlowResourceInstanceDO instanceDO = new WbFlowResourceInstanceDO();
//            BeanUtils.copyProperties(instanceEntity, instanceDO);
//            concreteUserTaskService.setResourceInstance(instanceDO); // review reject invalid 用到 todo zj 暂时用不到了
//            concreteUserTaskService.setResourceDefinitionBO(instanceDefinitionKeyToDefinition.get(instanceEntity.getFlowResourceDefinitionKey())); // review reject invalid 用到  todo zj 暂时用不到了

            // 获取动态注册的bean
            FlowService flowService = context.getBean("orderUuid-" + orderUuid, FlowService.class);
            FlowDefinitionResourceService flowDefinitionService = flowService.getFlowDefinitionService();
            concreteUserTaskService.init(flowDefinitionService, null);

            String beanName = "instanceUuid-" + instanceEntity.getResourceInstanceUuid();
            System.out.printf("inject Bean[%s]\n", beanName); // todo zj 待刪除
            // 获取BeanFactory
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
            // 动态注册bean
            // todo zj test is ok 使用线程池。。线程池中创建的所有实例bean，都要进行销毁
            defaultListableBeanFactory.registerSingleton(beanName, concreteUserTaskService);
            defaultListableBeanFactory.autowireBean(concreteUserTaskService); // 为Bean注入对象

            flowService.getUserTaskServiceMap().put(beanName, concreteUserTaskService);
        });
    }

    private void updateAfterExecution(String resourceInstanceUuid, BaseUserTaskService userTaskService) {
        WbFlowResourceInstanceDO updateInstanceEntity = new WbFlowResourceInstanceDO();
        updateInstanceEntity.setResourceInstanceUuid(resourceInstanceUuid);

        updateInstanceEntity.setStatus(userTaskService.getStatus());
        updateInstanceEntity.setOperateTime(DateUtil.format(new Date(), DateUtil.DATE_TIME_REGEX));
        updateInstanceEntity.setOperatorId(userTaskService.getOperatorId());
        String outputVariables = JsonConvertUtil.toJsonString(userTaskService); // 只包括本步骤的相关参数。基类中的公共属性不写入到order信息（wb_flow_order.input_variables）中，避免每次覆盖更新没有意义
        updateInstanceEntity.setOutputVariables(outputVariables);
        int updateCount = instanceMapper.updateForCompletion(updateInstanceEntity);
        InternalException.isTrue(1 != updateCount,
                String.format("updateCount=[%s], should be number [1]. please contact the administrator", updateCount));
    }

    private void updateLatterInstance(String instanceUuid, List<String> uuidListOfOutGoingInstanceEntity) {
        WbFlowResourceInstanceDO updateInstanceEntity = new WbFlowResourceInstanceDO();
        updateInstanceEntity.setResourceInstanceUuid(instanceUuid);
        updateInstanceEntity.setLatterResourceInstanceUuidList(uuidListOfOutGoingInstanceEntity);
        // 系统操作，不更新操作时间
        int updateCount = instanceMapper.updateLatterInstance(updateInstanceEntity);
        InternalException.isTrue(1 != updateCount,
                String.format("updateCount=[%s], should be number [1]. please contact the administrator", updateCount));
    }

    private void updateInvalidInstance(Set<String> setOfInvalidInstanceUuid) {
        if (CollectionUtils.isEmpty(setOfInvalidInstanceUuid)) {
            return;
        }
        int updateCount = instanceMapper.invalidateInstance(setOfInvalidInstanceUuid);
        InternalException.isTrue(setOfInvalidInstanceUuid.size() != updateCount,
                String.format("updateCount=[%s], should be number [%s]. please contact the administrator",
                        updateCount, setOfInvalidInstanceUuid.size()));
    }

    private List<WbFlowResourceInstanceDO> getInstanceByOrderUuid(String orderUuid, OrderInstanceStatusEnum instanceStatusEnum) {
        ParamException.isTrue(StringUtils.isBlank(orderUuid), "inputParam[orderUuid] should not be blank!");
        List<WbFlowResourceInstanceDO> instanceEntityList = instanceMapper.getInstanceByOrderUuid(orderUuid,
                Objects.isNull(instanceStatusEnum) ? null : instanceStatusEnum.getNameEn());
        InternalException.isTrue(CollectionUtils.isEmpty(instanceEntityList), // 可能原因：代码逻辑问题。一个工单下存在至少一个步骤
                String.format("order[orderUuid=%s] should have at lease one resource instance, but found none! Please contact the administrator to check DB!", orderUuid));
        return instanceEntityList;
    }

    private WbFlowResourceInstanceDO getInstanceByInstanceUuid(String resourceInstanceUuid) {
        ParamException.isTrue(StringUtils.isBlank(resourceInstanceUuid), "inputParam[resourceInstanceUuid] should not be blank!");
        WbFlowResourceInstanceDO instanceEntity = instanceMapper.getInstanceByInstanceUuid(resourceInstanceUuid);
        ParamException.isTrue(Objects.isNull(instanceEntity),
                String.format("invalid resourceInstanceUuid=[%s], instanceEntity not found!", resourceInstanceUuid));
        return instanceEntity;
    }

    private List<WbFlowResourceInstanceDO> getInstanceByOrderUuidList(List<String> orderUuidList) {
        if (CollectionUtils.isEmpty(orderUuidList)) {
            return Collections.EMPTY_LIST;
        }
        return instanceMapper.getInstanceByOrderUuidList(orderUuidList);
    }
}
