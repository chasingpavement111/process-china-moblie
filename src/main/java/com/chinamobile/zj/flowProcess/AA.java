package com.chinamobile.zj.flowProcess;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.definition.FlowProcessDefinitionBO;
import com.chinamobile.zj.flowProcess.bo.definition.FlowProcessOrderedBO;
import com.chinamobile.zj.flowProcess.bo.definition.ResourceDefinitionBO;
import com.chinamobile.zj.flowProcess.enums.StencilEnum;
import com.chinamobile.zj.flowProcess.service.definition.FlowDefinitionResourceService;
import com.chinamobile.zj.flowProcess.service.resource.BaseExclusiveGatewayService;
import com.chinamobile.zj.flowProcess.service.resource.BaseSequenceFlowService;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.SubmitPreCheckApplicationUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.ReviewApplicationByCountyJiaKeManagerUserTaskService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class AA {

    private Map<String, FlowDefinitionResourceService> definitionResourceId2DefinitionService = new HashMap<String, FlowDefinitionResourceService>() {
        {


            String jsonXml;
            File jsonFileOfProcess = new File("C:\\work\\zj\\process-china-moblie\\src\\main\\resources\\static\\precheck_install_process.json");
            try {
                jsonXml = FileUtils.readFileToString(jsonFileOfProcess,
                        Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FlowDefinitionResourceService service1 = new FlowDefinitionResourceService(jsonXml);
            put(service1.getFlowProcessBo().getProperties().getProcessId(), service1);
        }
    };

    private BaseSequenceFlowService baseSequenceFlowService = new BaseSequenceFlowService();
    private BaseExclusiveGatewayService baseExclusiveGatewayService = new BaseExclusiveGatewayService();

    private Map<String, BaseUserTaskService> definitionKey2BaseUserTaskService = new HashMap<String, BaseUserTaskService>() {
        {
            BaseUserTaskService service1 = new SubmitPreCheckApplicationUserTaskService();
            put(service1.getDefinitionKey(), service1);

            BaseUserTaskService service2 = new ReviewApplicationByCountyJiaKeManagerUserTaskService();
            put(service2.getDefinitionKey(), service2);
        }
    };

    public static void main(String[] args) {

        AA a = new AA();
        a.start();
    }

    public void start() {


        // order_uuid -> flow_definition_id、current_node_instance_uuid、current_node_output_variables
        // current_node_instance_uuid -> resource_id
        //
//        String resourceIdOfCurrentFlowDefinition = "bd350884-53d6-4a63-a6fd-ef35c14c8d09";
        String definitionKeyOfCurrentFlowDefinition = "check_install_process";
        FlowDefinitionResourceService flowDefinitionResourceService = definitionResourceId2DefinitionService.get(definitionKeyOfCurrentFlowDefinition);
//        String resourceIdOfCurrentUserTaskResource = "sid-89C18A16-012D-4EED-AB37-B1FE79DE6D3E";
        String definitionKeyOfCurrentUserTaskResource = "review_application_by_county_jia_ke_manager";

        BaseUserTaskService userTaskService = definitionKey2BaseUserTaskService.get(definitionKeyOfCurrentUserTaskResource);

        ReviewResourceInputBO submitApplicationInputBO = new ReviewResourceInputBO(); // 外部业务具体流程，入参
        submitApplicationInputBO.setOperatorId("testUser");
        submitApplicationInputBO.setOperationResult("finished"); // todo zj  状态集整理
        submitApplicationInputBO.setOperationSnapshot(null); // 申请不传快照，王俊杰直接存到ftp中。不存到节点中
        userTaskService.init(flowDefinitionResourceService, submitApplicationInputBO);

        // 1、执行任务
        userTaskService.execute();

        // 2、任务成功完成后，进入下一节点
//        ResourceDefinitionBO nextUserTask = userTaskService.getNextUserTaskByResourceId(definitionKeyOfCurrentUserTaskResource, "{\"preCheckApplicationPassedByWhiteCollar\":true}"); // 家客经理审核预勘需求
        ResourceDefinitionBO userTaskDefinitionBO = flowDefinitionResourceService.getUserTaskDefinitionMap().get(definitionKeyOfCurrentUserTaskResource);
        Map<String, Object> outputVariablesMap = new HashMap<String, Object>(){
            {
                put("preCheckApplicationPassedByWhiteCollar",true);
                put("preCheckApplicationPassedByBlueCollar",null);
            }
        };
        List<ResourceDefinitionBO> nextExecutionResourceBOList = userTaskService.getNextOutGoingExecutionResource(userTaskDefinitionBO, outputVariablesMap); // 家客经理审核预勘需求
        ResourceDefinitionBO nextExecutionResourceBO = nextExecutionResourceBOList.get(0);
//        ResourceDefinitionBO nextUserTask = userTaskService.getNextUserTaskByResourceId(definitionKeyOfCurrentUserTaskResource, "{\"preCheckApplicationPassedByWhiteCollar\":true, \"preCheckApplicationPassedByBlueCollar\":true}"); // 家客经理审核预勘需求
        if (StencilEnum.THROW_NONE_EVENT.equals(nextExecutionResourceBO.getStencilEnum())) {
            // todo zj 流程结束
        } else if (StencilEnum.USER_TASK.equals(nextExecutionResourceBO.getStencilEnum())) {
            // todo zj 1、更新当前节点状态（可以在更加之前更新）。将 nextExecutionResourceBO 更新为当前执行节点
        } else {
            throw new ParamException(String.format("unsupported resource type for execution: %s", nextExecutionResourceBO.getStencilEnum().getId()));
        }


        System.out.printf("当前执行节点[definitionKey=%s, stencilId=%s] \n 对应的下一个执行节点为[definitionKey=%s, stencilId=%s]",
                userTaskDefinitionBO.getProperties().getOverrideid(), userTaskDefinitionBO.getStencilEnum().getId(),
                nextExecutionResourceBO.getProperties().getOverrideid(), nextExecutionResourceBO.getStencilEnum().getId());
        // 状态变更 、 当前节点切换
//        FlowProcessOrderedBO orderedFlowProcessBO = sort(flowProcessBo);
    }

    private FlowProcessOrderedBO sort(FlowProcessDefinitionBO flowProcessBo) {
        FlowProcessOrderedBO orderedFlowProcessBO = new FlowProcessOrderedBO();
        BeanUtils.copyProperties(flowProcessBo, orderedFlowProcessBO);


        List<List<ResourceDefinitionBO>> multiChildShapes = new ArrayList<>();
        List<ResourceDefinitionBO> orderedChildShapes = new LinkedList<>();
        flowProcessBo.getChildShapes().forEach(childResource -> {
            orderedChildShapes.forEach(orderChildResource -> {
            });
            orderedChildShapes.add(childResource);
        });

        return orderedFlowProcessBO;
    }
}
