package com.chinamobile.zj.flowProcess.enums;

import com.chinamobile.zj.comm.ParamException;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum OrderInstanceStatusEnum {

//    READY("ready", "待启动", Arrays.asList(ResourceInstanceTypeEnum.REVIEW_DISPATCH_POINT)), 暂时无用状态
    PROCESSING("processing", "进行中", Arrays.asList(ResourceInstanceTypeEnum.EXECUTION_DISPATCH_POINT, ResourceInstanceTypeEnum.EXECUTION, ResourceInstanceTypeEnum.REVIEW, ResourceInstanceTypeEnum.REVIEW_DISPATCH_POINT)),
    FINISHED("finished", "已完成", Arrays.asList(ResourceInstanceTypeEnum.ASSIGNMENT, ResourceInstanceTypeEnum.EXECUTION, ResourceInstanceTypeEnum.EXECUTION_DISPATCH_POINT)),
    REJECTED("rejected", "驳回", Arrays.asList(ResourceInstanceTypeEnum.REVIEW)),
    PASSED("passed", "通过", Arrays.asList(ResourceInstanceTypeEnum.REVIEW, ResourceInstanceTypeEnum.REVIEW_DISPATCH_POINT)),
    ;

    /**
     * 非终态
     */
    public static final List<String> UNFINISHED_STATUS_NAME_EN_LIST = Arrays.asList(PROCESSING.getNameEn()); // READY.getNameEn(),

    /**
     * 状态-英文
     */
    private String nameEn;
    /**
     * 状态-中文
     */
    private String nameCh;
    /**
     * 节点类型集合
     * 表示本状态，可以是这些类型的节点
     */
    private List<ResourceInstanceTypeEnum> nodeTypeEnumList;

    OrderInstanceStatusEnum(String nameEn, String nameCh, List<ResourceInstanceTypeEnum> nodeTypeEnumList) {
        this.nameEn = nameEn;
        this.nameCh = nameCh;
        this.nodeTypeEnumList = nodeTypeEnumList;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameCh() {
        return nameCh;
    }

    public static OrderInstanceStatusEnum getByNameEn(String nameEn) {
        ParamException.isTrue(StringUtils.isBlank(nameEn), String.format("invalid blank instanceStatus: [%s]", nameEn));
        for (OrderInstanceStatusEnum elem : values()) {
            if (nameEn.equals(elem.nameEn)) {
                return elem;
            }
        }
        throw new ParamException(String.format("invalid instanceStatus: [%s]", nameEn));
    }
}
