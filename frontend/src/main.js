import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import axios from 'axios'; // 引入封装的axios实例
import router from './router'; // 导入路由配置
import '@/assets/styles/element-override.css';
import '@/assets/css/milkdown.css'; // 导入 Milkdown 编辑器样式
import '@/assets/css/custom-milkdown.css'; // 导入自定义 Milkdown 主题样式
import '@/assets/css/prism.css'; // 导入 Prism 代码高亮样式


const app = createApp(App);
app.use(ElementPlus, {
    // 自定义主题颜色
    themeOptions: {
      primary: '#CA4499'
    }
  });
app.use(ElementPlus);
app.use(router); // 使用路由
app.config.globalProperties.$axios = axios; // 将axios挂载到全局
console.log("Router:", router.getRoutes()); // 打印所有的路由
app.mount('#app')

// 把 router 挂载到 window，方便控制台调试
window.__VUE_ROUTER__ = router
