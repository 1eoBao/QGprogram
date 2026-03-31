package com.dormitory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 报修单实体类
 * 对应数据库表：repair_order
 */
@Data
@TableName("repair_order")
public class RepairOrder {

    /**
     * 报修单ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报修单号（唯一）
     */
    private String orderNo;

    /**
     * 报修学生ID
     */
    private Long studentId;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 状态：pending-待处理，processing-处理中，completed-已完成，cancelled-已取消
     */
    private String status;

    /**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}