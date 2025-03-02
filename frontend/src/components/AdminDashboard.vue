<template>
  <div class="admin-dashboard">
    <el-container>
      <el-aside width="200px">
        <div class="sidebar-header">
          <h1>实习管理系统</h1>
        </div>
        <el-menu default-active="1" class="sidebar">
          <el-submenu index="1" title="系统管理">
            <el-menu-item index="1-1">
              <i class="el-icon-user"></i> 用户管理
            </el-menu-item>
            <el-menu-item index="1-2">
              <i class="el-icon-user-solid"></i> 角色管理
            </el-menu-item>
            <el-menu-item index="1-3">
              <i class="el-icon-menu"></i> 菜单管理
            </el-menu-item>
            <el-menu-item index="1-4">
              <i class="el-icon-office-building"></i> 部门管理
            </el-menu-item>
          </el-submenu>
          <el-submenu index="2" title="高级设置">
            <el-menu-item index="2-1">
              <i class="el-icon-document"></i> 字典管理
            </el-menu-item>
            <el-menu-item index="2-2">
              <i class="el-icon-bell"></i> 通知公告
            </el-menu-item>
          </el-submenu>
          <el-menu-item index="3">
            <i class="el-icon-goods"></i> 产品管理
          </el-menu-item>
          <el-menu-item index="4">
            <i class="el-icon-shopping-cart-full"></i> 订单管理
          </el-menu-item>
          <el-menu-item index="5">
            <i class="el-icon-data-analysis"></i> 统计分析
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <el-header class="header">
          <div class="header-content">
            <el-dropdown class="user-dropdown" trigger="click">
              <span class="el-dropdown-link">
                <strong style="color: white;">系统管理员</strong> <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu>
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item>退出</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        <el-breadcrumb>
          <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
          <el-breadcrumb-item to="/system">系统管理</el-breadcrumb-item>
          <el-breadcrumb-item>用户管理</el-breadcrumb-item>
        </el-breadcrumb>
        <el-row>
          <el-col :span="24">
            <el-card class="user-card">
              <h2>用户列表</h2>
              <el-table :data="users" style="width: 100%">
                <el-table-column prop="id" label="序号" width="100"></el-table-column>
                <el-table-column prop="name" label="用户名" width="150"></el-table-column>
                <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
                <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
                <el-table-column prop="status" label="状态" width="100"></el-table-column>
                <el-table-column label="操作" width="180">
                  <template #default="scope">
                    <el-button type="primary" @click="editUser(scope.row)">编辑</el-button>
                    <el-button type="danger" @click="deleteUser(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
    <footer class="footer">
      <p>© 2022-2024 ContiNew Admin | 京ICP备20220058649-3</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'AdminDashboard',
  data() {
    return {
      users: [] // 初始化为空数组
    };
  },
  async created() {
    try {
      const response = await this.$axios.get('/api/users'); // 根据你的实际接口调整
      this.users = response.data;
    } catch (error) {
      console.error('获取用户数据失败:', error);
    }
  },
  methods: {
    async editUser(user) {
      try {
        const response = await this.$axios.put(`/api/users/${user.id}`, user);
        console.log('更新成功:', response.data);
      } catch (error) {
        console.error('更新失败:', error);
      }
    },
    async deleteUser(user) {
      try {
        await this.$axios.delete(`/api/users/${user.id}`);
        this.users = this.users.filter(u => u.id !== user.id);
      } catch (error) {
        console.error('删除失败:', error);
      }
    }
  }
}
</script>

<style scoped>
/* 全局边距重置 (需在 App.vue 中设置) */
/* 注意：scoped 样式无法直接修改 html/body，这部分需放在 App.vue */
/* 
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}
*/

.admin-dashboard {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
  margin: 0;
  padding: 0;
}

/* 头部固定且不压缩 */
.header {
  height: 60px !important;
  margin: 0 !important;
  padding: 0 !important;
  flex-shrink: 0; /* 禁止被压缩 */
  z-index: 1000;  /* 确保在顶层 */
  position: relative;
}

/* 头部内容布局 */
.header-content {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  background-color: #6a5acd;
  color: white;
}

/* 侧边栏容器高度处理 */
.el-aside {
  height: 100%;
}

/* 主内容容器配置 */
.container {
  flex: 1;
  min-height: 0; /* 关键：解决 flex 容器滚动问题 */
  overflow: auto;
}

/* 覆盖 Element UI 默认边距 */
:deep(.el-container),
:deep(.el-aside),
:deep(.el-main) {
  margin: 0 !important;
  padding: 0 !important;
}

/* 侧边栏菜单样式穿透 */
:deep(.sidebar) {
  background-color: #4b0082;
  height: 100%;
}

:deep(.sidebar .el-menu-item) {
  color: white !important;
}

:deep(.sidebar .el-menu-item:hover) {
  background-color: #6a5acd !important;
}

/* 用户卡片样式 */
.user-card {
  margin: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 表格样式调整 */
:deep(.el-table) {
  border: 1px solid #e0e0e0;
  margin-top: 15px;
}

/* 页脚固定 */
.footer {
  flex-shrink: 0;
  text-align: center;
  padding: 10px;
  background-color: #6a5acd;
  color: white;
  position: sticky;
  bottom: 0;
  z-index: 1000;
}

/* 面包屑导航样式 */
:deep(.el-breadcrumb) {
  margin: 15px 20px;
  padding: 10px 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.sidebar-header {
  background-color: #5b019b;
  color: white;
  padding: 10px;
  text-align: center;
}

.user-dropdown {
  cursor: pointer;
}
</style>