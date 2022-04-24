package com.chinamobile.zj.flowProcess.service;

import java.lang.annotation.*;

/**
 * 被注释的变量，指示为外部输入参数
 */
@Target(ElementType.FIELD) //注解放置的目标位置,FIELD是可注解在类的成员变量级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented //生成文档
public @interface InputParam {
}
