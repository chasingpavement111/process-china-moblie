package com.chinamobile.zj.flowProcess.enums;

import com.chinamobile.zj.comm.ParamException;
import org.apache.commons.lang.StringUtils;

public enum ReviewOperationResultEnum {

    REJECTED("reject", "驳回", OrderInstanceStatusEnum.REJECTED),
    PASSED("pass", "通过", OrderInstanceStatusEnum.PASSED),
    ;

    /**
     * 状态-英文
     */
    private String nameEn;
    /**
     * 状态-中文
     */
    private String nameCh;
    /**
     * 审核结果 对应 实例状态
     */
    private OrderInstanceStatusEnum correspondingInstanceStatusEnum;

    ReviewOperationResultEnum(String nameEn, String nameCh, OrderInstanceStatusEnum correspondingInstanceStatusEnum) {
        this.nameEn = nameEn;
        this.nameCh = nameCh;
        this.correspondingInstanceStatusEnum = correspondingInstanceStatusEnum;
    }

    public static ReviewOperationResultEnum getByNameEn(String nameEn) {
        ParamException.isTrue(StringUtils.isBlank(nameEn), String.format("invalid blank operationResult: [%s]", nameEn));
        for (ReviewOperationResultEnum elem : values()) {
            if (nameEn.equals(elem.nameEn)) {
                return elem;
            }
        }
        throw new ParamException(String.format("invalid operationResult: [%s]", nameEn));
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameCh() {
        return nameCh;
    }

    public OrderInstanceStatusEnum getCorrespondingInstanceStatusEnum() {
        return correspondingInstanceStatusEnum;
    }
}
