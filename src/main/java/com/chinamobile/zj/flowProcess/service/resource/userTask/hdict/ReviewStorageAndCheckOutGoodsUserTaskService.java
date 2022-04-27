package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public class ReviewStorageAndCheckOutGoodsUserTaskService extends AbstractHdictReviewUserTaskService {

    /**
     * 是否货源充足
     */
    @OutputParam
    private Boolean goodSufficient;

    @Override
    public String getDefinitionKey() {
        return "check_out_goods";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "铁通出库分配";
    }

    @Override
    public ExecutionResult execute() {
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult())) {
            // 审核不通过，直接返回
            goodSufficient = false;
            return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
        }
        goodSufficient = true;

        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict018", "铁通仓管");
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）出库分配{2}", getOperatorRoleName(), getOperatorName(),
                operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        super.checkOperatorAccessRight();
    }

    public Boolean getGoodSufficient() {
        return goodSufficient;
    }

    public void setGoodSufficient(Boolean goodSufficient) {
        this.goodSufficient = goodSufficient;
    }
}
