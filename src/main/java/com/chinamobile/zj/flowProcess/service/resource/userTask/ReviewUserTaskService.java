package com.chinamobile.zj.flowProcess.service.resource.userTask;

import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.enums.OrderInstanceStatusEnum;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Map;

public class ReviewUserTaskService extends BaseUserTaskService {

    @Override
    public String getDefinitionKey() {
        return "review";
    }

    @Override
    public String getDefinitionKeyDesc() {
        return "审核";
    }

    @Override
    public ExecutionResult execute() {
        return new ExecutionResult(ExecutionResult.RESULT_CODE.SUCCESS);
    }

    @Override
    public String getOperationOutputDesc() {
        // 步骤未结束时，status==null
        String operationStatus = StringUtils.isBlank(getStatus()) ? OrderInstanceStatusEnum.PROCESSING.getNameCh() :
                OrderInstanceStatusEnum.getByNameEn(getStatus()).getNameCh();
        return String.format("审核%s", operationStatus);
    }

    @Override
    public Map<String, String> supportedOperatorRoleMap() {
        return Collections.EMPTY_MAP;
    }
}
