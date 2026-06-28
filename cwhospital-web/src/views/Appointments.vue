<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span style="font-weight: 600">预约列表</span>
          <el-radio-group v-model="tab" size="small" @change="loadData">
            <el-radio-button value="all">全部</el-radio-button>
            <el-radio-button value="pending">待处理</el-radio-button>
            <el-radio-button value="cancel">撤销申请</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="petName" label="宠物名" width="90" />
        <el-table-column prop="petType" label="宠物类型" width="90" />
        <el-table-column prop="serviceType" label="服务类型" width="110" />
        <el-table-column prop="doctor" label="医生" width="90" />
        <el-table-column prop="appointmentDate" label="预约日期" width="110" />
        <el-table-column prop="appointmentTime" label="时间段" width="100" />
        <el-table-column prop="symptoms" label="症状" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING'">
              <el-button type="success" size="small" @click="handleUpdate(row.id, 'CONFIRMED', 0)">确认</el-button>
              <el-button type="danger" size="small" @click="handleUpdate(row.id, 'REJECTED', 0)">拒绝</el-button>
            </template>
            <template v-if="row.isCancelled === 1">
              <el-button type="warning" size="small" @click="handleUpdate(row.id, 'CANCELLED', 2)">同意撤销</el-button>
              <el-button type="primary" size="small" @click="handleUpdate(row.id, row.status, 0)">拒绝撤销</el-button>
            </template>
            <span v-if="row.status !== 'PENDING' && row.isCancelled !== 1" style="color: #909399; font-size: 12px">已处理</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllAppointments, getPendingAppointments, getCancelRequests, updateAppointment } from '../api'

const tab = ref('all')
const list = ref([])
const loading = ref(false)

const statusType = (s) => ({ PENDING: 'warning', CONFIRMED: 'success', REJECTED: 'danger', CANCELLED: 'info' }[s] || 'info')
const statusText = (s) => ({ PENDING: '待确认', CONFIRMED: '已确认', REJECTED: '已拒绝', CANCELLED: '已取消' }[s] || s)

const loadData = async () => {
  loading.value = true
  try {
    let res
    if (tab.value === 'pending') res = await getPendingAppointments()
    else if (tab.value === 'cancel') res = await getCancelRequests()
    else res = await getAllAppointments()
    list.value = res.success ? (res.user || []) : []
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleUpdate = async (id, status, isCancelled) => {
  try {
    const res = await updateAppointment({ id, status, isCancelled })
    if (res.success) { ElMessage.success('操作成功'); loadData() }
    else ElMessage.error(res.message || '操作失败')
  } catch { ElMessage.error('网络错误') }
}

onMounted(loadData)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
