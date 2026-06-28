import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../layout/AdminLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'appointments',
        name: 'Appointments',
        component: () => import('../views/Appointments.vue'),
        meta: { title: '预约管理' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'doctors',
        name: 'Doctors',
        component: () => import('../views/Doctors.vue'),
        meta: { title: '医生管理' }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('../views/Announcements.vue'),
        meta: { title: '公告管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const admin = sessionStorage.getItem('admin')
  if (to.matched.some(r => r.meta.requiresAuth) && !admin) {
    next('/login')
  } else if (to.path === '/login' && admin) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
