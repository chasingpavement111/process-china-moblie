package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InheritParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public class ReviewApplicationByCountyJiaKeManagerUserTaskService extends AbstractHdictReviewUserTaskService {

    /**
     * 县HDICT专员转派的家客经理的用户ID
     */
    @InheritParam
    private String assignedJiaKeCountyManagerId;

    @InheritParam
    private String assignedJiaKeCountyManagerName;

    @OutputParam
    private Boolean preCheckApplicationPassedByWhiteCollar;

    @Override
    public String getDefinitionKey() {
        return "review_application_by_county_jia_ke_manager";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "家客经理审核预勘需求";
    }

    @Override
    public ExecutionResult execute() {
        ReviewOperationResultEnum.getByNameEn(getOperationResult()); // 检查必须经过审核
        preCheckApplicationPassedByWhiteCollar = ReviewOperationResultEnum.PASSED.getNameEn().equals(getOperationResult());
        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict003", "家客经理-县市");
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）对预勘需求申请{2}", getOperatorRoleName(), getOperatorName(),
                operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        super.checkOperatorAccessRight();

        // 只允许被转派家客经理，进行审批。
        ParamException.isTrue(!assignedJiaKeCountyManagerId.equals(getOperatorId()),
                String.format("operator[CRMId=%s] is not the jiaKeManager that county hdict user assigned, only user[CRMId=%s] can operator this step!",
                        getOperatorId(), assignedJiaKeCountyManagerId));

    }

    public String getAssignedJiaKeCountyManagerId() {
        return assignedJiaKeCountyManagerId;
    }

    public void setAssignedJiaKeCountyManagerId(String assignedJiaKeCountyManagerId) {
        this.assignedJiaKeCountyManagerId = assignedJiaKeCountyManagerId;
    }

    public String getAssignedJiaKeCountyManagerName() {
        return assignedJiaKeCountyManagerName;
    }

    public void setAssignedJiaKeCountyManagerName(String assignedJiaKeCountyManagerName) {
        this.assignedJiaKeCountyManagerName = assignedJiaKeCountyManagerName;
    }

    public Boolean getPreCheckApplicationPassedByWhiteCollar() {
        return preCheckApplicationPassedByWhiteCollar;
    }

    public void setPreCheckApplicationPassedByWhiteCollar(Boolean preCheckApplicationPassedByWhiteCollar) {
        this.preCheckApplicationPassedByWhiteCollar = preCheckApplicationPassedByWhiteCollar;
    }
}
