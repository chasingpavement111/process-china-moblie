package com.chinamobile.zj.service.impl.user;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.entity.user.WgUserInfo;
import com.chinamobile.zj.mapper.user.WgUserInfoInCamelFormatMapper;
import com.chinamobile.zj.service.interfaces.user.WgUserInfoInCamelFormatService;
import com.chinamobile.zj.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangjie
 * @since 2022-01-20
 */
@Service
@Primary
public class WgUserInfoInCamelFormatServiceImpl extends ServiceImpl<WgUserInfoInCamelFormatMapper, WgUserInfo> implements WgUserInfoInCamelFormatService {

    @Autowired
    private WgUserInfoInCamelFormatMapper userInfoMapper;

    @Override
    public Optional<WgUserInfo> getByUserCRMId(String userCRMId) {
        ParamException.isTrue(StringUtils.isBlank(userCRMId), "inputParam[userCRMId] should not be blank");
        WgUserInfo info = userInfoMapper.getByUserCRMId(userCRMId);
        if (Objects.nonNull(info) && StringUtils.isNotBlank(info.getImg())) {
            // ftp路径，去除路径前缀只取文件名 —— 莫名其妙就文件名竟然可以得到文件。其次安全要求不允许包含‘/’，故若存在会被作为特殊字符进行拦截，不允许获取文件
            String img = info.getImg();
            int startIndex = img.lastIndexOf(Constants.SEPARATOR_OF_UNIX);
            String imgWithoutPrefixPath = img.substring(startIndex + 1);
            info.setImg(imgWithoutPrefixPath);
        }
        return Optional.ofNullable(info);
    }
}
