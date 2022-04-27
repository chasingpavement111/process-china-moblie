package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InheritParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.LimitOperatorRole;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.service.interfaces.HdictUserInfoService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class MakePlanByWhiteCollarUserTaskService extends BaseUserTaskService implements LimitOperatorRole {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InheritParam
    private Boolean hasCountyManager;

    /**
     * 县HDICT用户CRM编号
     */
    @InheritParam
    private String countyHdictUserId;

    /**
     * 县HDICT专员转派的家客经理的用户ID
     */
    @InheritParam
    private String assignedJiaKeCountyManagerId;

    @Override
    public String getDefinitionKey() {
        return "make_plan_by_white_collar";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "家客经理或县HDICT专员提交方案";
    }

    @Override
    public ExecutionResult execute() {
        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        if (hasCountyManager) {
            return Collections.singletonMap("hdict003", "家客经理-县市");
        } else {
            return Collections.singletonMap("hdict006", "HDICT专员-县市");
        }
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）提交方案{2}", getOperatorRoleName(), getOperatorName(),
                operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        Optional<HdictUserInfoDO> operatorInfoOpt = userInfoService.getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        if (hasCountyManager) {
            // 只允许 被转派家客经理 操作。
            ParamException.isTrue(!assignedJiaKeCountyManagerId.equals(getOperatorId()),
                    String.format("operator[CRMId=%s] is not the jiaKeManager that countyHdict assigned, only user[CRMId=%s] can operator this step!",
                            getOperatorId(), assignedJiaKeCountyManagerId));
        } else {
            // 只允许  (同一个)县Hdict 操作
            ParamException.isTrue(!countyHdictUserId.equals(getOperatorId()),
                    String.format("operator[CRMId=%s] is not the county hdict user, only user[CRMId=%s] can operator this step!",
                            getOperatorId(), countyHdictUserId));
        }

        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
    }

    public Boolean getHasCountyManager() {
        return hasCountyManager;
    }

    public void setHasCountyManager(Boolean hasCountyManager) {
        this.hasCountyManager = hasCountyManager;
    }

    public String getCountyHdictUserId() {
        return countyHdictUserId;
    }

    public void setCountyHdictUserId(String countyHdictUserId) {
        this.countyHdictUserId = countyHdictUserId;
    }

    public String getAssignedJiaKeCountyManagerId() {
        return assignedJiaKeCountyManagerId;
    }

    public void setAssignedJiaKeCountyManagerId(String assignedJiaKeCountyManagerId) {
        this.assignedJiaKeCountyManagerId = assignedJiaKeCountyManagerId;
    }
}
