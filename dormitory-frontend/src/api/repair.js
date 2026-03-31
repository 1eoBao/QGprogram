import request from '@/utils/request'

// 创建报修单
export function createRepair(data) {
    return request({
        url: '/repair/create',
        method: 'post',
        data
    })
}

// 获取我的报修单
export function getMyOrders() {
    return request({
        url: '/repair/my-orders',
        method: 'get'
    })
}

// 取消报修单
export function cancelOrder(orderId) {
    return request({
        url: `/repair/cancel/${orderId}`,
        method: 'put'
    })
}

// 获取所有报修单（管理员）
export function getAllOrders(status) {
    return request({
        url: '/repair/admin/orders',
        method: 'get',
        params: { status }
    })
}

// 更新报修单状态（管理员）
export function updateOrderStatus(orderId, status) {
    return request({
        url: `/repair/admin/order/${orderId}/status`,
        method: 'put',
        params: { status }
    })
}

// 删除报修单（管理员）
export function deleteOrder(orderId) {
    return request({
        url: `/repair/admin/order/${orderId}`,
        method: 'delete'
    })
}