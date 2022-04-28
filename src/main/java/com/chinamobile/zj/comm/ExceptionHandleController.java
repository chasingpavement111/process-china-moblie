package com.chinamobile.zj.comm;

import com.chinamobile.zj.util.Constants;
import com.chinamobile.zj.util.DateUtil;
import com.chinamobile.zj.util.IpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestControllerAdvice
public class ExceptionHandleController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandleController.class);

    @ExceptionHandler(value = Exception.class)
    public ResponseData handle(Exception ex, HttpServletRequest request) {
        String uuid = String.valueOf(request.getAttribute(Constants.REQUEST_LOG_ID_HEADER));// 与CookieHandle入口uuid保持一致
        String serverIp = IpUtils.getLocalHostIp();
        String clientIp = IpUtils.getIpAddr(request);
        LOGGER.error(String.format("[%s][%s-httpRequestResultFailed] server[%s] gateway record from client[%s], url=%s, result failed! handle Exception, error: %s",
                        DateUtil.format(new Date(), DateUtil.DATE_NANO_TIME_REGEX), uuid, serverIp, clientIp, request.getRequestURI(), ex.getMessage())
                , ex);

        if (StringUtils.isBlank(ex.getMessage())) {
            return ResponseData.fail("No message available");
        }
        return ResponseData.fail(ex.getMessage());
    }

    @ExceptionHandler(value = GlobalException.class)
    public ResponseData handle(GlobalException ex, HttpServletRequest request) {
        String uuid = String.valueOf(request.getAttribute(Constants.REQUEST_LOG_ID_HEADER));// 与CookieHandle入口uuid保持一致
        String serverIp = IpUtils.getLocalHostIp();
        String clientIp = IpUtils.getIpAddr(request);
        LOGGER.error(String.format("[%s][%s-httpRequestResultFailed] server[%s] gateway record from client[%s], url=%s, result failed! handle GlobalException, error: %s",
                        DateUtil.format(new Date(), DateUtil.DATE_NANO_TIME_REGEX), uuid, serverIp, clientIp, request.getRequestURI(), ex.getMsg())
                , ex);
        if (StringUtils.isBlank(ex.getMsg())) {
            return ResponseData.fail("No message available");
        }
        return ResponseData.fail(ex.getMsg(), ex.getCode());
    }

}
