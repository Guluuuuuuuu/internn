import { createRouter, createWebHashHistory } from 'vue-router';
import AdminDashboard from '../components/AdminDashboard.vue';
import StudentDashboard from '../components/StudentDashboard.vue';
import CompanyDashboard from '../components/CompanyDashboard.vue';
import SchoolDashboard from '../components/SchoolDashboard.vue';
import HelloWorld from '../components/HelloWorld.vue';


const routes = [
  { path: '/admin', name: 'AdminDashboard', component: AdminDashboard },
  { path: '/student', component: StudentDashboard },
  { path: '/company', component: CompanyDashboard },
  { path: '/school', component: SchoolDashboard },
  { path: '/hello', name: 'HelloWorld', component: HelloWorld },
];
// 不要添加 /login 和 /register 的路由，让它们使用静态 HTML

const router = createRouter({
  history: createWebHashHistory(), // 改成 hash 模式
  routes
});


export default router; 

