package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InheritParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.LimitOperatorRole;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.ReviewTask;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.entity.PreCheckApplication;
import com.chinamobile.zj.hdict.service.interfaces.HdictUserInfoService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ConstructByMaintainerUserTaskService extends BaseUserTaskService implements LimitOperatorRole, ReviewTask {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InheritParam
    private PreCheckApplication preCheckApplication;

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
        Optional<HdictUserInfoDO> operatorInfoOpt = userInfoService.getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        ParamException.isTrue(BooleanUtils.isNotTrue(preCheckApplication.getAreaId3().equals(operatorInfo.getAreaId3()) && supportedOperatorRoleMap().containsKey(operatorInfo.getRoleId())),
                String.format("user[CRMId=%s, areaId3=%s, roleName=%s] doesn't have access to operate this step!",
                        operatorInfo.getLoginId(), operatorInfo.getAreaId3(), operatorInfo.getRoleName()));
        // 只允许 被指派进行现场施工的装维人员 进行审核
        ParamException.isTrue(!assignedConstructorId.equals(getOperatorId()),
                String.format("operator[CRMId=%s] is not the county maintainer user, only user[CRMId=%s] can operator this step!",
                        getOperatorId(), assignedConstructorId));

        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
    }

    public PreCheckApplication getPreCheckApplication() {
        return preCheckApplication;
    }

    public void setPreCheckApplication(PreCheckApplication preCheckApplication) {
        this.preCheckApplication = preCheckApplication;
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
