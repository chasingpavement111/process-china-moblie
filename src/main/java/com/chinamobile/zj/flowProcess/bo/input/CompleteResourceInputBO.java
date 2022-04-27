package com.chinamobile.zj.flowProcess.bo.input;

import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import lombok.Data;

@Data
public class CompleteResourceInputBO extends BaseOperateResourceInputBO {

    private String operationResult = OrderInstanceStatusEnum.FINISHED.getNameEn();

    private void setOperationResult(String operationResult) {
        // 私有化方法，限制入参不允许外部设置。完成实例，步骤实例状态自动变成finished
        this.operationResult = operationResult;
    }
}
