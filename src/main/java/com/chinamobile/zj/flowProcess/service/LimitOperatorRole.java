package com.chinamobile.zj.flowProcess.service;

/**
 * 步骤的操作人权限有限制，需要做权限检查
 */
public interface LimitOperatorRole {

    /**
     * 检查用户的操作权限
     */
    void checkOperatorAccessRight();
}
