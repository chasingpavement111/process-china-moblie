package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public class ReviewPlanByCityJiaKeManagerUserTaskService extends AbstractHdictReviewUserTaskService {

    @OutputParam
    private Boolean planPassedByWhiteCollar;

    @Override
    public String getDefinitionKey() {
        return "review_plan_by_city_jia_ke_manager";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "市家客经理审核方案";
    }

    @Override
    public ExecutionResult execute() {
        ReviewOperationResultEnum.getByNameEn(getOperationResult()); // 检查必须经过审核
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult())) {
            // 审核不通过，直接返回
            planPassedByWhiteCollar = false;
        } else {
            planPassedByWhiteCollar = true;
        }

        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict004", "家客经理-地市");
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）对方案审核{2}", getOperatorRoleName(), getOperatorName(),
                operationStatus);
    }

    public Boolean getPlanPassedByWhiteCollar() {
        return planPassedByWhiteCollar;
    }

    public void setPlanPassedByWhiteCollar(Boolean planPassedByWhiteCollar) {
        this.planPassedByWhiteCollar = planPassedByWhiteCollar;
    }
}
