package com.chinamobile.zj.hdict.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.chinamobile.zj.entity.user.WgUserInfo;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HdictUserInfoMapper extends BaseMapper<HdictUserInfoDO> {

    HdictUserInfoDO getByUserCRMId(@Param("userCRMId") String userCRMId);

    List<HdictUserInfoDO> listByUserRoleId(@Param("areaId3") String areaId3, @Param("roleId") String roleId);
}
