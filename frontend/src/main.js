import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'
import { restoreSession } from './stores/auth'

async function bootstrap() {
  await restoreSession()
  createApp(App).use(router).mount('#app')
}

bootstrap()
