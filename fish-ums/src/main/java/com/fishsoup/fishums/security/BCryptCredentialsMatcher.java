package com.fishsoup.fishums.security;

import com.fishsoup.fishums.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptCredentialsMatcher implements CredentialsMatcher {

    private final BCryptPasswordEncoder encoder;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        boolean success = encoder.matches(String.valueOf(upToken.getPassword()), info.getCredentials().toString());
        if (!success) {
            throw new AuthenticationException("账号或密码不正确");
        }
        return true;
    }
}
