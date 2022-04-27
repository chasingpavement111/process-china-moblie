package com.chinamobile.zj.flowProcess.service.resource.userTask.hdict;

import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.flowProcess.service.resource.userTask.InheritParam;
import com.chinamobile.zj.flowProcess.service.resource.userTask.LimitOperatorRole;
import com.chinamobile.zj.flowProcess.service.resource.userTask.ReviewTask;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.entity.PreCheckApplication;
import com.chinamobile.zj.hdict.service.interfaces.HdictUserInfoService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class AbstractHdictReviewUserTaskService extends BaseUserTaskService implements LimitOperatorRole, ReviewTask {

    @Autowired
    private HdictUserInfoService userInfoService;

    @InheritParam
    private PreCheckApplication preCheckApplication;

    @Override
    public void checkOperatorAccessRight() {
        Optional<HdictUserInfoDO> operatorInfoOpt = userInfoService.getByUserCRMId(getOperatorId());
        ParamException.isTrue(BooleanUtils.isNotTrue(operatorInfoOpt.isPresent()),
                String.format("invalid userId[%s], user not exist.", getOperatorId()));
        HdictUserInfoDO operatorInfo = operatorInfoOpt.get();
        ParamException.isTrue(BooleanUtils.isNotTrue(getPreCheckApplication().getAreaId3().equals(operatorInfo.getAreaId3())
                        && supportedOperatorRoleMap().containsKey(operatorInfo.getRoleId())),
                String.format("user[CRMId=%s, areaId3=%s, roleName=%s] doesn't have access to operate this step!",
                        operatorInfo.getLoginId(), operatorInfo.getAreaId3(), operatorInfo.getRoleName()));

        setOperatorName(operatorInfo.getName());
        setOperatorRoleName(operatorInfo.getRoleName());
    }

    public HdictUserInfoService getUserInfoService() {
        return userInfoService;
    }

    public PreCheckApplication getPreCheckApplication() {
        return preCheckApplication;
    }

    public void setPreCheckApplication(PreCheckApplication preCheckApplication) {
        this.preCheckApplication = preCheckApplication;
    }
}
