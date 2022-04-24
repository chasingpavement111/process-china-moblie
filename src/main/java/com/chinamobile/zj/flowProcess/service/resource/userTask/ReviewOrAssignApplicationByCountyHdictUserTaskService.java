package com.chinamobile.zj.flowProcess.service.resource.userTask;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.service.InputParam;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.entity.PreCheckApplication;
import com.chinamobile.zj.hdict.service.interfaces.HdictUserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReviewOrAssignApplicationByCountyHdictUserTaskService extends BaseUserTaskService {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InputParam // todo zj 每个流程实例，都要注入一份bean....考虑合适销毁：流程结束后
    private PreCheckApplication preCheckApplication;

    private Boolean hasCountyManager;

    private String jiaKeCountyManagerId;

    private String jiaKeCountyManagerName;

    public void init() {
        // todo zj 或者反正
        // 当前步骤操作人
        List<HdictUserInfoDO> operatorInfoList = userInfoService.listByUserRoleId(preCheckApplication.getAreaId3(), "hdict006"); // roleId='hdict006', roleName='HDICT专员-县市'
        InternalException.isTrue(1 != CollectionUtils.size(operatorInfoList),
                // 一个县市应该有且只有一个"HDICT专员-县市", 但找到多个。请检查sql 或联系DBA检查数据是否正确
                String.format("%s should have one and only one user, but got [%s]. Please contact the DBA administrator to check database data", "HDICT专员-县市", operatorInfoList.size()));
        HdictUserInfoDO operatorInfo = operatorInfoList.get(0);
        setOperatorId(operatorInfo.getLoginId());
        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());

        // 是否有家客经理 -> 终于流程走向判断
        List<HdictUserInfoDO> jiaKeManagerInfoList = userInfoService.listByUserRoleId(preCheckApplication.getAreaId3(), "hdict003"); // roleId='hdict003', roleName='家客经理-县市'
        setHasCountyManager(CollectionUtils.isNotEmpty(jiaKeManagerInfoList));
    }

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
        // 当前步骤操作人
//        List<HdictUserInfoDO> operatorInfoList = userInfoService.listByUserRoleId(preCheckApplication.getAreaId3(), "hdict006"); // roleId='hdict006', roleName='HDICT专员-县市'
//        InternalException.isTrue(1 != CollectionUtils.size(operatorInfoList),
//                // 一个县市应该有且只有一个"HDICT专员-县市", 但找到多个。请检查sql 或联系DBA检查数据是否正确
//                String.format("%s should have one and only one user, but got [%s]. Please contact the DBA administrator to check database data", "HDICT专员-县市", operatorInfoList.size()));
//        HdictUserInfoDO operatorInfo = operatorInfoList.get(0);
//        setOperatorId(operatorInfo.getLoginId());
//        setOperatorName(operatorInfo.getName());
//        setOperatorRoleName(operatorInfo.getRoleName());
        // 检查用户的操作权限：必须为与发起人同县市的县Hdict专员
        Optional<HdictUserInfoDO> operatorInfoOpt = userInfoService.getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid operatorId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        ParamException.isTrue(preCheckApplication.getAreaId3().equals(operatorInfo.getAreaId3()) && "".equals(operatorInfo.getRoleName()),
                String.format("user[CRMId=%s, areaId3=%s, roleName=%s] doesn't have access to operate this step!", operatorInfo.getLoginId(), operatorInfo.getAreaId3(), operatorInfo.getRoleName()));
        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());

        // 必须设置 hasCountyManager，否则流程走向无法判断
        List<HdictUserInfoDO> jiaKeManagerInfoList = userInfoService.listByUserRoleId(preCheckApplication.getAreaId3(), "hdict003"); // roleId='hdict003', roleName='家客经理-县市'
        setHasCountyManager(CollectionUtils.isNotEmpty(jiaKeManagerInfoList));

        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public List<String> supportedOperatorRoleList() {
        return Collections.singletonList("HDICT专员-县市");
    }

    @Override
    public String getOperationOutputDesc() {
        if (Objects.isNull(hasCountyManager)) {
            return "";// todo zj
        } else if (hasCountyManager) {
            return MessageFormat.format("{0}（{1}）将预勘需求指派给{2}（{3}）进行审核",
                    getOperatorRoleName(), getOperatorName(),
                    "家客经理-县市", getJiaKeCountyManagerName());
        } else {
            return MessageFormat.format("{0}（{1}）对预勘需求审核{2}",
                    getOperatorRoleName(), getOperatorName(), OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh());
        }
    }

    public Boolean getHasCountyManager() {
        return hasCountyManager;
    }

    public void setHasCountyManager(Boolean hasCountyManager) {
        this.hasCountyManager = hasCountyManager;
    }

    public String getJiaKeCountyManagerId() {
        return jiaKeCountyManagerId;
    }

    public void setJiaKeCountyManagerId(String jiaKeCountyManagerId) {
        this.jiaKeCountyManagerId = jiaKeCountyManagerId;
    }

    public String getJiaKeCountyManagerName() {
        return jiaKeCountyManagerName;
    }

    public void setJiaKeCountyManagerName(String jiaKeCountyManagerName) {
        this.jiaKeCountyManagerName = jiaKeCountyManagerName;
    }
}
