package com.dormitory.controller;

import com.dormitory.dto.LoginDTO;
import com.dormitory.dto.RegisterDTO;
import com.dormitory.entity.User;
import com.dormitory.service.UserService;
import com.dormitory.utils.Result;
import com.dormitory.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final TokenUtil tokenUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("收到注册请求: {}", registerDTO.getAccount());
        Map<String, Object> result = userService.register(registerDTO);
        return Result.success(result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("收到登录请求: {}", loginDTO.getAccount());
        Map<String, Object> result = userService.login(loginDTO);
        return Result.success(result);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        Long userId = tokenUtil.getUserIdFromToken(token);
        log.info("收到修改密码请求: userId={}", userId);

        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    /**
     * 绑定宿舍
     */
    @PostMapping("/bind-dormitory")
    public Result<Void> bindDormitory(
            @RequestHeader("Authorization") String token,
            @RequestParam String building,
            @RequestParam String room) {

        Long userId = tokenUtil.getUserIdFromToken(token);
        log.info("收到绑定宿舍请求: userId={}, building={}, room={}", userId, building, room);

        userService.bindDormitory(userId, building, room);
        return Result.success();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = tokenUtil.getUserIdFromToken(token);
        log.info("获取用户信息: userId={}", userId);

        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }
}