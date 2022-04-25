package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
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

/**
 * 提交预勘申请
 */
public class SubmitPreCheckApplicationUserTaskService extends BaseUserTaskService implements LimitOperatorRole {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InputParam
    private PreCheckApplication preCheckApplication;

    @Override
    public String getDefinitionKey() {
        return "submit_pre_check_application";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "提交预勘申请";
    }

    @Override
    public ExecutionResult execute() {
        Optional<HdictUserInfoDO> operatorInfoOpt = userInfoService.getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    /**
     * todo zj

     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict018', '铁通仓管', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict001', '网格人员', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict002', 'HDICT装维', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict003', '家客经理-县市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict004', '家客经理-地市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict005', 'HDICT专员-县市铁通', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict006', 'HDICT专员-县市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict007', 'HDICT专员-地市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict008', '家宽主管-县市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict009', '家宽主管-地市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict010', '市场部经理-县市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict011', '市场分管副总-县市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict012', '市场分管副总-地市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict013', '网络HDICT管理员-县市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict014', '网络全业务主管-县市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict015', '网络全业务主管-地市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict016', '网络分管副总-地市', null);
     INSERT INTO WJJ_HDICT_ROLE (ROLE_ID, ROLE_NAME, ROLE_MENUS) VALUES ('hdict017', '网络家客产品支撑-地市', null);
     */

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.EMPTY_MAP; // 空集合，标识支持所有类型的用户
    }

    @Override
    public String getOperationOutputDesc() {
        return MessageFormat.format("{0}（{1}）提交预勘申请", getOperatorRoleName(), getOperatorName());
    }

    @Override
    public void checkOperatorAccessRight() {
        // 校验本步骤的操作人员，只能是最初的提交人
        ParamException.isTrue(!preCheckApplication.getCreatorId().equals(getOperatorId()),
                String.format("user[CRMId=%s] don't have operation right, application can only be applied by user[CRMId=%s]",
                        getOperatorId(), preCheckApplication.getCreatorId()));

    }

    public PreCheckApplication getPreCheckApplication() {
        return preCheckApplication;
    }

    public void setPreCheckApplication(PreCheckApplication preCheckApplication) {
        this.preCheckApplication = preCheckApplication;
    }
}
