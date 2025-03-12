<template>
  <div class="company-dashboard">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <img :src="companyInfo.avatar || '/images/default-avatar.png'" alt="企业头像" class="company-avatar">
        <h3>{{ companyInfo.name || '企业用户' }}</h3>
      </div>
      <div class="menu-items">
        <div class="menu-item" :class="{ active: $route.path === '/company' || $route.path === '/company/' }" @click="goTo('')">
          <i class="fas fa-chart-line"></i>
          <span>数据概览</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/company/jobs' }" @click="goTo('jobs')">
          <i class="fas fa-briefcase"></i>
          <span>岗位管理与发布</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/company/resumes' }" @click="goTo('resumes')">
          <i class="fas fa-users"></i>
          <span>简历筛选</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/company/interviews' }" @click="goTo('interviews')">
          <i class="fas fa-calendar-check"></i>
          <span>面试管理</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/company/interns' }" @click="goTo('interns')">
          <i class="fas fa-file-contract"></i>
          <span>实习生管理</span>
        </div>
        <div class="menu-item">
          <i class="fas fa-cog"></i>
          <span>企业设置</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'CompanyDashboard',
  data() {
    return {
      companyInfo: JSON.parse(localStorage.getItem('user') || '{}'),
      currentPath: this.$route.path
    }
  },
  watch: {
    '$route'(to) {
      this.currentPath = to.path
    }
  },
  methods: {
    goTo(path) {
      if (path === '') {
        this.$router.push('/company')
      } else {
        this.$router.push(`/company/${path}`)
      }
    }
  }
}
</script>

<style scoped>
.company-dashboard {
  display: flex;
  min-height: 100vh;
  background: #edb9d6;
}

.sidebar {
  width: 240px;
  padding: 20px 0;
  z-index: 2;
}

.sidebar-header {
  padding: 0 20px;
  margin-bottom: 30px;
  text-align: center;
}

.company-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 10px;
}

.menu-items {
  margin-top: 20px;
}

.menu-item {
  padding: 15px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.menu-item i {
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

.menu-item:hover {
  background: rgba(202, 68, 153, 0.1);
  width: 180px;
}

.menu-item.active {
  background: #f8f9fa;
  color: rgb(215, 53, 172);
  border-radius: 30px 0 0 30px;
  width: 180px;
}
</style>