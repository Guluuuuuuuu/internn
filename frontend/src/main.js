import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import axios from 'axios'; // 引入封装的axios实例
import router from './router'; // 导入路由配置


const app = createApp(App);
app.use(ElementPlus);
app.use(router); // 使用路由
app.config.globalProperties.$axios = axios; // 将axios挂载到全局
console.log("Router:", router.getRoutes()); // 打印所有的路由
app.mount('#app')

// 把 router 挂载到 window，方便控制台调试
window.__VUE_ROUTER__ = router
