package com.chinamobile.zj.flowProcess.bo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 完成步骤实例的返回结构体
 */
@Data
public class FinishResourceResultDTO {

    private String instanceUuid;

    private List<String> outGoingInstanceUuidList;

    /**
     * instance的输出变量
     */
    private Map<String, Object> outputVariablesMap;
}
