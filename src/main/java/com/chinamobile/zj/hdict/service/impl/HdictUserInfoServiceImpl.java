package com.chinamobile.zj.hdict.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.hdict.entity.HdictUserInfoDO;
import com.chinamobile.zj.hdict.mapper.HdictUserInfoMapper;
import com.chinamobile.zj.hdict.service.interfaces.HdictUserInfoService;
import com.chinamobile.zj.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangjie
 * @since 2022-01-20
 */
@Service
@Primary
public class HdictUserInfoServiceImpl extends ServiceImpl<HdictUserInfoMapper, HdictUserInfoDO> implements HdictUserInfoService {

    @Autowired
    private HdictUserInfoMapper userInfoMapper;

    @Override
    public Optional<HdictUserInfoDO> getByUserCRMId(String userCRMId) {
        ParamException.isTrue(StringUtils.isBlank(userCRMId), "inputParam[userCRMId] should not be blank");
        HdictUserInfoDO info = userInfoMapper.getByUserCRMId(userCRMId);
        updateImg(info);
        return Optional.ofNullable(info);
    }

    private void updateImg(HdictUserInfoDO info) {
        if (Objects.nonNull(info) && StringUtils.isNotBlank(info.getImg())) {
            // ftp路径，去除路径前缀只取文件名 —— 莫名其妙就文件名竟然可以得到文件。其次安全要求不允许包含‘/’，故若存在会被作为特殊字符进行拦截，不允许获取文件
            String img = info.getImg();
            int startIndex = img.lastIndexOf(Constants.SEPARATOR_OF_UNIX);
            String imgWithoutPrefixPath = img.substring(startIndex + 1);
            info.setImg(imgWithoutPrefixPath);
        }
    }

    @Override
    public List<HdictUserInfoDO> listByUserRoleId(String areaId3, String roleId) {
        ParamException.isTrue(StringUtils.isBlank(areaId3), "inputParam[areaId3] should not be blank");
        ParamException.isTrue(StringUtils.isBlank(roleId), "inputParam[roleId] should not be blank");
        List<HdictUserInfoDO> infoList = userInfoMapper.listByUserRoleId(areaId3, roleId);
        if (CollectionUtils.isEmpty(infoList)) {
            // 允许空，如某个县市下可能不存在家客经理
            return Collections.EMPTY_LIST;
        }
//        ParamException.isTrue(CollectionUtils.isEmpty(infoList),
//                String.format("invalid roleId[%s], user of this role is not found", roleId));
        infoList.forEach(info -> updateImg(info));
        return infoList;
    }
}
