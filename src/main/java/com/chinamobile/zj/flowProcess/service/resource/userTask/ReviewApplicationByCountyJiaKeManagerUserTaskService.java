package com.chinamobile.zj.flowProcess.service.resource.userTask;

import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class ReviewApplicationByCountyJiaKeManagerUserTaskService extends BaseUserTaskService {

    private Boolean preCheckApplicationPassedByWhiteCollar;

    private Boolean preCheckApplicationPassedByBlueCollar;


    @Override
    public String getDefinitionKey() {
        return "review_application_by_county_jia_ke_manager";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "家客经理审核预勘需求";
    }

    @Override
    public ExecutionResult execute() {
        return null;// todo zj
    }

    @Override
    public List<String> supportedOperatorRoleList() {
        return Collections.singletonList("家客经理-县市");
    }

    @Override
    public String getOperationOutputDesc() {
        return MessageFormat.format("{0}（{1}）对预勘需求申请{2}", supportedOperatorRoleList(), getOperatorName(),
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh());
    }

}
