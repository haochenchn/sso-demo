package com.aaron.cas.exception;

import javax.security.auth.login.AccountException;

/**
 * @author Aaron
 * @description 验证码错误异常
 * @date 2020/9/16
 */
public class CaptchaErrorException extends AccountException {

    public CaptchaErrorException() {
        super();
    }

    public CaptchaErrorException(String msg) {
        super(msg);
    }
}
