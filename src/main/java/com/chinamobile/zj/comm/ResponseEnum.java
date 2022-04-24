package com.chinamobile.zj.comm;

/**
 * Function: 错误code枚举类 <br/>
 * date: 2019年12月16日 下午15:17:21 <br/>
 * 
 * @author 岳渊琛
 * @since JDK 1.8
 */
public enum ResponseEnum {
	/** 正确 **/
	SUCCESS_CODE(200,"正确"),
	/** 默认业务错误 **/
	DEFAULT_ERROR_CODE(-1,"业务错误"),
	/** 参数错误 **/
	PARAM_ERROR_CODE(400,"参数错误"),
	/** 登陆认证失败 **/
	UNAUTHORIZED(401,"登陆认证失败"),
	/** 权限不足*/
	FORBIDDEN(403,"权限不足"),
	/** 禁止访问 **/
	NO_AUTH_CODE(402,"禁止访问"),
	/** 资源没找到 **/
	NOT_FOUND(404,"资源没找到"),
	/** 服务器错误 **/
	SERVER_ERROR_CODE(500,"服务器错误");

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

	private ResponseEnum(int code) {
		this.code = code;
	}
	private ResponseEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
