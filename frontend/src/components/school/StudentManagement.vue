<template>
  <div class="student-management">
    <div class="header">
      <h2>学生管理</h2>
      <div class="search-bar">
        <div class="date-range">
          <input type="date" v-model="startDate" placeholder="开始日期">
          <span>至</span>
          <input type="date" v-model="endDate" placeholder="结束日期">
        </div>
        <div class="search-input">
          <input type="text" v-model="searchKeyword" placeholder="请输入学生姓名搜索">
          <button class="search-btn">搜索</button>
        </div>
        <button class="add-btn">
          <i class="fas fa-plus"></i>
        </button>
      </div>
    </div>

    <div class="content-card">
      <!-- 表头 -->
      <div class="table-header">
        <div class="header-item">
          <i class="fas fa-minus-circle"></i>
        </div>
        <div class="header-item">姓名</div>
        <div class="header-item">学号</div>
        <div class="header-item">专业</div>
        <div class="header-item">年级</div>
        <div class="header-item">实习状态</div>
        <div class="header-item">操作</div>
      </div>

      <!-- 学生列表 -->
      <div class="student-list">
        <div v-for="student in students" :key="student.id" class="student-card">
          <div class="delete-btn">
            <i class="fas fa-minus-circle"></i>
          </div>
          <div class="info-item">{{ student.name }}</div>
          <div class="info-item">{{ student.studentId }}</div>
          <div class="info-item">{{ student.major }}</div>
          <div class="info-item">{{ student.grade }}</div>
          <div class="info-item">{{ student.status }}</div>
          <div class="info-item">
            <button class="edit-btn">查看</button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <button class="prev-btn"><i class="fas fa-chevron-left"></i></button>
        <div class="page-numbers">
          <span class="page-number active">1</span>
          <span class="page-number">2</span>
          <span class="page-number">3</span>
          <span class="ellipsis">...</span>
          <span class="page-number">10</span>
        </div>
        <button class="next-btn"><i class="fas fa-chevron-right"></i></button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SchoolStudentManagement',
  data() {
    return {
      startDate: '',
      endDate: '',
      searchKeyword: '',
      students: [
        {
          id: 1,
          name: '张三',
          studentId: '2021001',
          major: '计算机科学与技术',
          grade: '2021级',
          status: '实习中'
        }
      ]
    }
  }
}
</script>

<style scoped>
.student-management {
  flex: 1;
  padding: 30px;
  position: relative;
  z-index: 1;
  background: #f8f9fa;
  border-radius: 30px 0 0 30px;
  margin-left: -20px;
}

/* 复用管理员平台的样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  gap: 20px;
  align-items: center;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-range input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.search-input {
  display: flex;
  gap: 10px;
}

.search-input input {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  width: 200px;
}

.search-btn {
  padding: 8px 20px;
  background: #CA4499;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.add-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #52c41a;
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.content-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: grid;
  grid-template-columns: 40px 1fr 1fr 1.5fr 1fr 1fr 80px;
  gap: 20px;
  padding: 15px 20px;
  background: #f8f9fa;
  border-radius: 10px;
  margin-bottom: 15px;
}

.header-item {
  color: #666;
  font-weight: 500;
  font-size: 14px;
}

.student-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.student-card {
  display: grid;
  grid-template-columns: 40px 1fr 1fr 1.5fr 1fr 1fr 80px;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 16px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.delete-btn {
  color: #ff4d4f;
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-item {
  font-size: 14px;
  color: #333;
}

.edit-btn {
  padding: 4px 12px;
  border: none;
  border-radius: 6px;
  background: #CA4499;
  color: white;
  cursor: pointer;
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 10px;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
}

.page-number.active {
  background: #CA4499;
  color: white;
}

.prev-btn, .next-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 