import request from '@/utils/request'

// 登录
export function login(data) {
    return request({
        url: '/user/login',
        method: 'post',
        data
    })
}

// 注册
export function register(data) {
    return request({
        url: '/user/register',
        method: 'post',
        data
    })
}

// 绑定宿舍
export function bindDormitory(building, room) {
    return request({
        url: '/user/bind-dormitory',
        method: 'post',
        params: { building, room }
    })
}

// 获取用户信息
export function getUserInfo() {
    return request({
        url: '/user/info',
        method: 'get'
    })
}