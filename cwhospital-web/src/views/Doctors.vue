<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <span style="font-weight: 600">医生列表</span>
      </template>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="animalType" label="擅长动物" width="100" />
        <el-table-column prop="appointmentTime" label="工作时间" width="160" />
        <el-table-column label="可预约时段" min-width="200">
          <template #default="{ row }">
            <div class="slots-wrap">
              <el-tag v-for="slot in (row.availableSlots || [])" :key="slot" size="small" type="success"
                style="margin: 2px">{{ slot }}</el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllDoctors } from '../api'

const list = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAllDoctors()
    list.value = res.success ? (res.user || []) : []
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.slots-wrap {
  display: flex;
  flex-wrap: wrap;
}
</style>
