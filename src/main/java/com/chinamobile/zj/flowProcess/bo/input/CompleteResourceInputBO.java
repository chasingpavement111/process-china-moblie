package com.chinamobile.zj.flowProcess.bo.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CompleteResourceInputBO {

    @NotBlank
    private String orderUuid;

    /**
     * 必传，可能一个工单有多个“流转中”中的步骤，需要通过本参数，明确告知当前操作的是哪个步骤
     */
    @NotBlank
    private String resourceInstanceUuid;

    @NotBlank
    private String operatorId;

    private String operationSnapshot;
}
