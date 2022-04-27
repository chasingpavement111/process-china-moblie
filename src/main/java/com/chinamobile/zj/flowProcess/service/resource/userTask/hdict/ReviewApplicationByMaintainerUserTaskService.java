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

public class ReviewApplicationByMaintainerUserTaskService extends AbstractHdictReviewUserTaskService {

    /**
     * 县铁通HDICT专员转派的装维人员的用户ID
     */
    @InheritParam
    private String assignedMaintainerId;

    @InheritParam
    private String assignedMaintainerName;

    @OutputParam
    private Boolean preCheckApplicationPassedByBlueCollar;


    @Override
    public String getDefinitionKey() {
        return "review_application_by_maintainer";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "装维人员审核预勘需求";
    }

    @Override
    public ExecutionResult execute() {
        ReviewOperationResultEnum.getByNameEn(getOperationResult()); // 检查必须经过审核
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult())) {
            // 审核不通过，直接返回
            preCheckApplicationPassedByBlueCollar = false;
            return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
        } else {
            preCheckApplicationPassedByBlueCollar = true;
        }

        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict002", "HDICT装维");
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）对预勘需求申请进行现场审核{2}", getOperatorRoleName(), getOperatorName(), operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        super.checkOperatorAccessRight();

        // 只允许 被指派进行现场审核的装维人员 操作
        ParamException.isTrue(!assignedMaintainerId.equals(getOperatorId()),
                String.format("operator[CRMId=%s] is not the county hdict user, only user[CRMId=%s] can operator this step!",
                        getOperatorId(), assignedMaintainerId));
    }

    public String getAssignedMaintainerId() {
        return assignedMaintainerId;
    }

    public void setAssignedMaintainerId(String assignedMaintainerId) {
        this.assignedMaintainerId = assignedMaintainerId;
    }

    public String getAssignedMaintainerName() {
        return assignedMaintainerName;
    }

    public void setAssignedMaintainerName(String assignedMaintainerName) {
        this.assignedMaintainerName = assignedMaintainerName;
    }

    public Boolean getPreCheckApplicationPassedByBlueCollar() {
        return preCheckApplicationPassedByBlueCollar;
    }

    public void setPreCheckApplicationPassedByBlueCollar(Boolean preCheckApplicationPassedByBlueCollar) {
        this.preCheckApplicationPassedByBlueCollar = preCheckApplicationPassedByBlueCollar;
    }
}
