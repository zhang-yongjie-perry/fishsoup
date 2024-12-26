package com.fishsoup.fishums.security;

import com.fishsoup.entity.user.User;
import com.fishsoup.fishums.feignService.UserFeignService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class UserRealm extends AuthorizingRealm {

    private final UserFeignService userFeignService;

    public UserRealm(BCryptCredentialsMatcher credentialsMatcher, UserFeignService userFeignService) {
        super.setCredentialsMatcher(credentialsMatcher);
        this.userFeignService = userFeignService;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        User presentedUser;
        try {
            presentedUser = userFeignService.findUserByUsername(username);
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage(), e);
        }
        return new SimpleAuthenticationInfo(presentedUser, presentedUser.getPassword(), getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }
}
