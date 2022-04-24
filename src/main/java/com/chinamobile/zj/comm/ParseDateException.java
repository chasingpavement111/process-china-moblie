package com.chinamobile.zj.comm;


public class ParseDateException extends GlobalException {

    public ParseDateException(String msg, Throwable cause) {
        super(msg, cause);
        setCode(ResponseEnum.PARAM_ERROR_CODE.getCode());
        setMsg(msg);
    }

}
