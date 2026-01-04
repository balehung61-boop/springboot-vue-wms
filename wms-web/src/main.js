import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 1. 引入 Element Plus 和 它的 CSS 样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

app.use(router)
// 2. 安装 Element Plus
app.use(ElementPlus)

// 3. 注册所有图标 (方便以后用)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')