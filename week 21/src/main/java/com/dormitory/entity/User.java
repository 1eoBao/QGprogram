package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {

    /**
     * 用户ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账号（学号/工号）
     */
    private String account;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 角色：student-学生, admin-管理员
     */
    private String role;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 宿舍楼栋（学生专用）
     */
    private String building;

    /**
     * 房间号（学生专用）
     */
    private String room;

    /**
     * 状态：1-正常, 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}