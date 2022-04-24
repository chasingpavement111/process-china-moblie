package com.chinamobile.zj.flowProcess.bo.definition;

import lombok.Data;

/**
 * “SequenceFlow”连线类型的SpEL表达式定义
 */
@Data
public class ConditionSequenceFlow {

    /**
     * SpEL表达式定义
     */
    private Expression expression;

    @Data
    public static class Expression {

        private String type;

        /**
         * SpEL表达式
         */
        private String staticValue;
    }
}
