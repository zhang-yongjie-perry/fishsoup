package com.fishsoup.fishchat.util;

import com.fishsoup.fishchat.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

import static com.fishsoup.fishchat.common.Constants.INTERFACE;


public class SecurityUtils {

    public static User getLoginUser() {
        return Objects.isNull(SecurityContextHolder.getContext()) ? null
            : Objects.isNull(SecurityContextHolder.getContext().getAuthentication()) ? null
            : SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User
            ? (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;
    }

    public static String getLoginName() {
        User loginUser = getLoginUser();
        return Objects.isNull(loginUser) ? INTERFACE : loginUser.getUsername();
    }

    public static String getUserId() {
        User loginUser = getLoginUser();
        return Objects.isNull(loginUser) ? INTERFACE : loginUser.getId();
    }
}
