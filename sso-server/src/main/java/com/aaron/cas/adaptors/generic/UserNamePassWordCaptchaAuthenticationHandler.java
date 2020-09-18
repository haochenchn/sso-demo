package com.aaron.cas.adaptors.generic;

import com.aaron.cas.exception.CaptchaErrorException;
import com.aaron.cas.service.IUserService;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountNotFoundException;
import java.security.GeneralSecurityException;

/**
 * @author Aaron
 * @description 自定义验证器
 * @date 2020/9/9
 */
public class UserNamePassWordCaptchaAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    @Autowired
    private IUserService userService;

    public UserNamePassWordCaptchaAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        UsernamePasswordCaptchaCredential myCredential = (UsernamePasswordCaptchaCredential) credential;
        String username = myCredential.getUsername();
        // TODO 这里可以添加验证码校验逻辑
        String requestCaptcha = myCredential.getCaptcha();
        if(!"123".equals(requestCaptcha)) {
            throw new CaptchaErrorException("验证码校验失败");
        }
        // 用户名密码校验
        // UserDto user = userService.findByUserName(username);
        //可以在这里直接对用户名密码校验,或者调用 CredentialsMatcher 校验
        if (!"admin".equals(username)) {
            throw new AccountNotFoundException("用户名或密码错误！");
        }
        return createHandlerResult(credential, this.principalFactory.createPrincipal(username));

    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof UsernamePasswordCaptchaCredential;
    }

}
