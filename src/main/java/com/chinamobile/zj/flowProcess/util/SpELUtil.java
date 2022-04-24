package com.chinamobile.zj.flowProcess.util;

import com.alibaba.fastjson.TypeReference;
import com.chinamobile.zj.util.JsonConvertUtil;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Collections;
import java.util.Map;

/**
 * SpEl表达式计算
 */
public class SpELUtil {

    public static Boolean getBooleanValue(String seEL, Object rootObject) {
        StandardEvaluationContext context = new StandardEvaluationContext(rootObject);
        ExpressionParser parser = new SpelExpressionParser();
        Expression ex1 = parser.parseExpression(seEL);
        return ex1.getValue(context, Boolean.class);
    }

    public static Boolean getBooleanValue(String seEL, Map<String, Object> rootObject) {
        StandardEvaluationContext context = new StandardEvaluationContext(rootObject);
        context.setPropertyAccessors(Collections.singletonList(new MapAccessor())); // 使得map对象的获取写法可以按照 . 的形式获取: 字段名.字段对象的下一级字段名； 默认的写法很复杂: map对象名['字段名']['字段对象的下一级字段名']
        ExpressionParser parser = new SpelExpressionParser();
        Expression ex1 = parser.parseExpression(seEL);
        return ex1.getValue(context, Boolean.class);
    }

    public static void main(String[] args) {
        Map<String, Object> outputVariablesMap = JsonConvertUtil.parseToParameterizedType("{\"preCheckApplicationPassedByWhiteCollar\":true, \"preCheckApplicationPassedByBlueCollar\":true}", new TypeReference<Map<String, Object>>() {
        });
        System.out.println(getBooleanValue("preCheckApplicationPassedByWhiteCollar==true || preCheckApplicationPassedByBlueCollar==true", outputVariablesMap)); // 必须加前缀 #
        System.out.println(getBooleanValue("preCheckApplicationPassedByWhiteCollar==true", outputVariablesMap)); // 必须加前缀 #
        outputVariablesMap.put("preCheckApplicationPassedByWhiteCollar", false);
        System.out.println(getBooleanValue("preCheckApplicationPassedByWhiteCollar==true", outputVariablesMap)); // 必须加前缀 #
        outputVariablesMap.put("preCheckApplicationPassedByWhiteCollar", null);
        System.out.println(getBooleanValue("preCheckApplicationPassedByWhiteCollar==true", outputVariablesMap)); // 必须加前缀 #

//        ProcessPropertiesBO request = new ProcessPropertiesBO();
//        request.setName("aaa");
//        request.setProcessId("bbb");
//        System.out.println(getBooleanValue(null, request));
//        System.out.println(getBooleanValue("name=='aaa' || processId=='bbb'", request));
//        System.out.println(getBooleanValue("#name=='aaa' || #processId=='bbb'", request));
//        System.out.println(getBooleanValue("#root.name=='aaa' || #root.processId=='bbb'", request));
    }

}
