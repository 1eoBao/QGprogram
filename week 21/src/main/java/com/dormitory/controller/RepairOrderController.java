package com.dormitory.controller;

import com.dormitory.dto.RepairOrderDTO;
import com.dormitory.entity.RepairOrder;
import com.dormitory.service.RepairOrderService;
import com.dormitory.utils.Result;
import com.dormitory.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

/**
 * 报修单控制器
 */
@Slf4j
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RepairOrderController {

    private final RepairOrderService repairOrderService;
    private final TokenUtil tokenUtil;

    /**
     * 创建报修单（学生）
     */
    @PostMapping("/create")
    public Result<RepairOrder> createOrder(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody RepairOrderDTO dto) {

        Long userId = tokenUtil.getUserIdFromToken(token);
        String role = tokenUtil.getRoleFromToken(token);

        // 权限校验
        if (!"student".equals(role)) {
            return Result.error("只有学生可以创建报修单");
        }

        log.info("收到创建报修单请求: userId={}, deviceType={}", userId, dto.getDeviceType());

        RepairOrder order = repairOrderService.createOrder(userId, dto);
        return Result.success(order);
    }

    /**
     * 获取我的报修记录（学生）
     */
    @GetMapping("/my-orders")
    public Result<List<RepairOrder>> getMyOrders(@RequestHeader("Authorization") String token) {
        Long userId = tokenUtil.getUserIdFromToken(token);
        log.info("获取我的报修记录: userId={}", userId);

        List<RepairOrder> orders = repairOrderService.getStudentOrders(userId);
        return Result.success(orders);
    }

    /**
     * 取消报修单（学生）
     */
    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {

        Long userId = tokenUtil.getUserIdFromToken(token);
        log.info("取消报修单: userId={}, orderId={}", userId, orderId);

        repairOrderService.cancelOrder(orderId, userId);
        return Result.success();
    }

    /**
     * 获取所有报修单（管理员）
     */
    @GetMapping("/admin/orders")
    public Result<List<RepairOrder>> getAllOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status) {

        String role = tokenUtil.getRoleFromToken(token);

        // 权限校验
        if (!"admin".equals(role)) {
            return Result.error("无权限访问");
        }

        log.info("管理员查询报修单, status={}", status);

        List<RepairOrder> orders = repairOrderService.getAllOrders(status);
        return Result.success(orders);
    }

    /**
     * 获取报修单详情（管理员）
     */
    @GetMapping("/admin/order/{orderId}")
    public Result<RepairOrder> getOrderDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {

        String role = tokenUtil.getRoleFromToken(token);

        // 权限校验
        if (!"admin".equals(role)) {
            return Result.error("无权限访问");
        }

        log.info("获取报修单详情: orderId={}", orderId);

        RepairOrder order = repairOrderService.getOrderDetail(orderId);
        return Result.success(order);
    }

    /**
     * 更新报修单状态（管理员）
     */
    @PutMapping("/admin/order/{orderId}/status")
    public Result<Void> updateOrderStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId,
            @RequestParam String status) {

        String role = tokenUtil.getRoleFromToken(token);

        // 权限校验
        if (!"admin".equals(role)) {
            return Result.error("无权限访问");
        }

        log.info("管理员更新报修单状态: orderId={}, status={}", orderId, status);

        repairOrderService.updateOrderStatus(orderId, status);
        return Result.success();
    }

    /**
     * 删除报修单（管理员）
     */
    @DeleteMapping("/admin/order/{orderId}")
    public Result<Void> deleteOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {

        String role = tokenUtil.getRoleFromToken(token);

        // 权限校验
        if (!"admin".equals(role)) {
            return Result.error("无权限访问");
        }

        log.info("管理员删除报修单: orderId={}", orderId);

        repairOrderService.deleteOrder(orderId);
        return Result.success();
    }
}