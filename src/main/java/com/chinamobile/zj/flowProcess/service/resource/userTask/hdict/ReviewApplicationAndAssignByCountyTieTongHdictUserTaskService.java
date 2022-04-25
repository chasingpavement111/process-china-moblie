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

public class ReviewApplicationAndAssignByCountyTieTongHdictUserTaskService extends BaseUserTaskService implements LimitOperatorRole {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InputParam
    private PreCheckApplication preCheckApplication;

    @InputParam
    private Boolean preCheckApplicationPassedByBlueCollar;


    @Override
    public String getDefinitionKey() {
        return "review_application_and_assign_by_county_tie_tong_hdict";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "县铁通HDICT专员审核预勘需求与派单";
    }

    @Override
    public ExecutionResult execute() {
        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict005", "HDICT专员-县市铁通");
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
        ParamException.isTrue(preCheckApplication.getAreaId3().equals(operatorInfo.getAreaId3()) && "hdict005".equals(operatorInfo.getRoleId()),
                String.format("user[CRMId=%s, areaId3=%s, roleName=%s] doesn't have access to operate this step!",
                        operatorInfo.getLoginId(), operatorInfo.getAreaId3(), operatorInfo.getRoleName()));
        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
    }

    public PreCheckApplication getPreCheckApplication() {
        return preCheckApplication;
    }

    public void setPreCheckApplication(PreCheckApplication preCheckApplication) {
        this.preCheckApplication = preCheckApplication;
    }

    public Boolean getPreCheckApplicationPassedByBlueCollar() {
        return preCheckApplicationPassedByBlueCollar;
    }

    public void setPreCheckApplicationPassedByBlueCollar(Boolean preCheckApplicationPassedByBlueCollar) {
        this.preCheckApplicationPassedByBlueCollar = preCheckApplicationPassedByBlueCollar;
    }
}
