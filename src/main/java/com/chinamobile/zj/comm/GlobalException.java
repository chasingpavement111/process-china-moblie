package com.chinamobile.zj.comm;


/**
 * Function: 全局异常类. <br/>
 * date: 2019年12月16日 下午16:00 <br/>
 *
 * @author zach
 * @since JDK 1.8
 */
public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = -5701182284190108797L;

    private int code;
    private String msg;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public GlobalException(GlobalException e) {
        super(e.getMessage());
        this.code = e.getCode();
    }

    public GlobalException(String message) {
        super("code:" + ResponseEnum.DEFAULT_ERROR_CODE.getCode() + ",msg:" + message);
        this.code = ResponseEnum.DEFAULT_ERROR_CODE.getCode();
        this.msg = message;
    }

    public GlobalException(String message, int code) {
        super("code:" + code + ",msg:" + message);
        this.msg = message;
        this.code = code;
    }

    public GlobalException(String message, int code, Throwable cause) { // todo zj git 需要新增该行
        super(message, cause);
        this.code = code;
        this.msg = message;
    }

    public GlobalException(String msg, Throwable cause) {
        super(msg, cause);
        this.code = ResponseEnum.DEFAULT_ERROR_CODE.getCode();
        this.msg = msg;
    }

}
