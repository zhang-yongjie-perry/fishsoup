package com.fishsoup.fishums.security;

import com.fishsoup.fishums.domain.User;
import com.fishsoup.fishums.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class UserRealm extends AuthorizingRealm {

    private final UserService userService;

    public UserRealm(BCryptCredentialsMatcher credentialsMatcher, UserService userService) {
        super.setCredentialsMatcher(credentialsMatcher);
        this.userService = userService;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        User presentedUser;
        try {
            presentedUser = userService.loadUserByUsername(username);
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
