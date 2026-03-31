<template>
  <div class="container">
    <div class="header">
      <h2>👨‍🎓 学生端 - 欢迎 {{ account }}</h2>
      <button @click="logout" class="logout-btn">退出登录</button>
    </div>
    
    <!-- 绑定宿舍 -->
    <div v-if="!building" class="card">
      <h3>📌 绑定宿舍</h3>
      <input v-model="bindForm.building" placeholder="楼栋 (如: A栋)" />
      <input v-model="bindForm.room" placeholder="房间号 (如: 101)" />
      <button @click="handleBindDorm">绑定</button>
    </div>
    
    <!-- 创建报修单 -->
    <div class="card">
      <h3>📝 创建报修单</h3>
      <input v-model="repairForm.deviceType" placeholder="设备类型 (如: 水龙头、电灯、空调)" />
      <textarea v-model="repairForm.description" placeholder="问题描述" rows="3"></textarea>
      <button @click="handleCreateRepair">提交报修</button>
    </div>
    
    <!-- 我的报修记录 -->
    <div class="card">
      <h3>📋 我的报修记录</h3>
      <div v-if="myOrders.length === 0" class="empty">暂无报修记录</div>
      <div v-for="order in myOrders" :key="order.id" class="order-item">
        <div><strong>单号：</strong>{{ order.orderNo }}</div>
        <div><strong>设备：</strong>{{ order.deviceType }}</div>
        <div><strong>描述：</strong>{{ order.description }}</div>
        <div><strong>状态：</strong>
          <span :class="'status-' + order.status">{{ getStatusText(order.status) }}</span>
        </div>
        <div><strong>时间：</strong>{{ order.createTime }}</div>
        <button v-if="order.status === 'pending'" @click="handleCancel(order.id)" class="cancel-btn">取消报修</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { bindDormitory } from '@/api/user'
import { createRepair, getMyOrders, cancelOrder } from '@/api/repair'

const router = useRouter()
const account = localStorage.getItem('account') || ''
const building = ref(localStorage.getItem('building') || '')

const bindForm = ref({ building: '', room: '' })
const repairForm = ref({ deviceType: '', description: '' })
const myOrders = ref([])

const getStatusText = (status) => {
  const map = { 
    'pending': '待处理', 
    'processing': '处理中', 
    'completed': '已完成', 
    'cancelled': '已取消' 
  }
  return map[status] || status
}

const loadMyOrders = async () => {
  try {
    const res = await getMyOrders()
    if (res.code === 200) {
      myOrders.value = res.data
    }
  } catch (error) {
    console.error('加载失败', error)
  }
}

const handleBindDorm = async () => {
  if (!bindForm.value.building || !bindForm.value.room) {
    ElMessage.error('请填写楼栋和房间号')
    return
  }
  
  try {
    const res = await bindDormitory(bindForm.value.building, bindForm.value.room)
    if (res.code === 200) {
      building.value = bindForm.value.building
      localStorage.setItem('building', bindForm.value.building)
      ElMessage.success('绑定成功')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('绑定失败')
  }
}

const handleCreateRepair = async () => {
  if (!repairForm.value.deviceType || !repairForm.value.description) {
    ElMessage.error('请填写完整信息')
    return
  }
  
  try {
    const res = await createRepair(repairForm.value)
    if (res.code === 200) {
      ElMessage.success('报修单创建成功')
      repairForm.value = { deviceType: '', description: '' }
      loadMyOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleCancel = async (orderId) => {
  try {
    const res = await cancelOrder(orderId)
    if (res.code === 200) {
      ElMessage.success('已取消')
      loadMyOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('取消失败')
  }
}

const logout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(() => {
  loadMyOrders()
})
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #eee;
}

.logout-btn {
  width: auto;
  padding: 8px 20px;
  background: #909399;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.card h3 {
  margin-bottom: 15px;
  color: #333;
}

input, textarea {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
  font-family: inherit;
}

.order-item {
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}

.order-item:last-child {
  border-bottom: none;
}

.order-item div {
  margin-bottom: 5px;
  font-size: 14px;
}

.status-pending { color: #e6a23c; }
.status-processing { color: #409eff; }
.status-completed { color: #67c23a; }
.status-cancelled { color: #f56c6c; }

.cancel-btn {
  width: auto;
  padding: 5px 15px;
  background: #f56c6c;
  margin-top: 10px;
}

.empty {
  text-align: center;
  padding: 30px;
  color: #999;
}
</style>