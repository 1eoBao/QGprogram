package com.dormitory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.dto.LoginDTO;
import com.dormitory.dto.RegisterDTO;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.UserMapper;
import com.dormitory.utils.JWTUtil;
import com.dormitory.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用户服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JWTUtil jwtUtil;

    /**
     * 用户注册
     */
    public Map<String, Object> register(RegisterDTO registerDTO) {
        log.info("开始注册用户: {}", registerDTO.getAccount());

        // 1. 校验密码一致性
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 2. 校验账号格式
        String account = registerDTO.getAccount();
        String role = registerDTO.getRole();

        if ("student".equals(role)) {
            if (!Pattern.matches("^(3125|3225)\\d{6}$", account)) {
                throw new BusinessException("学号格式错误，应为3125或3225开头+6位数字");
            }
        } else if ("admin".equals(role)) {
            if (!Pattern.matches("^0025\\d{6}$", account)) {
                throw new BusinessException("工号格式错误，应为0025开头+6位数字");
            }
        } else {
            throw new BusinessException("角色只能为student或admin");
        }

        // 3. 检查账号是否已存在
        User existUser = userMapper.findByAccount(account);
        if (existUser != null) {
            throw new BusinessException("账号已存在");
        }

        // 4. 创建新用户
        User user = new User();
        user.setAccount(account);
        user.setPassword(registerDTO.getPassword());  // 直接存明文
        user.setRole(role);
        user.setName(registerDTO.getName());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);  // 默认正常

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("注册失败");
        }

        log.info("用户注册成功: {}", account);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("account", account);
        resultMap.put("role", role);
        return resultMap;
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getAccount());

        User user = userMapper.findByAccount(loginDTO.getAccount());

        // 临时跳过 BCrypt 验证，直接比较明文
        if (user == null || !loginDTO.getPassword().equals(user.getPassword())) {
            log.warn("登录失败: 账号或密码错误 - {}", loginDTO.getAccount());
            throw new BusinessException("账号或密码错误");
        }

        // 3. 检查账号状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 4. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getAccount(), user.getRole());

        log.info("用户登录成功: {}", loginDTO.getAccount());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userId", user.getId());
        resultMap.put("account", user.getAccount());
        resultMap.put("role", user.getRole());
        // 判断是否需要绑定宿舍（学生且未绑定宿舍）
        boolean needBindDorm = "student".equals(user.getRole()) &&
                (user.getBuilding() == null || user.getBuilding().isEmpty());
        resultMap.put("needBindDorm", needBindDorm);

        return resultMap;
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码: userId={}", userId);

        // 1. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 校验原密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 3. 校验新密码
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }

        // 4. 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("修改密码失败");
        }

        log.info("密码修改成功: {}", user.getAccount());
    }

    /**
     * 绑定宿舍
     */
    public void bindDormitory(Long userId, String building, String room) {
        log.info("绑定宿舍: userId={}, building={}, room={}", userId, building, room);

        // 1. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 检查角色（只有学生可以绑定宿舍）
        if (!"student".equals(user.getRole())) {
            throw new BusinessException("只有学生可以绑定宿舍");
        }

        // 3. 校验参数
        if (building == null || building.trim().isEmpty()) {
            throw new BusinessException("楼栋不能为空");
        }
        if (room == null || room.trim().isEmpty()) {
            throw new BusinessException("房间号不能为空");
        }

        // 4. 更新宿舍信息
        user.setBuilding(building.trim());
        user.setRoom(room.trim());
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("绑定宿舍失败");
        }

        log.info("宿舍绑定成功: {} - {}{}", user.getAccount(), building, room);
    }

    /**
     * 获取用户信息
     */
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 清除密码字段
        user.setPassword(null);
        return user;
    }

    /**
     * 根据账号查询用户
     */
    public User findByAccount(String account) {
        return userMapper.findByAccount(account);
    }
}