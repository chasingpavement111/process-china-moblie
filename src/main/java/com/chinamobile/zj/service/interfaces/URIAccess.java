package com.chinamobile.zj.service.interfaces;

import com.chinamobile.zj.requestEntity.base.MapOfFieldChineseName2Value;

import java.lang.annotation.*;

/**
 * 接口访问的操作与控制
 * <p>
 * Examples:
 * <pre>
 * &#64;URIAccess(menuName = "进驻拍照-更新单个工单的详情") // 请求入参的类实现了 MapOfFieldChineseName2Value.class
 * </pre>
 * <pre>
 * &#64;URIAccess(menuName = "进驻拍照-获取单个工单的详情", args = {&#64;URIAccess.ArgsMapping(fieldNameEn = "id", fieldNameCh = "记录标识")}) // 请求入参id所属类型，不是 MapOfFieldChineseName2Value.class 实现类
 * </pre>
 * <pre>
 * &#64;URIAccess(menuName = "进驻拍照-获取单个工单的详情", args = {
 *   &#64;URIAccess.ArgsMapping(fieldNameEn = "id", fieldNameCh = "记录标识"),
 *   &#64;URIAccess.ArgsMapping(fieldNameEn = "name", fieldNameCh = "名称"),
 * }) // 有多个请求入参，args中是未实现 MapOfFieldChineseName2Value.class 的参数
 * </pre>
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented //生成文档
public @interface URIAccess {

    /**
     * 菜单名称
     *
     * @return
     */
    String menuName(); // 不设置default属性，要求必填

    /**
     * 参数名的中英文对应关系<br/>
     * 用于 配合 CLY_T_ALL_DOLOG.cont 要求字段名称为中文<br/>
     * 非必填。<br/>
     * <br/>
     * <p>
     * 使用场景：<br/>
     * 1、若入参的类型实现了 {@link MapOfFieldChineseName2Value} 。该入参可以忽略本属性；<br/>
     * 2、否则必须！使用本属性声明。一般是非自定义类型的入参，如基本类型、MultipartFile <br/>
     *
     * @return
     */
    ArgsMapping[] args() default {};

    @interface ArgsMapping {
        String fieldNameEn();

        String fieldNameCh();
    }
}
