package com.chinamobile.zj.flowProcess.enums;
import com.chinamobile.zj.comm.ParamException;
import com.chinamobile.zj.util.JsonConvertUtil;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * 元素类型
 */
public enum StencilEnum {

    START_NONE_EVENT("StartNoneEvent", "开始"),
    THROW_NONE_EVENT("ThrowNoneEvent", "结束"),
    USER_TASK("UserTask", "用户任务"),
    EXCLUSIVE_GATEWAY("ExclusiveGateway", "排他网关"),
    SEQUENCE_FLOW("SequenceFlow", "连线"),
    ;

    private String id;

    private String name;

    StencilEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static StencilEnum getById(String idMapStr) {
        StencilId obj = JsonConvertUtil.parseToObject(idMapStr, StencilId.class);
        ParamException.isTrue(Objects.isNull(obj) || StringUtils.isBlank(obj.id),
                String.format("invalid blank id: [%s]", idMapStr));
        String id = obj.getId();
        for (StencilEnum elem : values()) {
            if (id.equals(elem.id)) {
                return elem;
            }
        }
        throw new ParamException(String.format("invalid id: [%s]", id));
    }

    @Data
    public static class StencilId {
        private String id;
    }
}