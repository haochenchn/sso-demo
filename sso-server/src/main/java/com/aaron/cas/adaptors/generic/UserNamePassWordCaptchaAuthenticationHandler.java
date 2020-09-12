package com.aaron.cas.adaptors.generic;

import com.aaron.cas.service.UserService;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

import javax.security.auth.login.AccountNotFoundException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @author Aaron
 * @description 自定义验证器
 * @date 2020/9/9
 */
public class UserNamePassWordCaptchaAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserNamePassWordCaptchaAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        UsernamePasswordCaptchaCredential myCredential = (UsernamePasswordCaptchaCredential) credential;
        // 这里可以添加验证码校验逻辑
        String requestCaptcha = myCredential.getCaptcha();

        String username = myCredential.getUsername();
        Map<String, Object> user = userService.findByUserName(username);
        //可以在这里直接对用户名校验,或者调用 CredentialsMatcher 校验
        /*if (user == null || !user.get("password").equals(myCredential.getPassword())) {
            throw new UnknownAccountException("用户名或密码错误！");
        }*/
        //这里将 密码对比 注销掉,否则 无法锁定  要将密码对比 交给 密码比较器 在这里可以添加自己的密码比较器等
        //if (!password.equals(user.getPassword())) {
        //    throw new IncorrectCredentialsException("用户名或密码错误！");
        //}
        if("admin".equals(myCredential.getUsername())) {
            //这里可以自定义属性数据
            user.put("captcha", requestCaptcha);
            return createHandlerResult(credential, this.principalFactory.createPrincipal(username, user));
        } else {
            throw new AccountNotFoundException("必须是admin用户才允许通过");
        }
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof UsernamePasswordCaptchaCredential;
    }


}
