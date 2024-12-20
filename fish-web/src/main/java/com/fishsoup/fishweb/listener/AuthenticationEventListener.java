package com.fishsoup.fishweb.listener;

import com.fishsoup.fishweb.domain.User;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventListener {

    @EventListener({InteractiveAuthenticationSuccessEvent.class})
    public void listenerAuthenticationEvent(InteractiveAuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        User user = (User) authentication.getPrincipal();
        System.out.printf("%s登录成功", user.getUsername());
    }
}
