package com.chinamobile.zj.service.interfaces.user;

import com.baomidou.mybatisplus.service.IService;
import com.chinamobile.zj.entity.user.WgUserInfo;

import java.util.Optional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangjie
 * @since 2022-01-20
 */
public interface WgUserInfoInCamelFormatService extends IService<WgUserInfo> {

    Optional<WgUserInfo> getByUserCRMId(String userCRMId);
}
