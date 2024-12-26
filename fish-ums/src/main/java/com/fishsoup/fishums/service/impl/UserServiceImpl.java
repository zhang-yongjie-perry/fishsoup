package com.fishsoup.fishums.service.impl;

import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.user.User;
import com.fishsoup.enums.AccountStatusEnum;
import com.fishsoup.fishums.feignService.UserFeignService;
import com.fishsoup.fishums.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.fishsoup.enums.ResponseCodeEnum.SUCCESS;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserFeignService userFeignService;

    @Override
    @GlobalTransactional(name = "ums-lock-account")
    public boolean lockAccount(User user) throws BusinessException {
        User toLcokUser = new User();
        toLcokUser.setId(user.getId());
        toLcokUser.setUsername(user.getUsername());
        toLcokUser.setAccountStatus(AccountStatusEnum.LOCKED);
        ResponseResult saveResult = userFeignService.saveUser(toLcokUser);
        if (Objects.isNull(saveResult) || !Objects.equals(saveResult.getCode(), SUCCESS.getCode())) {
            throw new BusinessException("冻结账号失败, 请稍后重试: " + saveResult.getMsg());
        }
        ResponseResult logoutResult = userFeignService.logout(toLcokUser);
        if (Objects.isNull(logoutResult) || !Objects.equals(logoutResult.getCode(), SUCCESS.getCode())) {
            throw new BusinessException("账号退出失败, 请稍后重试: " + logoutResult.getMsg());
        }
        return true;
    }

    @Override
    @GlobalTransactional(name = "ums-unlock-account")
    public boolean unlockAccount(List<User> userList) throws BusinessException {
        List<User> users = userList.stream().map(user -> {
            User toUnlcokUser = new User();
            toUnlcokUser.setId(user.getId());
            toUnlcokUser.setUsername(user.getUsername());
            toUnlcokUser.setAccountStatus(AccountStatusEnum.NORMAL);
            return toUnlcokUser;
        }).toList();

        ResponseResult responseResult = userFeignService.batchSaveUser(users);
        if (Objects.isNull(responseResult) || !Objects.equals(responseResult.getCode(), SUCCESS.getCode())) {
            throw new BusinessException("账号退出失败, 请稍后重试");
        }
        return true;
    }
}
