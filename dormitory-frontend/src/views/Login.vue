<template>
  <div class="login-container">
    <div class="login-card">
      <h2>🏠 宿舍报修管理系统</h2>
      
      <div class="tabs">
        <button :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">
          登录
        </button>
        <button :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">
          注册
        </button>
      </div>
      
      <!-- 登录表单 -->
      <div v-if="activeTab === 'login'">
        <input 
          v-model="loginForm.account" 
          type="text" 
          placeholder="账号（学号/工号）"
        />
        <input 
          v-model="loginForm.password" 
          type="password" 
          placeholder="密码"
        />
        <button @click="handleLogin">登录</button>
      </div>
      
      <!-- 注册表单 -->
      <div v-if="activeTab === 'register'">
        <input 
          v-model="registerForm.account" 
          type="text" 
          placeholder="账号（3125/3225开头）"
        />
        <select v-model="registerForm.role">
          <option value="student">学生</option>
          <option value="admin">管理员</option>
        </select>
        <input 
          v-model="registerForm.name" 
          type="text" 
          placeholder="姓名"
        />
        <input 
          v-model="registerForm.phone" 
          type="text" 
          placeholder="联系电话"
        />
        <input 
          v-model="registerForm.password" 
          type="password" 
          placeholder="密码"
        />
        <input 
          v-model="registerForm.confirmPassword" 
          type="password" 
          placeholder="确认密码"
        />
        <button @click="handleRegister">注册</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '@/api/user'

const router = useRouter()
const activeTab = ref('login')

const loginForm = ref({
  account: '',
  password: ''
})

const registerForm = ref({
  account: '',
  password: '',
  confirmPassword: '',
  role: 'student',
  name: '',
  phone: ''
})

const handleLogin = async () => {
  if (!loginForm.value.account || !loginForm.value.password) {
    ElMessage.error('请填写完整信息')
    return
  }
  
  try {
    const res = await login(loginForm.value)
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('role', res.data.role)
      localStorage.setItem('userId', res.data.userId)
      localStorage.setItem('account', res.data.account)
      
      ElMessage.success('登录成功')
      
      if (res.data.role === 'student') {
        router.push('/student')
      } else {
        router.push('/admin')
      }
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('登录失败，请确保后端已启动')
  }
}

const handleRegister = async () => {
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  try {
    const res = await register(registerForm.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      activeTab.value = 'login'
      registerForm.value = {
        account: '',
        password: '',
        confirmPassword: '',
        role: 'student',
        name: '',
        phone: ''
      }
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('注册失败')
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.tabs {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 2px solid #eee;
}

.tabs button {
  flex: 1;
  padding: 10px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 16px;
  color: #666;
}

.tabs button.active {
  color: #409eff;
  border-bottom: 2px solid #409eff;
  margin-bottom: -2px;
}

input, select {
  width: 100%;
  padding: 12px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

button {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background: #66b1ff;
}
</style>