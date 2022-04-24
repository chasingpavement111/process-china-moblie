package com.chinamobile.zj.flowProcess.service.resource.userTask;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.flowProcess.bo.ExecutionResult;
import com.chinamobile.zj.flowProcess.service.resource.BaseUserTaskService;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.service.interfaces.HdictUserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

/**
 * 提交预勘申请
 */
public class SubmitPreCheckApplicationUserTaskService extends BaseUserTaskService {

    @Autowired
    private HdictUserInfoService userInfoService;

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


        return null;
    }

    @Override
    public List<String> supportedOperatorRoleList() {
        return Collections.EMPTY_LIST; // 空集合，标识支持所有类型的用户
    }

    @Override
    public String getOperationOutputDesc() {
        return MessageFormat.format("{0}（{1}）提交预勘申请", getOperatorRoleName(), getOperatorName());
    }
}
