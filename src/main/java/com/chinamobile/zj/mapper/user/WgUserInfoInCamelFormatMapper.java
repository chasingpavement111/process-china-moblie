package com.chinamobile.zj.mapper.user;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.chinamobile.zj.entity.user.WgUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangjie
 * @since 2022-01-20
 */
public interface WgUserInfoInCamelFormatMapper extends BaseMapper<WgUserInfo> {

    WgUserInfo getByUserCRMId(@Param("userCRMId") String userCRMId);
}
