import { createRouter, createWebHistory } from 'vue-router'
import AuthView from '../views/AuthView.vue'
import DashboardView from '../views/DashboardView.vue'
import OpsCenterView from '../views/OpsCenterView.vue'
import BatchCenterView from '../views/BatchCenterView.vue'
import TraceQueryView from '../views/TraceQueryView.vue'
import { authState, isManagerRole } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/auth', name: 'auth', component: AuthView },
    { path: '/dashboard', name: 'dashboard', component: DashboardView },
    { path: '/batch-center', name: 'batch-center', component: BatchCenterView },
    { path: '/trace', name: 'trace', component: TraceQueryView },
    { path: '/ops', name: 'ops', component: OpsCenterView, meta: { requiresManager: true } }
  ]
})

router.beforeEach((to) => {
  if (!to.meta?.requiresManager) {
    return true
  }

  if (!authState.user) {
    return { path: '/auth', query: { redirect: to.fullPath } }
  }

  if (!isManagerRole()) {
    return { path: '/dashboard' }
  }

  return true
})

export default router
