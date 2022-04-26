package com.chinamobile.zj.flowProcess.bo.input;

import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReviewResourceInputBO extends BaseOperateResourceInputBO {

    @NotBlank
    private String operationResult;

    @NotBlank
    private String operationMessage;

    public void setOperationResult(String operationResult) {
        ReviewOperationResultEnum.getByNameEn(operationResult);
        this.operationResult = operationResult;
    }
}
