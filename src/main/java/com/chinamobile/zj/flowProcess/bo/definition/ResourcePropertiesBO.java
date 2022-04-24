package com.chinamobile.zj.flowProcess.bo.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 流程的元素特性
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourcePropertiesBO {

    /**
     * ID
     * 流程元素的唯一标识
     */
    private String overrideid;

    /**
     * 名称
     */
    private String name;

    /**
     * 说明
     */
    private String documentation;

    /**
     * “SequenceFlow”资源类型特有的属性，用于定于SpEL表达式。
     * 设置流转的条件判断
     */
    private ConditionSequenceFlow conditionsequenceflow;

    private Boolean defaultflow;
}