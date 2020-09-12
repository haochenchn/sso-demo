package com.aaron.cas.adaptors.generic;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apereo.cas.authentication.RememberMeUsernamePasswordCredential;


/**
 * @author Aaron
 * @description 验证码 Credential
 * @date 2020/9/12
 */
public class UsernamePasswordCaptchaCredential extends RememberMeUsernamePasswordCredential {

    private String captcha;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.captcha)
                .toHashCode();
    }
}
