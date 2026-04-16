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
    { path: '/auth', name: 'auth', component: AuthView, meta: { public: true } },
    { path: '/dashboard', name: 'dashboard', component: DashboardView, meta: { requiresAuth: true } },
    { path: '/batch-center', name: 'batch-center', component: BatchCenterView, meta: { requiresAuth: true } },
    { path: '/trace', name: 'trace', component: TraceQueryView, meta: { requiresAuth: true } },
    { path: '/ops', name: 'ops', component: OpsCenterView, meta: { requiresAuth: true, requiresManager: true } }
  ]
})

router.beforeEach((to) => {
  if (to.meta?.public && authState.user && to.path === '/auth') {
    const redirect = typeof to.query.redirect === 'string' ? to.query.redirect : '/dashboard'
    return redirect
  }

  if (to.meta?.requiresAuth && !authState.user) {
    return { path: '/auth', query: { redirect: to.fullPath } }
  }

  if (to.meta?.requiresManager && !isManagerRole()) {
    return { path: '/dashboard' }
  }

  return true
})

export default router
