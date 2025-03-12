import { createRouter, createWebHashHistory } from 'vue-router';
import AdminDashboard from '../components/AdminDashboard.vue';
import StudentDashboard from '../components/StudentDashboard.vue';
import CompanyDashboard from '../components/CompanyDashboard.vue';
import SchoolDashboard from '../components/SchoolDashboard.vue';
import HelloWorld from '../components/HelloWorld.vue';
import UserManagement from '../components/admin/UserManagement.vue'
import Dashboard from '../components/admin/Dashboard.vue'
import JobManagement from '../components/admin/JobManagement.vue'
import CompanyManagement from '../components/admin/CompanyManagement.vue'
import SchoolManagement from '../components/admin/SchoolManagement.vue'
import CompanyDashboardHome from '../components/company/Dashboard.vue'
import StudentDashboardHome from '../components/student/Dashboard.vue'
import SchoolDashboardHome from '../components/school/Dashboard.vue'
import SchoolStudentManagement from '../components/school/StudentManagement.vue'
import SchoolCompanyManagement from '../components/school/CompanyManagement.vue'
import SchoolPositionManagement from '../components/school/PositionManagement.vue'
import SchoolReportManagement from '../components/school/ReportManagement.vue'
import CompanyJobManagement from '../components/company/JobManagement.vue'
import CompanyResumeManagement from '../components/company/ResumeManagement.vue'
import CompanyInterviewManagement from '../components/company/InterviewManagement.vue'
import CompanyInternManagement from '../components/company/InternManagement.vue'
import StudentJobBrowsing from '../components/student/JobBrowsing.vue'
import StudentResumeManagement from '../components/student/ResumeManagement.vue'
import StudentApplicationRecord from '../components/student/ApplicationRecord.vue'
import StudentInternshipReport from '../components/student/InternshipReport.vue'
import StudentFavoriteJobs from '../components/student/FavoriteJobs.vue'
import ResumeManagement from '../components/student/ResumeManagement.vue'


const routes = [
  { path: '/admin', name: 'AdminDashboard', component: AdminDashboard, children: [
    {
      path: '',
      name: 'AdminHome',
      component: Dashboard
    },
    {
      path: 'users',
      name: 'UserManagement',
      component: UserManagement
    },
    {
      path: 'jobs',
      name: 'JobManagement',
      component: JobManagement
    },
    {
      path: 'companies',
      name: 'CompanyManagement',
      component: CompanyManagement
    },
    {
      path: 'schools',
      name: 'SchoolManagement',
      component: SchoolManagement
    }
  ] },
  { path: '/student', name: 'StudentDashboard', component: StudentDashboard, children: [
    {
      path: '',
      name: 'StudentDashboardHome',
      component: StudentDashboardHome
    },
    {
      path: 'jobs',
      name: 'StudentJobBrowsing',
      component: StudentJobBrowsing
    },
    {
      path: 'resumes',
      name: 'StudentResumeManagement',
      component: StudentResumeManagement
    },
    {
      path: 'applications',
      name: 'StudentApplicationRecord',
      component: StudentApplicationRecord
    },
    {
      path: 'reports',
      name: 'StudentInternshipReport',
      component: StudentInternshipReport
    },
    {
      path: 'favorites',
      name: 'StudentFavoriteJobs',
      component: StudentFavoriteJobs
    },
    {
      path: 'resume',
      name: 'StudentResume',
      component: ResumeManagement
    }
  ] },
  { path: '/company', name: 'CompanyDashboard', component: CompanyDashboard, children: [
    {
      path: '',
      name: 'CompanyDashboardHome',
      component: CompanyDashboardHome
    },
    {
      path: 'jobs',
      name: 'CompanyJobManagement',
      component: CompanyJobManagement
    },
    {
      path: 'resumes',
      name: 'CompanyResumeManagement',
      component: CompanyResumeManagement
    },
    {
      path: 'interviews',
      name: 'CompanyInterviewManagement',
      component: CompanyInterviewManagement
    },
    {
      path: 'interns',
      name: 'CompanyInternManagement',
      component: CompanyInternManagement
    }
  ] },
  { path: '/school', name: 'SchoolDashboard', component: SchoolDashboard, children: [
    {
      path: '',
      name: 'SchoolDashboardHome',
      component: SchoolDashboardHome
    },
    {
      path: 'students',
      name: 'SchoolStudentManagement',
      component: SchoolStudentManagement
    },
    {
      path: 'companies',
      name: 'SchoolCompanyManagement',
      component: SchoolCompanyManagement
    },
    {
      path: 'positions',
      name: 'SchoolPositionManagement',
      component: SchoolPositionManagement
    },
    {
      path: 'reports',
      name: 'SchoolReportManagement',
      component: SchoolReportManagement
    }
  ] },
  { path: '/hello', name: 'HelloWorld', component: HelloWorld },
];
// 不要添加 /login 和 /register 的路由，让它们使用静态 HTML

const router = createRouter({
  history: createWebHashHistory(), // 改成 hash 模式
  routes
});


export default router; 

