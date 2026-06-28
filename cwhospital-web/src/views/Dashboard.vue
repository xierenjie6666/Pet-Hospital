<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-total">
          <div class="stat-num">{{ stats.total }}</div>
          <div class="stat-label">今日预约总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-pending">
          <div class="stat-num">{{ stats.pending }}</div>
          <div class="stat-label">待处理预约</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-completed">
          <div class="stat-num">{{ stats.completed }}</div>
          <div class="stat-label">已完成预约</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-users">
          <div class="stat-num">{{ userCount }}</div>
          <div class="stat-label">注册用户数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header><span style="font-weight: 600">最近预约</span></template>
          <el-table :data="recentList" stripe size="small">
            <el-table-column prop="petName" label="宠物名" width="90" />
            <el-table-column prop="petType" label="宠物类型" width="90" />
            <el-table-column prop="serviceType" label="服务类型" width="100" />
            <el-table-column prop="appointmentDate" label="预约日期" width="110" />
            <el-table-column prop="appointmentTime" label="时间段" width="100" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header><span style="font-weight: 600">待处理预约</span></template>
          <div v-if="pendingList.length === 0" class="empty-tip">暂无待处理预约</div>
          <div v-else class="pending-list">
            <div v-for="item in pendingList" :key="item.id" class="pending-item">
              <div class="pending-info">
                <span class="pending-pet">{{ item.petName }}</span>
                <span class="pending-service">{{ item.serviceType }}</span>
              </div>
              <div class="pending-time">{{ item.appointmentDate }} {{ item.appointmentTime }}</div>
              <div class="pending-actions">
                <el-button type="success" size="small" @click="handleUpdate(item.id, 'CONFIRMED', 0)">确认</el-button>
                <el-button type="danger" size="small" @click="handleUpdate(item.id, 'REJECTED', 0)">拒绝</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodayStats, getRecentAppointments, getPendingAppointments, updateAppointment, getAllUsers } from '../api'

const stats = ref({ total: 0, pending: 0, completed: 0 })
const userCount = ref(0)
const recentList = ref([])
const pendingList = ref([])

const statusType = (s) => ({ PENDING: 'warning', CONFIRMED: 'success', REJECTED: 'danger', CANCELLED: 'info' }[s] || 'info')
const statusText = (s) => ({ PENDING: '待确认', CONFIRMED: '已确认', REJECTED: '已拒绝', CANCELLED: '已取消' }[s] || s)

const handleUpdate = async (id, status, isCancelled) => {
  try {
    const res = await updateAppointment({ id, status, isCancelled })
    if (res.success) {
      ElMessage.success('操作成功')
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch { ElMessage.error('网络错误') }
}

const loadData = async () => {
  try {
    const [statsRes, recentRes, pendingRes, usersRes] = await Promise.all([
      getTodayStats(), getRecentAppointments(8), getPendingAppointments(), getAllUsers()
    ])
    if (statsRes.success) stats.value = statsRes.user || {}
    if (recentRes.success) recentList.value = recentRes.user || []
    if (pendingRes.success) pendingList.value = (pendingRes.user || []).slice(0, 6)
    if (usersRes.success) userCount.value = (usersRes.user || []).length
  } catch {}
}

onMounted(loadData)
</script>

<style scoped>
.stat-card {
  text-align: center;
  padding: 20px 0;
  border-radius: 12px;
  border: none;
}
.stat-num {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.stat-total .stat-num { color: #409eff; }
.stat-pending .stat-num { color: #e6a23c; }
.stat-completed .stat-num { color: #67c23a; }
.stat-users .stat-num { color: #909399; }
.empty-tip {
  text-align: center;
  color: #c0c4cc;
  padding: 40px 0;
}
.pending-list {
  max-height: 380px;
  overflow-y: auto;
}
.pending-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.pending-item:last-child { border-bottom: none; }
.pending-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.pending-pet { font-weight: 600; font-size: 14px; }
.pending-service { color: #909399; font-size: 13px; }
.pending-time { color: #909399; font-size: 12px; margin-bottom: 8px; }
.pending-actions { display: flex; gap: 8px; }
</style>
