<template>
  <div class="school-dashboard">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <img :src="schoolInfo.avatar || '/images/default-avatar.png'" alt="学校头像" class="school-avatar">
        <h3>{{ schoolInfo.name || '学校用户' }}</h3>
      </div>
      <div class="menu-items">
        <div class="menu-item" :class="{ active: currentPath === '/school' }" @click="goTo('')">
          <i class="fas fa-chart-line"></i>
          <span>数据概览</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/school/students' }" @click="goTo('students')">
          <i class="fas fa-user-graduate"></i>
          <span>学生管理</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/school/companies' }" @click="goTo('companies')">
          <i class="fas fa-building"></i>
          <span>企业管理</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/school/positions' }" @click="goTo('positions')">
          <i class="fas fa-briefcase"></i>
          <span>岗位管理</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/school/reports' }" @click="goTo('reports')">
          <i class="fas fa-file-alt"></i>
          <span>实习报告</span>
        </div>
        <div class="menu-item">
          <i class="fas fa-cog"></i>
          <span>学校设置</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'SchoolDashboard',
  data() {
    return {
      schoolInfo: JSON.parse(localStorage.getItem('user') || '{}'),
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
        this.$router.push('/school')
      } else {
        this.$router.push(`/school/${path}`)
      }
    }
  }
}
</script>

<style scoped>
.school-dashboard {
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

.school-avatar {
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