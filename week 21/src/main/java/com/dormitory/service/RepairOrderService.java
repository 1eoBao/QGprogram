package com.dormitory.service;

import cn.hutool.core.util.IdUtil;
import com.dormitory.dto.RepairOrderDTO;
import com.dormitory.entity.RepairOrder;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.RepairOrderMapper;
import com.dormitory.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

/**
 * 报修单服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepairOrderService {

    private final RepairOrderMapper repairOrderMapper;
    private final UserMapper userMapper;

    // 有效状态列表
    private static final List<String> VALID_STATUSES = Arrays.asList(
            "pending", "processing", "completed", "cancelled"
    );

    /**
     * 创建报修单
     */
    @Transactional
    public RepairOrder createOrder(Long studentId, RepairOrderDTO dto) {
        log.info("创建报修单: studentId={}, deviceType={}", studentId, dto.getDeviceType());

        // 1. 检查学生是否存在
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        // 2. 检查角色
        if (!"student".equals(student.getRole())) {
            throw new BusinessException("只有学生可以创建报修单");
        }

        // 3. 检查是否已绑定宿舍
        if (student.getBuilding() == null || student.getBuilding().isEmpty()) {
            throw new BusinessException("请先绑定宿舍后再创建报修单");
        }

        // 4. 创建报修单
        RepairOrder order = new RepairOrder();
        order.setOrderNo(IdUtil.fastSimpleUUID());  // 生成唯一单号
        order.setStudentId(studentId);
        order.setDeviceType(dto.getDeviceType());
        order.setDescription(dto.getDescription());
        order.setImageUrl(dto.getImageUrl());
        order.setStatus("pending");  // 待处理

        int result = repairOrderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException("创建报修单失败");
        }

        log.info("报修单创建成功: {}", order.getOrderNo());
        return order;
    }

    /**
     * 获取学生的报修记录
     */
    public List<RepairOrder> getStudentOrders(Long studentId) {
        log.info("查询学生报修记录: studentId={}", studentId);

        // 检查学生是否存在
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        return repairOrderMapper.findByStudentId(studentId);
    }

    /**
     * 取消报修单
     */
    @Transactional
    public void cancelOrder(Long orderId, Long studentId) {
        log.info("取消报修单: orderId={}, studentId={}", orderId, studentId);

        // 1. 查询报修单
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("报修单不存在");
        }

        // 2. 验证权限
        if (!order.getStudentId().equals(studentId)) {
            throw new BusinessException("无权取消此报修单");
        }

        // 3. 验证状态（只有待处理可以取消）
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException("只有待处理的报修单可以取消");
        }

        // 4. 更新状态
        order.setStatus("cancelled");
        int result = repairOrderMapper.updateById(order);
        if (result <= 0) {
            throw new BusinessException("取消报修单失败");
        }

        log.info("报修单取消成功: {}", order.getOrderNo());
    }

    /**
     * 获取所有报修单（管理员）
     */
    public List<RepairOrder> getAllOrders(String status) {
        log.info("管理员查询报修单, status={}", status);

        if (status != null && !status.isEmpty()) {
            // 验证状态值
            if (!VALID_STATUSES.contains(status)) {
                throw new BusinessException("无效的状态值");
            }
            return repairOrderMapper.findByStatus(status);
        }

        return repairOrderMapper.selectList(null);
    }

    /**
     * 获取报修单详情
     */
    public RepairOrder getOrderDetail(Long orderId) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("报修单不存在");
        }
        return order;
    }

    /**
     * 更新报修单状态（管理员）
     */
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        log.info("管理员更新报修单状态: orderId={}, status={}", orderId, status);

        // 1. 查询报修单
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("报修单不存在");
        }

        // 2. 验证状态值
        if (!VALID_STATUSES.contains(status)) {
            throw new BusinessException("无效的状态值");
        }

        // 3. 不能从已取消状态修改
        if ("cancelled".equals(order.getStatus())) {
            throw new BusinessException("已取消的报修单不能修改状态");
        }

        // 4. 更新状态
        int result = repairOrderMapper.updateStatus(orderId, status);
        if (result <= 0) {
            throw new BusinessException("更新状态失败");
        }

        log.info("报修单状态更新成功: {} -> {}", order.getOrderNo(), status);
    }

    /**
     * 删除报修单（管理员）
     */
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("管理员删除报修单: orderId={}", orderId);

        // 1. 查询报修单
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("报修单不存在");
        }

        // 2. 执行删除
        int result = repairOrderMapper.deleteById(orderId);
        if (result <= 0) {
            throw new BusinessException("删除报修单失败");
        }

        log.info("报修单删除成功: {}", order.getOrderNo());
    }
}