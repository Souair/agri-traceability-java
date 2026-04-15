import { createRouter, createWebHistory } from 'vue-router'
import TraceQueryView from '../views/TraceQueryView.vue'
import AdminView from '../views/AdminView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'trace', component: TraceQueryView },
    { path: '/admin', name: 'admin', component: AdminView }
  ]
})

export default router
