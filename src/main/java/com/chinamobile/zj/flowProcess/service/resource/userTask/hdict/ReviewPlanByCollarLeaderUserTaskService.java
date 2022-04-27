package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InheritParam;
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
import java.util.Optional;

public class ReviewPlanByCollarLeaderUserTaskService extends AbstractHdictReviewUserTaskService {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InheritParam
    private PreCheckApplication preCheckApplication;

    @InheritParam
    private Boolean hasCountyManager;

    /**
     * 县HDICT用户CRM编号
     */
    @InheritParam
    private String countyHdictUserId;

    /**
     * 多个步骤的流程控制变量，planPassedByWhiteCollar==false时都走到同一个下一跳“家客经理或县HDICT专员提交方案”
     */
    @OutputParam // todo zj 区分入参、出参，不对出参进行变量初始化？ 也要看时机吧，未执行前不初始化？
    private Boolean planPassedByWhiteCollar;

    @OutputParam
    private Boolean canceledByCountyHdict;

    @Override
    public String getDefinitionKey() {
        return "review_plan_by_collar_leader";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "县HDICT专员或市家宽主管审核方案";
    }

    @Override
    public ExecutionResult execute() {
        if (OrderInstanceStatusEnum.CANCELED.getNameEn().equals(getOperationResult())) {
            // 县Hdict 进行 强制废止动作
            canceledByCountyHdict = true;
            return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
        }
        canceledByCountyHdict = false;

        ReviewOperationResultEnum.getByNameEn(getOperationResult()); // 检查必须经过审核
        if (ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult())) {
            // 审核不通过，直接返回
            planPassedByWhiteCollar = false;
        } else {
            planPassedByWhiteCollar = true;
        }

        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        if (hasCountyManager) {
            return Collections.singletonMap("hdict006", "HDICT专员-县市");
        } else {
            return Collections.singletonMap("hdict008", "家宽主管-县市");
        }
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return MessageFormat.format("{0}（{1}）对方案审核{2}", getOperatorRoleName(), getOperatorName(),
                operationStatus);
    }

    @Override
    public void checkOperatorAccessRight() {
        if (OrderInstanceStatusEnum.CANCELED.getNameEn().equals(getOperationResult())) {
            // 只允许 县Hdict 在任意阶段，进行“强制废止”动作
            ParamException.isTrue(!countyHdictUserId.equals(getOperatorId()),
                    String.format("operator[CRMId=%s] is not the county hdict user, only user[CRMId=%s] can do cancel operation!",
                            getOperatorId(), countyHdictUserId));
            return;
        }

        Optional<HdictUserInfoDO> operatorInfoOpt = userInfoService.getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        if (hasCountyManager) {
            // 只允许 （同一个）县Hdict 对 家客经理提交的方案 进行审核
            ParamException.isTrue(!countyHdictUserId.equals(getOperatorId()),
                    String.format("operator[CRMId=%s] is not the county hdict user, only user[CRMId=%s] can operator this step!",
                            getOperatorId(), countyHdictUserId));
        } else {
            // 只允许 县家宽主管 对 县hdict提交的方案 进行审核
            ParamException.isTrue(BooleanUtils.isNotTrue(preCheckApplication.getAreaId3().equals(operatorInfo.getAreaId3()) && supportedOperatorRoleMap().containsKey(operatorInfo.getRoleId())),
                    String.format("user[CRMId=%s, areaId3=%s, roleName=%s] doesn't have access to operate this step!",
                            operatorInfo.getLoginId(), operatorInfo.getAreaId3(), operatorInfo.getRoleName()));
        }

        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
    }

    public PreCheckApplication getPreCheckApplication() {
        return preCheckApplication;
    }

    public void setPreCheckApplication(PreCheckApplication preCheckApplication) {
        this.preCheckApplication = preCheckApplication;
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

    public Boolean getPlanPassedByWhiteCollar() {
        return planPassedByWhiteCollar;
    }

    public void setPlanPassedByWhiteCollar(Boolean planPassedByWhiteCollar) {
        this.planPassedByWhiteCollar = planPassedByWhiteCollar;
    }

    public Boolean getCanceledByCountyHdict() {
        return canceledByCountyHdict;
    }

    public void setCanceledByCountyHdict(Boolean canceledByCountyHdict) {
        this.canceledByCountyHdict = canceledByCountyHdict;
    }
}
