package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.enums.ReviewOperationResultEnum;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InputParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.OutputParam;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.entity.PreCheckApplication;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.*;

public class ReviewOrAssignApplicationByCountyHdictUserTaskService extends AbstractHdictReviewUserTaskService {

    @OutputParam
    private Boolean hasCountyManager;

    @OutputParam
    private Boolean preCheckApplicationPassedByWhiteCollar;

    /**
     * 县HDICT用户CRM编号，供后面步骤使用
     */
    @OutputParam
    private String countyHdictUserId;

    /**
     * 县HDICT专员转派的家客经理的用户ID
     */
    @InputParam
    private String assignedJiaKeCountyManagerId;

    @OutputParam
    private String assignedJiaKeCountyManagerName;

    @Override
    public String getDefinitionKey() {
        return "review_or_assign_application_by_county_hdict";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "县HDICT专员进行指派或审核预勘需求";
    }

    @Override
    public ExecutionResult execute() {

        PreCheckApplication preCheckApplication = getPreCheckApplication();
        ParamException.isTrue(Objects.isNull(preCheckApplication) || StringUtils.isBlank(preCheckApplication.getAreaId3()),
                String.format("inputParam[preCheckApplication.areaId3] should not be blank"));

        // 必须设置 hasCountyManager，否则流程走向无法判断
        List<HdictUserInfoDO> jiaKeManagerInfoList = getUserInfoService().listByUserRoleId(preCheckApplication.getAreaId3(), "hdict003"); // roleId='hdict003', roleName='家客经理-县市'
        setHasCountyManager(CollectionUtils.isNotEmpty(jiaKeManagerInfoList));
        if (Boolean.TRUE.equals(getHasCountyManager())) {
            // 不允许审核驳回，必须进行转派。
            preCheckApplicationPassedByWhiteCollar = false;
            ParamException.isTrue(ReviewOperationResultEnum.REJECTED.getNameEn().equals(getOperationResult()),
                    String.format("county[areaId3=%s] has jiaKeManager, not allowed to do reject operation", preCheckApplication.getAreaId3()));
            // 该县不存在家客经理，必须进行转派。转派对象不能为空
            ParamException.isTrue(StringUtils.isBlank(assignedJiaKeCountyManagerId),
                    String.format("inputParam[assignedJiaKeCountyManagerId] should not be blank"));
            // 检查用户指定的家客经理：角色、归属县市
            Optional<HdictUserInfoDO> jiaKeManagerInfoOpt = getUserInfoService().getByUserCRMId(assignedJiaKeCountyManagerId);
            ParamException.isTrue(BooleanUtils.isNotTrue(jiaKeManagerInfoOpt.isPresent()),
                    String.format("invalid userId[%s], user not exist.", getOperatorId()));
            HdictUserInfoDO jiaKeManagerInfo = jiaKeManagerInfoOpt.get();
            ParamException.isTrue(BooleanUtils.isNotTrue(preCheckApplication.getAreaId3().equals(jiaKeManagerInfo.getAreaId3()) && "hdict003".equals(jiaKeManagerInfo.getRoleId())),
                    String.format("user[CRMId=%s, areaId3=%s, roleName=%s] is not a valid target user!", jiaKeManagerInfo.getLoginId(), jiaKeManagerInfo.getAreaId3(), jiaKeManagerInfo.getRoleName()));
            setAssignedJiaKeCountyManagerName(jiaKeManagerInfo.getName());
        } else {
            // 该县不存在家客经理，必须由本步骤操作人进行审核动作。
            // 若进行的是完成操作，operationResult=finished，不符合要求
            ReviewOperationResultEnum.getByNameEn(getOperationResult()); // 检查必须经过审核
            preCheckApplicationPassedByWhiteCollar = OrderInstanceStatusEnum.PASSED.getNameEn().equals(getStatus());
        }

        // 当前步骤操作人
//        List<HdictUserInfoDO> operatorInfoList = userInfoService.listByUserRoleId(preCheckApplication.getAreaId3(), "hdict006"); // roleId='hdict006', roleName='HDICT专员-县市'
//        InternalException.isTrue(1 != CollectionUtils.size(operatorInfoList),
//                // 一个县市应该有且只有一个"HDICT专员-县市", 但找到多个。请检查sql 或联系DBA检查数据是否正确
//                String.format("%s should have one and only one user, but got [%s]. Please contact the DBA administrator to check database data", "HDICT专员-县市", operatorInfoList.size()));
//        HdictUserInfoDO operatorInfo = operatorInfoList.get(0);
//        setOperatorId(operatorInfo.getLoginId());
//        setOperatorName(operatorInfo.getName());
//        setOperatorRoleName(operatorInfo.getRoleName());


        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public void checkOperatorAccessRight() {
        super.checkOperatorAccessRight();

        setCountyHdictUserId(getOperatorId());
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.singletonMap("hdict006", "HDICT专员-县市");
    }

    @Override
    public String getOperationOutputDesc() {
        if (Objects.nonNull(assignedJiaKeCountyManagerId) || BooleanUtils.isTrue(hasCountyManager)) {
            return MessageFormat.format("{0}（{1}）将预勘需求的审核任务指派给{2}（{3}）",
                    getOperatorRoleName(), getOperatorName(),
                    "家客经理-县市", getAssignedJiaKeCountyManagerName());
        } else {
            // 步骤未结束时，status==null
            String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                    OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
            return MessageFormat.format("{0}（{1}）对预勘需求审核{2}",
                    getOperatorRoleName(), getOperatorName(), operationStatus);
        }
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

    public String getAssignedJiaKeCountyManagerName() {
        return assignedJiaKeCountyManagerName;
    }

    public void setAssignedJiaKeCountyManagerName(String assignedJiaKeCountyManagerName) {
        this.assignedJiaKeCountyManagerName = assignedJiaKeCountyManagerName;
    }

    public Boolean getPreCheckApplicationPassedByWhiteCollar() {
        return preCheckApplicationPassedByWhiteCollar;
    }

    public void setPreCheckApplicationPassedByWhiteCollar(Boolean preCheckApplicationPassedByWhiteCollar) {
        this.preCheckApplicationPassedByWhiteCollar = preCheckApplicationPassedByWhiteCollar;
    }
}
