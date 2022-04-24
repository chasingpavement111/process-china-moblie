package com.chinamobile.zj.hdict.entity;

import com.chinamobile.zj.entity.user.WgUserInfo;
import lombok.Data;

/**
 * 预勘申请 hdict模块的用户信息
 */
@Data
public class HdictUserInfoDO extends WgUserInfo {

    /**
     * 角色ID。对应表 wjj_hdict_role.role_id
     */
    private String roleId;

    /**
     * 角色名稱。对应表 wjj_hdict_role.role_name
     */
    private String roleName;
}
