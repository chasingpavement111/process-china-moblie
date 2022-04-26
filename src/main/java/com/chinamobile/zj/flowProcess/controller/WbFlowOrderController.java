package com.chinamobile.zj.flowProcess.controller;


import com.chinamobile.zj.comm.ResponseData;
import com.chinamobile.zj.flowProcess.bo.dto.CreateFlowOrderDTO;
import com.chinamobile.zj.flowProcess.bo.dto.OrderInfoResultDTO;
import com.chinamobile.zj.flowProcess.bo.dto.StartFlowOrderDTO;
import com.chinamobile.zj.flowProcess.bo.input.CompleteResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowOrderService;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowResourceInstanceService;
import com.chinamobile.zj.hdict.entity.PreCheckApplication;
import com.chinamobile.zj.service.interfaces.URIAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 流程工单
 * </p>
 *
 * @author zhangjie
 * @since 2022-04-21
 */
@RestController
@RequestMapping("/flow")
public class WbFlowOrderController {

    @Autowired
    private WbFlowOrderService orderService;

    @Autowired
    private WbFlowResourceInstanceService instanceService;

    @PostMapping(value = "/order/create")
    @URIAccess(menuName = "创建工单")
    public ResponseData createOrder(@Valid @RequestBody CreateFlowOrderDTO createOrderDTO) {
        if (false) {
            // 入参样例
            String loginUserId = "chenyaoting";
            createOrderDTO.setCreatorId(loginUserId);
            createOrderDTO.setFlowDefinitionKey("check_install_process"); // 固定值。预勘流程的流程唯一标识
            {
                Map<String, Object> inputVariablesMap = new HashMap<>();
                {
                    // inputVariablesMap 入参设置样例。必须包含key=preCheckApplication, value的class类型为 PreCheckApplication.class 的元素。
                    // 原因见 com.chinamobile.zj.hdict.entity.PreCheckApplication 注释
                    // com.chinamobile.zj.hdict.entity.PreCheckApplication 哪些属性必填，见注解。
                    PreCheckApplication preCheckApplication = new PreCheckApplication();
                    preCheckApplication.setAreaId3("84");
                    preCheckApplication.setCreatorId(loginUserId);
                    inputVariablesMap.put("preCheckApplication", preCheckApplication);
                }
                createOrderDTO.setInputVariablesMap(inputVariablesMap);
            }
        }
        String orderUuid = orderService.create(createOrderDTO, true);
        return ResponseData.ok(orderUuid);
    }

    @PostMapping(value = "/order/start")
    @URIAccess(menuName = "启动ready的工单")
    public ResponseData startOrder(@Valid @RequestBody StartFlowOrderDTO startOrderDTO) {
        if (false) {
            // 入参样例
            String loginUserId = "chenyaoting";
            startOrderDTO.setOperatorId(loginUserId);
            startOrderDTO.setOrderUuid("f3fa57b6-404b-4a00-825f-cde23b6600fb"); // 固定值。预勘流程的流程唯一标识
            {
                Map<String, Object> inputVariablesMap = new HashMap<>();
                {
                    // inputVariablesMap 入参设置样例。必须包含key=preCheckApplication, value的class类型为 PreCheckApplication.class 的元素。
                    // 原因见 com.chinamobile.zj.hdict.entity.PreCheckApplication 注释
                    // com.chinamobile.zj.hdict.entity.PreCheckApplication 哪些属性必填，见注解。
                    PreCheckApplication preCheckApplication = new PreCheckApplication();
                    preCheckApplication.setAreaId3("84");
                    preCheckApplication.setCreatorId(loginUserId);
                    inputVariablesMap.put("preCheckApplication", preCheckApplication);
                }
                // 最终的工单入参，为 创工单时入参 与 本入参的 合集
                startOrderDTO.setInputVariablesMap(inputVariablesMap);
            }
        }
        String orderUuid = orderService.start(startOrderDTO);
        return ResponseData.ok(orderUuid);
    }

    @GetMapping(value = "/order/info")
    @URIAccess(menuName = "查询单个工单")
    public ResponseData orderInfo(@RequestParam String orderUuid) {
        OrderInfoResultDTO info = orderService.info(orderUuid);
        return ResponseData.ok(info);
    }

    @GetMapping(value = "/order/executionHistory")
    @URIAccess(menuName = "获取工单执行历史", args = {
            @URIAccess.ArgsMapping(fieldNameEn = "orderUuid", fieldNameCh = "流程工单唯一标识"),
            @URIAccess.ArgsMapping(fieldNameEn = "areaId4", fieldNameCh = "网格编号")
    })
    public ResponseData orderExecutionHistory(@RequestParam String orderUuid,
                                              @RequestParam(required = false) String areaId4) {
//        List<SelectionDo> list = businessCircleInfoService.busiCircleSelection(areaId3, areaId4);
        return ResponseData.ok(null);
    }

    @PostMapping(value = "/resource/instance/complete")
    @URIAccess(menuName = "完成步骤实例")
    public ResponseData completeInstance(@Valid @RequestBody CompleteResourceInputBO inputBO) {
        if (false) {
            // 入参样例
            inputBO.setOrderUuid("954b9844-35fc-41db-a57e-235d1477db8c"); // orderUuid值
            inputBO.setResourceInstanceUuid("b4a4db35-3767-4aac-a09b-25c1ff502a4c"); // resourceInstanceUuid值。操作步骤的实例唯一标识
            String loginUserId = "chenyaoting";
            inputBO.setOperatorId(loginUserId);
            inputBO.setOperationSnapshot(null); // 是否必填，需要必填时传什么值，都以操作步骤的要求为准。
            /*
            operationSnapshot入参按不同步骤的具体要求（每个步骤对应一个class类，下面以步骤的具体执行顺序，进行说明）：
            需注意：这里只列了最小入参集合，参数会被用户。可以填多余的参数，多填的参数会被存储但使用时忽略。
            1、SubmitPreCheckApplicationUserTaskService：operationSnapshot==null
            2、
             */
        }
        String resourceInstanceUuid = orderService.completeResourceInstance(inputBO);
        return ResponseData.ok(resourceInstanceUuid);
    }

    @PostMapping(value = "/resource/instance/review")
    @URIAccess(menuName = "步骤实例进行审核")
    public ResponseData reviewInstance(@Valid @RequestBody ReviewResourceInputBO reviewResourceInputBO) {
        String resourceInstanceUuid = orderService.reviewResourceInstance(reviewResourceInputBO);
        return ResponseData.ok(resourceInstanceUuid);
    }
}

