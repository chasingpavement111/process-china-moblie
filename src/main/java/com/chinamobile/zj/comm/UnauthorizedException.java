package com.chinamobile.zj.comm;


/**
 * 登录认证失败：无登录信息、无效的登录信息、登陆超时。。。
 */
public class UnauthorizedException extends GlobalException {

	public UnauthorizedException(String message) {
		super(message, ResponseEnum.UNAUTHORIZED.getCode());
	}

	public static void isTrue(boolean expression, String message) {
		if (expression) {
			throw new UnauthorizedException(message);
		}
	}

}
