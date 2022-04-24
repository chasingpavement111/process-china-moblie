package com.chinamobile.zj.hdict.service.interfaces;

import com.baomidou.mybatisplus.service.IService;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;

import java.util.List;
import java.util.Optional;

public interface HdictUserInfoService extends IService<HdictUserInfoDO> {

    Optional<HdictUserInfoDO> getByUserCRMId(String userCRMId);

    List<HdictUserInfoDO> listByUserRoleId(String areaId3, String roleId);
}
