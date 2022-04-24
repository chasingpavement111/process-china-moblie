package com.chinamobile.zj.flowProcess.enums;

import com.chinamobile.zj.comm.ParamException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务管理看板-工单流程节点类型
 * </p>
 *
 * @author zhangjie
 * @since 2021-11-01
 */
public enum ResourceInstanceTypeEnum {

    ASSIGNMENT("assignment", "派单", true),
    EXECUTION_DISPATCH_POINT("execution_dispatch_point", "任务的汇总点", false), // 标识内部所有execution的审核总结果：所有下级execution都完成反馈、且审核通过时，本节点才会通过。
    EXECUTION("execution", "反馈任务", true),
    REVIEW_DISPATCH_POINT("review_dispatch_point", "审核的汇总点", false), // 标识同级别审批人的审核总结果：所有下级review都审核通过时，本节点才会通过。
    REVIEW("review", "审批", true),

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
     * 是叶子结点
     */
    private boolean isLeaf;

    public static boolean isDispatchType(String nameEn) {
        ParamException.isTrue(StringUtils.isBlank(nameEn), String.format("invalid blank nodeType: [%s]", nameEn));
        List<ResourceInstanceTypeEnum> dispatchTypeList = Arrays.stream(values()).filter(type -> BooleanUtils.isNotTrue(type.isLeaf)).collect(Collectors.toList());
        for (ResourceInstanceTypeEnum elem : dispatchTypeList) {
            if (elem.getNameEn().equals(nameEn)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExecutionLikeType(String nodeType) {
        if (EXECUTION_DISPATCH_POINT.getNameEn().equals(nodeType)) {
            return true;
        } else if (EXECUTION.getNameEn().equals(nodeType)) {
            return true;
        }
        return false;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameCh() {
        return nameCh;
    }

    ResourceInstanceTypeEnum(String nameEn, String nameCh, boolean isLeaf) {
        this.nameEn = nameEn;
        this.nameCh = nameCh;
        this.isLeaf = isLeaf;
    }

    public static ResourceInstanceTypeEnum getByNameEn(String nameEn) {
        ParamException.isTrue(StringUtils.isBlank(nameEn), String.format("invalid blank nodeType: [%s]", nameEn));
        for (ResourceInstanceTypeEnum elem : values()) {
            if (nameEn.equals(elem.nameEn)) {
                return elem;
            }
        }
        throw new ParamException(String.format("invalid nodeType: [%s]", nameEn));
    }
}
