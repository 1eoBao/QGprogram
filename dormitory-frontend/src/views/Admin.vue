<template>
  <div class="container">
    <div class="header">
      <h2>👨‍💼 管理员端 - 欢迎 {{ account }}</h2>
      <button @click="logout" class="logout-btn">退出登录</button>
    </div>
    
    <div class="card">
      <h3>🔍 筛选</h3>
      <div class="filter-row">
        <select v-model="filterStatus" @change="loadAllOrders">
          <option value="">全部</option>
          <option value="pending">待处理</option>
          <option value="processing">处理中</option>
          <option value="completed">已完成</option>
        </select>
        <button @click="loadAllOrders" class="refresh-btn">刷新</button>
      </div>
    </div>
    
    <div class="card">
      <h3>📋 报修单列表</h3>
      <div v-if="allOrders.length === 0" class="empty">暂无报修单</div>
      <div v-for="order in allOrders" :key="order.id" class="order-item">
        <div><strong>单号：</strong>{{ order.orderNo }}</div>
        <div><strong>学生ID：</strong>{{ order.studentId }}</div>
        <div><strong>设备：</strong>{{ order.deviceType }}</div>
        <div><strong>描述：</strong>{{ order.description }}</div>
        <div><strong>状态：</strong>
          <span :class="'status-' + order.status">{{ getStatusText(order.status) }}</span>
        </div>
        <div><strong>时间：</strong>{{ order.createTime }}</div>
        <div class="admin-actions">
          <select v-model="order.newStatus" @change="updateStatus(order.id, order.newStatus)">
            <option value="">修改状态</option>
            <option value="pending">待处理</option>
            <option value="processing">处理中</option>
            <option value="completed">已完成</option>
          </select>
          <button @click="handleDelete(order.id)" class="delete-btn">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllOrders, updateOrderStatus, deleteOrder } from '@/api/repair'

const router = useRouter()
const account = localStorage.getItem('account') || ''
const allOrders = ref([])
const filterStatus = ref('')

const getStatusText = (status) => {
  const map = { 
    'pending': '待处理', 
    'processing': '处理中', 
    'completed': '已完成', 
    'cancelled': '已取消' 
  }
  return map[status] || status
}

const loadAllOrders = async () => {
  try {
    const res = await getAllOrders(filterStatus.value)
    if (res.code === 200) {
      allOrders.value = res.data.map(order => ({ ...order, newStatus: '' }))
    }
  } catch (error) {
    console.error('加载失败', error)
  }
}

const updateStatus = async (orderId, status) => {
  if (!status) return
  
  try {
    const res = await updateOrderStatus(orderId, status)
    if (res.code === 200) {
      ElMessage.success('状态已更新')
      loadAllOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleDelete = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个报修单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteOrder(orderId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadAllOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const logout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(() => {
  loadAllOrders()
})
</script>

<style scoped>
.container {
  max-width: 900px;
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

.filter-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-row select {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.refresh-btn {
  width: auto;
  padding: 8px 20px;
  background: #67c23a;
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

.admin-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.admin-actions select {
  flex: 1;
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.delete-btn {
  width: auto;
  padding: 6px 15px;
  background: #f56c6c;
}

.empty {
  text-align: center;
  padding: 30px;
  color: #999;
}
</style>