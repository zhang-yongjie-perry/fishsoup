package com.fishsoup.fishweb.config;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.fishweb.exception.CustomAccessDeniedHandler;
import com.fishsoup.fishweb.exception.CustomAuthenticationEntryPoint;
import com.fishsoup.fishweb.filter.JwtAuthenticationFilter;
import com.fishsoup.fishweb.http.ResponseResult;
import com.fishsoup.fishweb.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public static final String[] IGNORE_URLS = {"/user/hello", "/user/login", "/user/register", "/user/logout",
        "/image/download/**"};

    @Autowired
    public void config(AuthenticationManagerBuilder builder) {
        builder.eraseCredentials(true);
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 由于 AuthenticationManager#authenticate 认证成功后, 还有许多如下的功能调用, 因本项目使用jwt进行权鉴, 所以本项目登录时使用自定义认证
     * SessionAuthenticationStrategy is notified of a new login. See the SessionAuthenticationStrategy interface.
     * The Authentication is set on the SecurityContextHolder. Later, if you need to save the SecurityContext
     * so that it can be automatically set on future requests, SecurityContextRepository#saveContext must be explicitly invoked.
     * See the SecurityContextHolderFilter class.
     * RememberMeServices.loginSuccess is invoked. If remember me is not configured, this is a no-op. See the rememberme package.
     * ApplicationEventPublisher publishes an InteractiveAuthenticationSuccessEvent.
     * AuthenticationSuccessHandler is invoked. See the AuthenticationSuccessHandler interface.
     *
     * @param http 安全过滤器链配置对象
     * @param jwtAuthenticationFilter 自定义jwt认证过滤器
     * @return 默认安全过滤器链
     * @throws Exception 构建安全过滤器链失败时抛出的异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .logout(logout -> logout.logoutUrl("/user/form/logout"))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 自定义表单参数格式登录处理路径
            .formLogin(form -> form.loginProcessingUrl("/user/form/login")
                // 认证成功处理器
                .successHandler((request, response, authentication) -> {
                    Map<String, String> token = new HashMap<>();
                    token.put("token", JWTUtils.createJWT(JSON.toJSONString(authentication.getPrincipal())));
                    response.setStatus(HttpStatus.OK.value());
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(JSON.toJSONString(ResponseResult.success(token)));
                })
                // 认证失败处理器
                .failureHandler((request, response, authenticationException) -> {
                    response.setStatus(HttpStatus.OK.value());
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(JSON.toJSONString(ResponseResult.error(authenticationException.getMessage())));
                })
            )
            .exceptionHandling(handlingConfigurer -> handlingConfigurer.accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
            .authorizeHttpRequests(authorize -> authorize.requestMatchers(IGNORE_URLS).permitAll().anyRequest().authenticated());
        return http.build();
    }
}