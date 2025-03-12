<template>
  <div class="intern-management">
    <div class="header">
      <h2>实习生管理</h2>
      <div class="search-bar">
        <div class="date-range">
          <input type="date" v-model="startDate" placeholder="开始日期">
          <span>至</span>
          <input type="date" v-model="endDate" placeholder="结束日期">
        </div>
        <div class="search-input">
          <input type="text" v-model="searchKeyword" placeholder="请输入姓名搜索">
          <button class="search-btn">搜索</button>
        </div>
      </div>
    </div>

    <div class="content-card">
      <!-- 表头 -->
      <div class="table-header">
        <div class="header-item">
          <i class="fas fa-minus-circle header-minus"></i>
        </div>
        <div class="header-item">姓名</div>
        <div class="header-item">实习岗位</div>
        <div class="header-item">所在部门</div>
        <div class="header-item">实习时长</div>
        <div class="header-item">入职时间</div>
        <div class="header-item">状态</div>
        <div class="header-item">操作</div>
      </div>

      <!-- 实习生列表 -->
      <div class="intern-list">
        <div v-for="intern in interns" :key="intern.id" class="intern-card">
          <div class="delete-btn">
            <i class="fas fa-minus-circle"></i>
          </div>
          <div class="info-item">{{ intern.name }}</div>
          <div class="info-item">{{ intern.position }}</div>
          <div class="info-item">{{ intern.department }}</div>
          <div class="info-item">{{ intern.duration }}</div>
          <div class="info-item">{{ intern.start_time }}</div>
          <div class="info-item">{{ intern.status }}</div>
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
  name: 'CompanyInternManagement',
  data() {
    return {
      startDate: '',
      endDate: '',
      searchKeyword: '',
      interns: [
        {
          id: 1,
          name: '张三',
          position: '前端开发实习生',
          department: '技术部',
          duration: '120小时',
          start_time: '2024.03.01',
          status: '实习中'
        }
      ]
    }
  }
}
</script>

<style scoped>
.intern-management {
  flex: 1;
  padding: 30px;
  position: relative;
  z-index: 1;
  background: #f8f9fa;
  border-radius: 30px 0 0 30px;
  margin-left: -20px;
}

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
  grid-template-columns: 40px 0.8fr 1.2fr 1fr 0.8fr 1fr 0.6fr 0.6fr;
  gap: 10px;
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

.table-header .header-item:first-child {
  color: #9B55D3;
  display: flex;
  align-items: center;
  justify-content: center;
}

.intern-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.intern-card {
  display: grid;
  grid-template-columns: 40px 0.8fr 1.2fr 1fr 0.8fr 1fr 0.6fr 0.6fr;
  gap: 10px;
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

.header-minus {
  color: #9B55D3;
  font-size: 20px;
}
</style> 