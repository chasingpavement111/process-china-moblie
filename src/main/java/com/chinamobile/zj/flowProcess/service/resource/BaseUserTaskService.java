package com.chinamobile.zj.flowProcess.service.resource;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import com.chinamobile.zj.flowProcess.service.definition.FlowDefinitionResourceService;
import com.chinamobile.zj.util.JsonConvertUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseUserTaskService extends BaseResourceService implements MultipleNextOutGoing {

    private transient FlowDefinitionResourceService flowDefinitionResourceService;

    @Autowired
    private transient BaseSequenceFlowService baseSequenceFlowService;

    @Autowired
    private transient BaseExclusiveGatewayService baseExclusiveGatewayService;

    private String operatorRoleName;

    private String operatorId;

    private String operatorName;

    /**
     * 步骤的操作结果的外部入参值
     * 决定status
     */
    private String operationResult;
    /**
     * 步骤的操作结果备注信息的外部入参值
     * 用户描述语句
     */
    private String operationMessage;

    /**
     * 步骤状态，由操作结果决定
     */
    private String status;

    /**
     * 操作的入参快照
     * todo zj 不做持久化?？？？
     */
    private String operationSnapshot;

    private transient Map<String, Object> outputVariablesMap;

//    operator_id, operation_status, status(由operation_status+instance_type转换得到), operation_snapshot

    /**
     * userTask类型元素，对应的key.
     * 简单描述了处理功能
     * 对应 com.chinamobile.zj.flowProcess.BO.definition.ResourcePropertiesBO#overrideid
     */
    public abstract String getDefinitionKey();

    public abstract String getDefinitionKeyDesc();

    /**
     * 初始化
     */
    public void init(FlowDefinitionResourceService flowDefinitionResourceService, ReviewResourceInputBO inputBO) {
        setFlowDefinitionResourceService(flowDefinitionResourceService);
        if (Objects.nonNull(inputBO)) {
            BeanUtils.copyProperties(inputBO, this); // todo zj test
        }
    }

    /**
     * complete\review 等操作，都会触发执行execute
     * <p>
     * 或者
     * XXXXXXXXXXXXXXXXXXXXXXX
     * 创建节点后，直接执行
     * complete\review 操作只改变节点status，不会触发执行execute
     */
    public abstract ExecutionResult execute();

    private FlowDefinitionResourceService getFlowDefinitionResourceService() {
        // todo zj private 是避免被序列化写入数据库。但是子类是否会用到这个对象呢
        return flowDefinitionResourceService;
    }

    /**
     * 关联流程定义的对象
     * 一个userTask可能被多个流程中定义。故需要使用时动态关联
     */
    private void setFlowDefinitionResourceService(FlowDefinitionResourceService flowDefinitionResourceService) {
        this.flowDefinitionResourceService = flowDefinitionResourceService;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        if (StringUtils.isBlank(operatorName)) {
            return ""; // 避免方法输出内容包含null:com.chinamobile.zj.flowProcess.service.resource.userTask.hdict.ReviewOrAssignApplicationByCountyHdictUserTaskService.getOperationOutputDesc
        }
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public String getOperatorRoleName() {
        if (StringUtils.isBlank(operatorRoleName)) {
            String roleNameStr = supportedOperatorRoleMap().values().stream().collect(Collectors.joining("/"));
            return roleNameStr; // 避免方法输出内容包含null:com.chinamobile.zj.flowProcess.service.resource.userTask.hdict.ReviewOrAssignApplicationByCountyHdictUserTaskService.getOperationOutputDesc
        }
        return operatorRoleName;
    }

    public void setOperatorRoleName(String operatorRoleName) {
        this.operatorRoleName = operatorRoleName;
    }

    public void setOperationResult(String operationResult) {
        if (StringUtils.isBlank(operationResult)) {
            return;
        }
        this.operationResult = operationResult;

        // 根据operationResult动作结果, 设置流程步骤的状态
        if (operationResult.equals(OrderInstanceStatusEnum.FINISHED.getNameEn())) {
            // 1、完成操作
            setStatus(OrderInstanceStatusEnum.FINISHED.getNameEn());
        } else {
            // 2、审核操作
            ReviewOperationResultEnum operationResultEnum = ReviewOperationResultEnum.getByNameEn(operationResult);
            setStatus(operationResultEnum.getCorrespondingInstanceStatusEnum().getNameEn());
        }
    }

    public String getOperationMessage() {
        return operationMessage;
    }

    public void setOperationMessage(String operationMessage) {
        this.operationMessage = operationMessage;
    }

    public String getStatus() {
        return status;
    }

    /**
     * private 为了只能通过 operationResult 赋值
     */
    private void setStatus(String status) {
        this.status = status;
    }

    public String getOperationSnapshot() {
        return operationSnapshot;
    }

    public void setOperationSnapshot(String operationSnapshot) {
        if (StringUtils.isBlank(operationSnapshot)) {
            return;
        }
        this.operationSnapshot = operationSnapshot;

        // 将操作的入参，更新到对应的操作实现类的成员变量中
        BaseUserTaskService tempSource = JsonConvertUtil.parseToObject(operationSnapshot, getClass()); // 类需要实现fastJson序列化的，必须定义setter\getter
        for (Field field : getClass().getDeclaredFields()) {
            if (Objects.nonNull(field.getAnnotation(Autowired.class))) {
                // 忽略Autowire的成员
                continue;
            }
            field.setAccessible(true); // 使得可以获取private修饰的成员变量值
            try {
                Object newValue = field.get(tempSource);
                if (Objects.nonNull(newValue)) {
                    // 入参值非空，则更新
                    field.set(this, newValue);
                }
            } catch (IllegalAccessException ex) {
                throw new InternalException(ex);
            }
        }


//        BeanUtils.copyProperties(tempSource, this); // todo zj 好像不需要 外部代码已实现赋值。operationSnapshot 可能是outputVariable的子集，导致参数丢失
////        if(true){
////            throw new InternalException("aa");
////        }
//        System.out.println(1);
//        // todo zj BeanUtil有问题就换成反射
    }

    public Map<String, Object> getOutputVariablesMap() {
        return outputVariablesMap;
    }

    public void setOutputVariablesMap(Map<String, Object> outputVariablesMap) {
        this.outputVariablesMap = outputVariablesMap;
    }

    /**
     * 操作结果描述，用于接口返回执行结果描述
     */
    public abstract String getOperationOutputDesc();

    /**
     * <roleId, roleName>
     * 步骤的操作人角色，可能支持多种用户角色
     * 若元素数量为0的集合对象 -> 标识支持所有类型的用户
     */
    public abstract Map<String, String> supportedOperatorRoleMap();

    /**
     * 只返回具体类中声明的成员变量
     * 避免获取基类中的公共变量。因为这个方法的返回值，用于回写到order的inputVariables, 没必要将每个步骤独有的信息写入order
     */
    public Map<String, Object> currentUniqueOutputVariables() { // 方法名，不要已get开头，避免写入 wb_flow_resource_instanceoutput_variables todo  zj test
        Map<String, Object> map = new HashMap<>();
        for (Field field : getClass().getDeclaredFields()) {
            if (Objects.nonNull(field.getAnnotation(Autowired.class))) {
                // 忽略Autowire的成员
                continue;
            }
            field.setAccessible(true); // 是的可以获取private修饰的成员变量值
            try {
                map.put(field.getName(), field.get(this));
            } catch (IllegalAccessException ex) {
                throw new InternalException(ex);
            }
        }
        return map;
    }

    public List<ResourceDefinitionBO> getNextOutGoingExecutionResource(ResourceDefinitionBO userTaskDefinitionBO, Map<String, Object> outputVariablesMap) {
        InternalException.isTrue(Objects.isNull(userTaskDefinitionBO),
                String.format("invalid resourceId=[%s] of order[orderUuid=%s, currentInstanceUuid=%s]. Please contact the administrator to check flow definition!!",
                        userTaskDefinitionBO.getResourceId(), "aaaa", "bbb")); // 报错可能原因：流程定义json文件被修改。不存在该resource元素 todo zj aaa bbb 修改
        InternalException.isTrue(!StencilEnum.USER_TASK.equals(userTaskDefinitionBO.getStencilEnum()) &&
                        !StencilEnum.START_NONE_EVENT.equals(userTaskDefinitionBO.getStencilEnum()),
                String.format("resource type mismatch! Expect [userTask], but got [%s]. Please contact the administrator! ",
                        userTaskDefinitionBO.getStencilEnum().getId())); // 报错可能原因：后端代码问题。只有userTask类型的元素，有执行中的概念。只有该类型会生成 nodeInstance对象。
//        Map<String, Object> outputVariablesMap = JsonConvertUtil.parseToParameterizedType(outputVariables, new TypeReference<Map<String, Object>>() {
//        });

        List<ResourceDefinitionBO> nextOutGoingList = new ArrayList<>();
        for (ResourceDefinitionBO outGoingResource : userTaskDefinitionBO.getOutGoing()) {
            // todo zj 实例化资源对象。不用实例化了。详见有问题的实现：com.chinamobile.zj.flowProcess.service.definition.FlowDefinitionResourceService.setUserTaskDefinitionMap
//            outGoingResource = flowDefinitionResourceService.getResourceDefinitionMap().get(outGoingResource.getResourceId());
            if (!StencilEnum.SEQUENCE_FLOW.equals(outGoingResource.getStencilEnum())) {
                // 肯定是 type = SequenceFlow, 但是有问题就忽略，不管
                continue;
            }
            ResourceDefinitionBO sequenceDefinitionBO = outGoingResource;
            ResourceDefinitionBO nextOutGoing = getNextOutGoingExecutionResourceBySequence(sequenceDefinitionBO, outputVariablesMap);
            if (Objects.nonNull(nextOutGoing)) {
                nextOutGoingList.add(nextOutGoing);
            }
        }
        // userTask 的下一跳，可能有多个；todo zj 可能吗？
        // finalNextOutGoing == null 的两种情况：
        // 1、到达结束节点，流程结束；
        // 2、流传中断，不知道该走哪个分支，因为所有分支都不成立（此时为定义问题或代码问题或流转条件入参值有问题，需排查） —— 此时本步骤应处于非终态，继续执行
        return nextOutGoingList;
    }

    /**
     * 根据连线，获取下一跳待执行资源
     * 待执行资源的类型可能为：ThrowNoneEvent、UserTask
     */
    private ResourceDefinitionBO getNextOutGoingExecutionResourceBySequence(ResourceDefinitionBO sequenceDefinitionBO, Map<String, Object> outputVariablesMap) {
        ResourceDefinitionBO definitionBO = baseSequenceFlowService.getDirectNextOutGoingResource(sequenceDefinitionBO, outputVariablesMap); // 连线，只有一个出口
        if (Objects.isNull(definitionBO)) {
            // 条件不成立，不走该分支
            return null;
        }

        if (StencilEnum.USER_TASK.equals(definitionBO.getStencilEnum())) {
            return definitionBO;
        } else if (StencilEnum.EXCLUSIVE_GATEWAY.equals(definitionBO.getStencilEnum())) {
            ResourceDefinitionBO firstNextOutGoing = baseExclusiveGatewayService.getDirectNextOutGoingResource(definitionBO, outputVariablesMap);
            ResourceDefinitionBO outGoingOfGateway = firstNextOutGoing;
            while (Objects.nonNull(outGoingOfGateway) && StencilEnum.EXCLUSIVE_GATEWAY.equals(outGoingOfGateway.getStencilEnum())) {
                ResourceDefinitionBO nextOutGoingOfExclusiveGatewayResource = baseExclusiveGatewayService.getDirectNextOutGoingResource(outGoingOfGateway, outputVariablesMap);
                outGoingOfGateway = nextOutGoingOfExclusiveGatewayResource;
            }
            if (Objects.isNull(outGoingOfGateway)) {
                // 网关有多个入口，存在一到多个入口还未执行完成，故还不能确定本网关的下一跳出口
                return null;
            } else {
                return outGoingOfGateway;
            }
        } else if (StencilEnum.THROW_NONE_EVENT.equals(definitionBO.getStencilEnum())) {
            // 到达结束节点，流程结束
            return definitionBO;
        }
        // 不支持处理的元素类型。
        // 连线出口，目前只支持上面的几种类型。若进入本报错，可能原因是利用activiti画出的流程图有问题，使用了不支持的元素
        throw new InternalException(String.format("unsupported resourceType=[%s]. Please contact the administrator to check flow definition!!", definitionBO.getStencilEnum().getId()));
    }

//    /**
//     * 执行过程报错，需重试
//     * todo zj 待测试  暂不需要。抛出异常会直接透传到前端。
//     */
//    @ExceptionHandler(value = Exception.class)
//    public ExecutionResult handle(Exception ex, HttpServletRequest request) {
//        return new ExecutionResult(ExecutionResult.RESULT_CODE.EXCEPTION, ex.getMessage());
//
//    }
}
