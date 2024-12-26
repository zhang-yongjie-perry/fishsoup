package com.fishsoup.fishums.config;

import com.fishsoup.fishums.feignService.UserFeignService;
import com.fishsoup.fishums.security.BCryptCredentialsMatcher;
import com.fishsoup.fishums.security.UserRealm;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ShiroConfig {

    private final UserFeignService userFeignService;
    private final BCryptCredentialsMatcher credentialsMatcher;

    @Bean
    public Realm realm() {
        return new UserRealm(credentialsMatcher, userFeignService);
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/user/test/**", "anon");
        chainDefinition.addPathDefinition("/jQuery/**", "anon");
        chainDefinition.addPathDefinition("/css/**", "anon");
        chainDefinition.addPathDefinition("/js/**", "anon");
        chainDefinition.addPathDefinition("/image/**", "anon");
        chainDefinition.addPathDefinition("/layui/**", "anon");
        chainDefinition.addPathDefinition("/user/login", "anon");
        chainDefinition.addPathDefinition("/user/logout", "anon");
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }
}
