import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import BatchCenterView from '../views/BatchCenterView.vue'
import TraceQueryView from '../views/TraceQueryView.vue'
import AdminView from '../views/AdminView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/dashboard', name: 'dashboard', component: DashboardView },
    { path: '/batch-center', name: 'batch-center', component: BatchCenterView },
    { path: '/trace', name: 'trace', component: TraceQueryView },
    { path: '/admin', name: 'admin', component: AdminView }
  ]
})

export default router
