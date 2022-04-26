package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.InputParam;
import com.chinamobile.zj.flowProcess.service.LimitOperatorRole;
import com.chinamobile.zj.flowProcess.service.OutputParam;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
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

public class ReviewApplicationAndAssignByCountyTieTongHdictUserTaskService extends BaseUserTaskService implements LimitOperatorRole {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InputParam
    private PreCheckApplication preCheckApplication;

    /**
     * 县铁通HDICT专员转派的装维人员的用户ID
     */
    @InputParam
    private String assignedMaintainerId;

    @OutputParam
    private String assignedMaintainerName;

    @OutputParam
    private Boolean preCheckApplicationPassedByCountyTieTongHdict;


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
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult())) {
            // 审核不通过，直接返回
            preCheckApplicationPassedByCountyTieTongHdict = false;
            return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
        }

        preCheckApplicationPassedByCountyTieTongHdict = true;
        // 进行转派。转派对象不能为空
        ParamException.isTrue(StringUtils.isBlank(assignedMaintainerId),
                String.format("inputParam[assignedMaintainerId] should not be blank"));
        // 检查用户指定的家客经理：角色、归属县市
        Optional<HdictUserInfoDO> maintainerInfoOpt = userInfoService.getByUserCRMId(assignedMaintainerId);
        ParamException.isTrue(BooleanUtils.isNotTrue(maintainerInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO maintainerInfo = maintainerInfoOpt.get();
        ParamException.isTrue(BooleanUtils.isNotTrue(preCheckApplication.getAreaId3().equals(maintainerInfo.getAreaId3()) && "hdict003".equals(maintainerInfo.getRoleId())),
                String.format("user[CRMId=%s, areaId3=%s, roleName=%s] is not a valid target user!", maintainerInfo.getLoginId(), maintainerInfo.getAreaId3(), maintainerInfo.getRoleName()));
        setAssignedMaintainerName(maintainerInfo.getName());
        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict005", "HDICT专员-县市铁通");
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）对预勘需求申请进行审核与派单{2}", getOperatorRoleName(), getOperatorName(), operationStatus);
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
        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
    }

    public PreCheckApplication getPreCheckApplication() {
        return preCheckApplication;
    }

    public void setPreCheckApplication(PreCheckApplication preCheckApplication) {
        this.preCheckApplication = preCheckApplication;
    }

    public Boolean getPreCheckApplicationPassedByCountyTieTongHdict() {
        return preCheckApplicationPassedByCountyTieTongHdict;
    }

    public void setPreCheckApplicationPassedByCountyTieTongHdict(Boolean preCheckApplicationPassedByCountyTieTongHdict) {
        this.preCheckApplicationPassedByCountyTieTongHdict = preCheckApplicationPassedByCountyTieTongHdict;
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
}
