<template>
  <div class="admin-dashboard">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <img :src="adminInfo.avatar || '/images/default-avatar.png'" alt="管理员头像" class="admin-avatar">
        <h3>{{ adminInfo.name || 'root' }}</h3>
      </div>
      <div class="menu-items">
        <div class="menu-item" :class="{ active: currentPath === '/admin' }" @click="goTo('')">
          <i class="fas fa-chart-line"></i>
          <span>数据概览</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/admin/users' }" @click="goTo('users')">
          <i class="fas fa-users"></i>
          <span>用户管理</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/admin/jobs' }" @click="goTo('jobs')">
          <i class="fas fa-briefcase"></i>
          <span>职位管理</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/admin/companies' }" @click="goTo('companies')">
          <i class="fas fa-building"></i>
          <span>企业管理</span>
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/admin/schools' }" @click="goTo('schools')">
          <i class="fas fa-school"></i>
          <span>学校管理</span>
        </div>
        <div class="menu-item">
          <i class="fas fa-cog"></i>
          <span>系统设置</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'AdminDashboard',
  data() {
    return {
      adminInfo: JSON.parse(localStorage.getItem('user') || '{}'),
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
        this.$router.push('/admin')
      } else {
        this.$router.push(`/admin/${path}`)
      }
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
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

.admin-avatar {
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
  border-radius:30px  0 0 30px ;

  
}
</style>