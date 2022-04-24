package com.chinamobile.zj.util;

import java.time.format.DateTimeFormatter;

/**
 * Copyright (c) 2019 by [中国移动]
 *
 * @ClassName: Constant
 * @Description: 常量类
 * @Author: zty
 * @CreateDate: 2019/12/16 18:33
 * @LastUpdateAutor: zty
 * @LastUpdateDate: 2019/12/16 18:33
 */
public class Constants {
    /**
    * 添加小区成功
    *
    * */
    public static String ADDRESIDENTIAL_SUCCESS = "1";
    public static String ADDRESIDENTIAL_FAIL = "0";
    /**
     * 添加信息的常量
     *
     * */
    public static String ADD_SUCCESS = "1";
    public static String ADD_FAIL = "0";
    /***
     * 删除信息常量
     *
     */
    public static String DELETE_SUCCESS = "1";
    public static String DELETE_FAIL = "0";
    /**
     *
     * 更新信息常量
     * */
    public static String UPLOAD_SUCCESS = "1";
    public static String UPLOAD_FAIL = "0";
    /**
     *  查询详情常量
     *
     * */
    public static String QUERYONE_SUCCESS = "1";
    public static String QUERYONE_FAIL = "0";

    /**
     * 时间格式：2021-01-01 15:01:01
     * 两位，24小时制
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 无符号日期格式：20210101
     * 两位
     */
    public static final DateTimeFormatter BASIC_DATE_WITHOUT_ZONE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * 用来追踪一次request请求的过程
     */
    public static final String REQUEST_LOG_ID_HEADER = "request-log-id";

    public static String COMMA = ",";

    /**
     * unix系统的文件分隔符
     */
    public static final String SEPARATOR_OF_UNIX = "/";

}
