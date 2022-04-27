package com.chinamobile.zj.flowProcess.bo.input;

import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CancelResourceInputBO extends BaseOperateResourceInputBO {

    private String operationResult = OrderInstanceStatusEnum.CANCELED.getNameEn();

    @NotBlank
    private String operationMessage;

    private void setOperationResult(String operationResult) {
        // 私有化方法，限制入参不允许外部设置。强制废止实例，步骤实例状态自动变成canceled
        this.operationResult = operationResult;
    }
}
