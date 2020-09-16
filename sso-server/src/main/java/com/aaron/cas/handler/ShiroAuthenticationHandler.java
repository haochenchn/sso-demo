package com.aaron.cas.handler;

import com.aaron.cas.adaptors.generic.UsernamePasswordCaptchaCredential;
import com.aaron.cas.service.IUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aaron
 * @description 自定义验证器(shiro)
 * CAS服务器的org.apereo.cas.authentication.AuthenticationManager负责基于提供的凭证信息进行用户认证。
 * 与Spring Security很相似，实际的认证委托给了一个或多个实现了
 * org.apereo.cas.authentication.AuthenticationHandler接口的处理类。
 * 在cas的认证过程中逐个执行authenticationHandlers中配置的认证管理，直到有一个成功为止。
 * @date 2020/9/9
 */
public class ShiroAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroAuthenticationHandler.class);

    @Autowired
    private IUserService IUserService;

    public ShiroAuthenticationHandler(String name,ServicesManager servicesManager,PrincipalFactory principalFactory,Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        UsernamePasswordCaptchaCredential myCredential = (UsernamePasswordCaptchaCredential) credential;
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(myCredential.getUsername(), myCredential.getPassword());
            // 交给shiro验证
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);

            checkSubjectRolesAndPermissions(subject);
            // 要返回给cas客户端的参数放在这里
            Map<String,Object> returnParams = new HashMap<>();
            return createHandlerResult(credential, this.principalFactory.createPrincipal(myCredential.getUsername(), returnParams));
        } catch (final UnknownAccountException uae) {
            throw new AccountNotFoundException(uae.getMessage());
        } catch (final IncorrectCredentialsException ice) {
            throw new FailedLoginException(ice.getMessage());
        } catch (final LockedAccountException | ExcessiveAttemptsException lae) {
            throw new AccountLockedException(lae.getMessage());
        } catch (final ExpiredCredentialsException eae) {
            throw new CredentialExpiredException(eae.getMessage());
        } catch (final DisabledAccountException eae) {
            throw new AccountDisabledException(eae.getMessage());
        } catch (final AuthenticationException e) {
            throw new FailedLoginException(e.getMessage());
        }
    }


    /**
     * Check subject roles and permissions.
     * 这只是举个简单的例子 进行对比,可以自己写 自己对应的逻辑
     *
     * @param subject the current user
     * @throws FailedLoginException the failed login exception in case roles or permissions are absent
     */
    protected void checkSubjectRolesAndPermissions(final Subject subject) throws FailedLoginException {

        /*//查询用户id， 也可以在登录成功之后,将id 放到session中,从session中获取,这里直接查库
        UserDto user = IUserService.findByUserName(String.valueOf(currentUser.getPrincipal()));
        //获取所有的用户角色
        Set<String> allRoles = roleService.findAllRoles();
        //根据id获取用户的角色,这里一个用户只对应一个角色
        String userRole = roleService.findRolesByUserId(String.valueOf(user.get("uid")));
        //判断如果有角色,就登陆成功
        for (String role : allRoles){
            if (role.equals(userRole)) {
                return;
            }
        }
        //否则抛出异常,也可以自定义异常,返回不同的提示
        throw new FailedLoginException();*/
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof UsernamePasswordCaptchaCredential;
    }
}
