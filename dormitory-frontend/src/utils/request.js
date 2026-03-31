import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8080',  // 后端地址，没有 /api
    timeout: 10000
})

// 请求拦截器：自动添加 token
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器：统一处理错误
request.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    ElMessage.error('登录已过期，请重新登录')
                    localStorage.clear()
                    window.location.href = '/login'
                    break
                case 403:
                    ElMessage.error('没有权限')
                    break
                case 404:
                    ElMessage.error('请求的接口不存在')
                    break
                case 500:
                    ElMessage.error('服务器错误')
                    break
                default:
                    ElMessage.error(error.response.data?.message || '请求失败')
            }
        } else {
            ElMessage.error('网络连接失败，请确保后端已启动')
        }
        return Promise.reject(error)
    }
)

export default request