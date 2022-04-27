package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InputParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ReviewApplicationAndAssignByCountyTieTongHdictUserTaskService extends AbstractHdictReviewUserTaskService {

    /**
     * 县铁通HDICT专员转派进行现场审查的装维人员的用户ID
     */
    @InputParam
    private String assignedMaintainerId;

    @OutputParam
    private String assignedMaintainerName;

    /**
     * 县铁通hdict的用户CRM编号
     */
    @OutputParam
    private String countyTieTongHdictUserId;

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
        // 检查用户指定的装维人员：角色、归属县市
        Optional<HdictUserInfoDO> maintainerInfoOpt = getUserInfoService().getByUserCRMId(assignedMaintainerId);
        ParamException.isTrue(BooleanUtils.isNotTrue(maintainerInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO maintainerInfo = maintainerInfoOpt.get();
        ParamException.isTrue(BooleanUtils.isNotTrue(getPreCheckApplication().getAreaId3().equals(maintainerInfo.getAreaId3()) && "hdict002".equals(maintainerInfo.getRoleId())),
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
        return MessageFormat.format("{0}（{1}）对预勘需求申请进行审核与对现场审核进行派单{2}", getOperatorRoleName(), getOperatorName(), operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        super.checkOperatorAccessRight();

        setCountyTieTongHdictUserId(getOperatorId());
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

    public String getCountyTieTongHdictUserId() {
        return countyTieTongHdictUserId;
    }

    public void setCountyTieTongHdictUserId(String countyTieTongHdictUserId) {
        this.countyTieTongHdictUserId = countyTieTongHdictUserId;
    }
}
