package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.service.InputParam;
import com.chinamobile.zj.flowProcess.service.LimitOperatorRole;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.entity.PreCheckApplication;
import com.chinamobile.zj.hdict.service.interfaces.HdictUserInfoService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ReviewApplicationByCountyJiaKeManagerUserTaskService extends BaseUserTaskService implements LimitOperatorRole {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InputParam
    private PreCheckApplication preCheckApplication;

    /**
     * 县HDICT专员转派的家客经理的用户ID
     */
    @InputParam
    private String assignedJiaKeCountyManagerId;

    @InputParam
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
        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict003", "家客经理-县市");
    }

    @Override
    public String getOperationOutputDesc() {
        return MessageFormat.format("{0}（{1}）对预勘需求申请{2}", getOperatorRoleName(), getOperatorName(),
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh());
    }

    @Override
    public void checkOperatorAccessRight() {
        Optional<HdictUserInfoDO> operatorInfoOpt = userInfoService.getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        // 只允许被转派家客经理，进行审批。
        ParamException.isTrue(!assignedJiaKeCountyManagerId.equals(operatorInfo.getLoginId()),
                String.format("operator[CRMId=%s] is not the jiaKeManager that countyHdict assigned, only user[CRMId=%s] can operator this step!",
                        getOperatorId(), assignedJiaKeCountyManagerId));

//        ParamException.isTrue(preCheckApplication.getAreaId3().equals(operatorInfo.getAreaId3()) && "hdict003".equals(operatorInfo.getRoleId()),
//                String.format("user[CRMId=%s, areaId3=%s, roleName=%s] doesn't have access to operate this step!", 上一步已做校验，无需重复校验
//                        operatorInfo.getLoginId(), operatorInfo.getAreaId3(), operatorInfo.getRoleName()));
        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
    }

    public PreCheckApplication getPreCheckApplication() {
        return preCheckApplication;
    }

    public void setPreCheckApplication(PreCheckApplication preCheckApplication) {
        this.preCheckApplication = preCheckApplication;
    }

    public String getAssignedJiaKeCountyManagerId() {
        return assignedJiaKeCountyManagerId;
    }

    public void setAssignedJiaKeCountyManagerId(String assignedJiaKeCountyManagerId) {
        this.assignedJiaKeCountyManagerId = assignedJiaKeCountyManagerId;
    }

    public Boolean getPreCheckApplicationPassedByWhiteCollar() {
        return preCheckApplicationPassedByWhiteCollar;
    }

    public void setPreCheckApplicationPassedByWhiteCollar(Boolean preCheckApplicationPassedByWhiteCollar) {
        this.preCheckApplicationPassedByWhiteCollar = preCheckApplicationPassedByWhiteCollar;
    }
}
