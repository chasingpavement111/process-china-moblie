package com.chinamobile.zj.flowProcess.entity;

import com.alibaba.fastjson.TypeReference;
import com.chinamobile.zj.util.JsonConvertUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

@Accessors(chain = true)
@Data
public class WbFlowResourceInstanceDO extends WbFlowResourceInstance {

    private List<String> latterResourceInstanceUuidList; // 非数据库字段，便于与程序交互

    @Override
    public WbFlowResourceInstance setLatterResourceInstanceUuid(String latterResourceInstanceUuid) {
        if (StringUtils.isBlank(latterResourceInstanceUuid)) {
            return this;
        }
        this.latterResourceInstanceUuidList = JsonConvertUtil.parseToParameterizedType(latterResourceInstanceUuid, new TypeReference<List<String>>() {
        });
        return super.setLatterResourceInstanceUuid(latterResourceInstanceUuid);
    }

    public WbFlowResourceInstance setLatterResourceInstanceUuidList(List<String> latterResourceInstanceUuidList) {
        if (CollectionUtils.isEmpty(latterResourceInstanceUuidList)) {
            return this;
        }
        String listStr = JsonConvertUtil.toJsonString(latterResourceInstanceUuidList);
        super.setLatterResourceInstanceUuid(listStr);

        this.latterResourceInstanceUuidList = latterResourceInstanceUuidList;
        return this;
    }
}
