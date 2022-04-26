package com.chinamobile.zj.flowProcess.enums;

import com.chinamobile.zj.comm.ParamException;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum OrderStatusEnum {

    READY("ready", "待处理"),
    PROCESSING("processing", "流转中"),
    FINISHED("finished", "已完结"),
    CANCELED("canceled", "已废止"),
    ;

    public static final List<String> UNFINISHED_STATUS_NAME_EN_LIST = Arrays.asList(READY.getNameEn(), PROCESSING.getNameEn());

    /**
     * 状态-英文
     */
    private String nameEn;
    /**
     * 状态-中文
     */
    private String nameCh;

    OrderStatusEnum(String nameEn, String nameCh) {
        this.nameEn = nameEn;
        this.nameCh = nameCh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public static OrderStatusEnum getByNameEn(String nameEn) {
        ParamException.isTrue(StringUtils.isBlank(nameEn), String.format("invalid blank orderStatus: [%s]", nameEn));
        for (OrderStatusEnum elem : values()) {
            if (nameEn.equals(elem.nameEn)) {
                return elem;
            }
        }
        throw new ParamException(String.format("invalid orderStatus: [%s]", nameEn));
    }
}
