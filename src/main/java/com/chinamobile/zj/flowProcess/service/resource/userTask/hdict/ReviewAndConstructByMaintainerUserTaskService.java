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

public class ReviewAndConstructByMaintainerUserTaskService extends AbstractHdictReviewUserTaskService {

    @OutputParam
    private Boolean canBeConstructed;

    /**
     * 县铁通hdict专员转派进行施工的装维人员的用户ID
     */
    @InheritParam
    private String assignedConstructorId;

    @InheritParam
    private String assignedConstructorName;

    @Override
    public String getDefinitionKey() {
        return "construct_by_maintainer";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "装维人员审核现场与施工";
    }

    @Override
    public ExecutionResult execute() {
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult())) {
            // 审核不通过，直接返回
            canBeConstructed = false;
            return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
        }
        canBeConstructed = true;

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
        return MessageFormat.format("{0}（{1}）对施工{2}", getOperatorRoleName(), getOperatorName(), operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        super.checkOperatorAccessRight();

        // 只允许 被指派进行现场施工的装维人员 进行审核
        ParamException.isTrue(!assignedConstructorId.equals(getOperatorId()),
                String.format("operator[CRMId=%s] is not the county maintainer user, only user[CRMId=%s] can operator this step!",
                        getOperatorId(), assignedConstructorId));
    }

    public Boolean getCanBeConstructed() {
        return canBeConstructed;
    }

    public void setCanBeConstructed(Boolean canBeConstructed) {
        this.canBeConstructed = canBeConstructed;
    }

    public String getAssignedConstructorId() {
        return assignedConstructorId;
    }

    public void setAssignedConstructorId(String assignedConstructorId) {
        this.assignedConstructorId = assignedConstructorId;
    }

    public String getAssignedConstructorName() {
        return assignedConstructorName;
    }

    public void setAssignedConstructorName(String assignedConstructorName) {
        this.assignedConstructorName = assignedConstructorName;
    }
}
