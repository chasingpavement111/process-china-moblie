package com.chinamobile.zj.comm;

public class InternalException extends GlobalException {

    public InternalException(Throwable cause) {
        super(ResponseEnum.SERVER_ERROR_CODE.getMsg(), ResponseEnum.SERVER_ERROR_CODE.getCode(), cause);
    }

    public InternalException(String message, Throwable cause) {
        super(message, ResponseEnum.SERVER_ERROR_CODE.getCode(), cause);
    }

    public InternalException(String message) {
        super(message, ResponseEnum.SERVER_ERROR_CODE.getCode());
    }

    public static void isTrue(boolean expression, String message) {
        if (expression) {
            throw new InternalException(message);
        }
    }

}
