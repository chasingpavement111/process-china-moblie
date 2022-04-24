package com.chinamobile.zj.flowProcess.bo;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class ExecutionResult {

    private Boolean success;

    private String code;

    private String message;

    public ExecutionResult(RESULT_CODE resultCode) {
        this.success = "0".equals(resultCode.getCode());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public ExecutionResult(RESULT_CODE resultCode, String message) {
        this.success = "0".equals(resultCode.getCode());
        this.code = resultCode.getCode();
        if (StringUtils.isNotBlank(message)) {
            this.message = resultCode.getMessage() + ":" + message;
        } else {
            this.message = resultCode.getMessage();
        }
    }

    public enum RESULT_CODE {
        SUCCESS("0", "成功"),
//        REJECTED("1", "审核失败"),

//        EXCEPTION("2", "异常"),
        ;

        private String code;

        private String message;

        RESULT_CODE(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
