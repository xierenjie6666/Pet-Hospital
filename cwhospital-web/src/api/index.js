import request from '../utils/request'

export const adminLogin = (data) => request.post('/api/admin/login', data)

export const getAllUsers = () => request.get('/api/user/all')
export const addUser = (data) => request.post('/api/user/add', data)
export const deleteUser = (id) => request.delete(`/api/user/delete/${id}`)
export const updateUser = (data) => request.put('/api/user/update', data)

export const getAllAppointments = () => request.get('/appointments/all')
export const getPendingAppointments = () => request.get('/appointments/pending')
export const getCancelRequests = () => request.get('/appointments/cancel-requests')
export const updateAppointment = (data) => request.post('/appointments/update', data)
export const getTodayStats = () => request.get('/appointments/stats/today')
export const getRecentAppointments = (limit = 5) => request.get('/appointments/recent', { params: { limit } })

export const getAllDoctors = () => request.get('/doctor/all')
export const getDoctorById = (id) => request.get(`/doctor/${id}`)

export const getAllAnnouncements = () => request.get('/announcement/all')
export const getEnabledAnnouncement = () => request.get('/announcement/enabled')
export const saveAnnouncement = (data) => request.post('/announcement/save', data)
export const enableAnnouncement = (id) => request.post(`/announcement/enable/${id}`)
export const disableAnnouncement = (id) => request.post(`/announcement/disable/${id}`)
export const deleteAnnouncement = (id) => request.delete(`/announcement/delete/${id}`)
