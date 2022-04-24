package com.chinamobile.zj.flowProcess.controller;


import com.chinamobile.zj.comm.ResponseData;
import com.chinamobile.zj.flowProcess.bo.input.CompleteResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.input.ReviewResourceInputBO;
import com.chinamobile.zj.flowProcess.bo.dto.CreateFlowOrderDTO;
import com.chinamobile.zj.flowProcess.bo.dto.OrderInfoResultDTO;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowOrderService;
import com.chinamobile.zj.flowProcess.service.busi.interfaces.WbFlowResourceInstanceService;
import com.chinamobile.zj.service.interfaces.URIAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        String orderUuid = orderService.create(createOrderDTO, true);
        return ResponseData.ok(orderUuid);
    }

    @GetMapping(value = "/order/info")
    @URIAccess(menuName = "查询单个工单")
    public ResponseData orderInfo(@RequestParam String orderUuid) {
//        CreateFlowOrderDTO createOrderDTO  = new CreateFlowOrderDTO();
//        createOrderDTO.setFlowDefinitionKey("check_install_process");
//        createOrderDTO.setInputVariables(null);
//        createOrderDTO.setCreatorId("wubiao1"); // loginUserId
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
        String resourceInstanceUuid = orderService.completeResourceInstance(inputBO);
        return ResponseData.ok(resourceInstanceUuid);
    }

    @PostMapping(value = "/resource/instance/review")
    @URIAccess(menuName = "步骤实例进行审核")
    public ResponseData reviewInstance(@Valid @RequestBody ReviewResourceInputBO reviewResourceInputBO) {
//        executionResourceInputBO.setOrderUuid();
//        executionResourceInputBO.setResourceInstanceUuid();

//        executionResourceInputBO.setOperatorId("wubiao1"); // loginUserId
//        executionResourceInputBO.setOperationResult("passed");
//        executionResourceInputBO.setOperationMessage("这是我的操作说明");
        String resourceInstanceUuid = orderService.reviewResourceInstance(reviewResourceInputBO);
        return ResponseData.ok(resourceInstanceUuid);
    }
}

