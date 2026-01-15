import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router, { setupRouterGuards } from './router'
import './styles/theme.css'
import './styles/base.css'
import App from './App.vue'

// 函数级注释：创建并挂载应用，安装路由与状态管理。
// 负责：创建应用、注册全局插件、做一次性全局初始化。
const pinia = createPinia()
setupRouterGuards(pinia)
createApp(App).use(pinia).use(router).mount('#app')
