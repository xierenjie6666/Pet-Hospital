<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span style="font-weight: 600">公告管理</span>
          <el-button type="primary" size="small" @click="showAddDialog">
            <el-icon><Plus /></el-icon> 新建公告
          </el-button>
        </div>
      </template>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="updateTime" label="更新时间" width="170" />
        <el-table-column label="展示状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="small">
              {{ row.enabled ? '展示中' : '未展示' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.enabled" type="success" size="small" @click="handleEnable(row.id)">设为展示</el-button>
            <el-button v-if="row.enabled" type="warning" size="small" @click="handleDisable(row.id)">取消展示</el-button>
            <el-button type="primary" size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新建公告'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="设为展示">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllAnnouncements, saveAnnouncement, enableAnnouncement, disableAnnouncement, deleteAnnouncement } from '../api'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({ id: null, title: '', content: '', enabled: false })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAllAnnouncements()
    list.value = res.success ? (res.user || []) : []
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, title: '', content: '', enabled: false })
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    const res = await saveAnnouncement({ ...form })
    if (res.success) { ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
    else ElMessage.error(res.message || '保存失败')
  } catch { ElMessage.error('网络错误') }
}

const handleEnable = async (id) => {
  try {
    const res = await enableAnnouncement(id)
    if (res.success) { ElMessage.success('已设为展示'); loadData() }
    else ElMessage.error(res.message || '操作失败')
  } catch { ElMessage.error('网络错误') }
}

const handleDisable = async (id) => {
  try {
    const res = await disableAnnouncement(id)
    if (res.success) { ElMessage.success('已取消展示'); loadData() }
    else ElMessage.error(res.message || '操作失败')
  } catch { ElMessage.error('网络错误') }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' }).then(async () => {
    try {
      const res = await deleteAnnouncement(id)
      if (res.success) { ElMessage.success('删除成功'); loadData() }
      else ElMessage.error(res.message || '删除失败')
    } catch { ElMessage.error('网络错误') }
  }).catch(() => {})
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
