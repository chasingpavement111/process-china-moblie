package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InheritParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InputParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.LimitOperatorRole;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.entity.PreCheckApplication;
import com.chinamobile.zj.hdict.service.interfaces.HdictUserInfoService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AssignConstructionByCountyTieTongHdictUserTaskService extends BaseUserTaskService implements LimitOperatorRole {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InheritParam
    private PreCheckApplication preCheckApplication;

    /**
     * 县铁通hdict的用户CRM编号
     */
    @InheritParam
    private String countyTieTongHdictUserId;

    /**
     * 县铁通hdict专员转派进行施工的装维人员的用户ID
     */
    @InputParam
    private String assignedConstructorId;

    @OutputParam
    private String assignedConstructorName;

    @Override
    public String getDefinitionKey() {
        return "assign_construction_by_county_tie_tong_hdict";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "县铁通HDICT专员派单施工";
    }

    @Override
    public ExecutionResult execute() {
        ParamException.isTrue(Objects.isNull(preCheckApplication) || StringUtils.isBlank(preCheckApplication.getAreaId3()),
                String.format("inputParam[preCheckApplication.areaId3] should not be blank"));

        // 进行转派。转派对象不能为空
        ParamException.isTrue(StringUtils.isBlank(assignedConstructorId),
                String.format("inputParam[assignedConstructorId] should not be blank"));
        // 检查用户指定的装维人员：角色、归属县市
        Optional<HdictUserInfoDO> maintainerInfoOpt = userInfoService.getByUserCRMId(assignedConstructorId);
        ParamException.isTrue(BooleanUtils.isNotTrue(maintainerInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO maintainerInfo = maintainerInfoOpt.get();
        ParamException.isTrue(BooleanUtils.isNotTrue(preCheckApplication.getAreaId3().equals(maintainerInfo.getAreaId3()) && "hdict002".equals(maintainerInfo.getRoleId())),
                String.format("user[CRMId=%s, areaId3=%s, roleName=%s] is not a valid target user!", maintainerInfo.getLoginId(), maintainerInfo.getAreaId3(), maintainerInfo.getRoleName()));
        setAssignedConstructorName(maintainerInfo.getName());

        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public void checkOperatorAccessRight() {
        Optional<HdictUserInfoDO> operatorInfoOpt = userInfoService.getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        // 只允许 (同一个)县铁通hdict 进行施工派单
        ParamException.isTrue(!getCountyTieTongHdictUserId().equals(operatorInfo.getLoginId()),
                String.format("operator[CRMId=%s] is not the county tie tong hdict user, only user[CRMId=%s] can operator this step!",
                        getOperatorId(), getCountyTieTongHdictUserId()));

        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
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
        return MessageFormat.format("{0}（{1}）对施工任务进行派单{2}", getOperatorRoleName(), getOperatorName(), operationStatus);
    }

    public PreCheckApplication getPreCheckApplication() {
        return preCheckApplication;
    }

    public void setPreCheckApplication(PreCheckApplication preCheckApplication) {
        this.preCheckApplication = preCheckApplication;
    }

    public String getCountyTieTongHdictUserId() {
        return countyTieTongHdictUserId;
    }

    public void setCountyTieTongHdictUserId(String countyTieTongHdictUserId) {
        this.countyTieTongHdictUserId = countyTieTongHdictUserId;
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
