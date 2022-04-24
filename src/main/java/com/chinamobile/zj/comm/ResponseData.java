package com.chinamobile.zj.comm;


import java.io.Serializable;


/**
 * Function: 接口统一返回对象 <br/>
 *  date: 2019年12月16日 下午15:17:21 <br/>
 *
 * @author 岳渊琛
 * @since JDK 1.8
 */
public class ResponseData<T> implements Serializable {
    /**
     * code编码
     */
    private int code = 200;

    private int totality;
    /**
     * 返回描述
     */
    private String message = "";
    /**
     * 数据
     */
    private T data;

    public ResponseData(int code) {
        super();
        this.code = code;
    }

    public static ResponseData ok() {
        return new ResponseData();
    }

    public static <T> ResponseData<T> ok(T data) {
        return new ResponseData<T>(data);
    }

    public static <T> ResponseData<T> fail() {
        return new ResponseData<T>(ResponseEnum.SERVER_ERROR_CODE.getCode());
    }

    public static <T> ResponseData<T> fail(String message) {
        return new ResponseData<T>(message, ResponseEnum.PARAM_ERROR_CODE.getCode());
    }

    public static <T> ResponseData<T> fail(String message, int code) {
        return new ResponseData<T>(message, code);
    }

    public static <T> ResponseData<T> failByParam(String message) {
        return new ResponseData<T>(message, ResponseEnum.PARAM_ERROR_CODE.getCode());
    }

    public ResponseData(T data) {
        super();
        this.data = data;
    }

    public ResponseData(String message) {
        super();
        this.message = message;
    }

    public ResponseData(String message, int code) {
        super();
        this.message = message;
        this.code = code;
    }

    public ResponseData() {
        super();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotality() {
        return totality;
    }

    public void setTotality(int totality) {
        this.totality = totality;
    }
}
