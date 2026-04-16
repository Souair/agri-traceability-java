import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './style.css'
import { restoreSession } from './stores/auth'

async function bootstrap() {
  await restoreSession()
  createApp(App).use(router).use(ElementPlus).mount('#app')
}

bootstrap()
