package com.chinamobile.zj.comm;


import org.apache.commons.lang.StringUtils;

/**
 * Function: 参数错误异常类<br/>
 * date: 2019年12月16日 下午15:38:11 <br/>
 * 
 * @author zach
 * @since JDK 1.8
 */
public class ParamException extends GlobalException {
	private static final long serialVersionUID = 6021390821349937519L;

	public ParamException(String message) {
		super(message, ResponseEnum.PARAM_ERROR_CODE.getCode());
	}

	public static void isTrue(boolean expression, String message) {
		if (expression) {
			throw new ParamException(message);
		}
	}
	public static void isNull(Object object, String message) {
		if (object == null) {
			throw new ParamException(message);
		}
	}


	public static void isNull(Object obj) {
		isNull(obj, "参数不能为空");
	}

	public static void isBlank(String text, String message) {
		if (StringUtils.isBlank(text)) {
			throw new ParamException(message);
		}
	}

	public static void isBlank(String text) {
		isBlank(text, "参数不能为空");
	}
}
