<template>
  <div class="internship-report">
    <div class="header">
      <h2>实习报告</h2>
      <button class="add-btn" @click="createReport">
        <i class="fas fa-plus"></i> 新建报告
      </button>
    </div>

    <div class="content-card">
      <div class="report-list">
        <div v-for="report in reports" :key="report.id" class="report-card">
          <div class="report-header">
            <div class="report-info">
              <h3>{{ report.title }}</h3>
              <div class="company-info">
                <img :src="report.companyLogo" alt="公司logo" class="company-logo">
                <span>{{ report.company }}</span>
                <span class="divider">|</span>
                <span>{{ report.position }}</span>
              </div>
            </div>
            <div class="status" :class="report.status">{{ report.status }}</div>
          </div>
          <div class="report-content">
            <p class="summary">{{ report.summary }}</p>
            <div class="report-meta">
              <div class="meta-item">
                <i class="fas fa-calendar-alt"></i>
                <span>实习时间：{{ report.duration }}</span>
              </div>
              <div class="meta-item">
                <i class="fas fa-clock"></i>
                <span>提交时间：{{ report.submitTime }}</span>
              </div>
            </div>
          </div>
          <div class="report-footer">
            <div class="feedback" v-if="report.feedback">
              <i class="fas fa-comment"></i>
              <span>导师评语：{{ report.feedback }}</span>
            </div>
            <div class="actions">
              <button class="edit-btn" v-if="report.status === 'draft'">
                <i class="fas fa-edit"></i> 编辑
              </button>
              <button class="submit-btn" v-if="report.status === 'draft'">
                <i class="fas fa-paper-plane"></i> 提交
              </button>
              <button class="view-btn" v-if="report.status !== 'draft'">
                <i class="fas fa-eye"></i> 查看
              </button>
            </div>
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
  name: 'StudentInternshipReport',
  data() {
    return {
      reports: [
        {
          id: 1,
          title: '前端开发实习总结报告',
          company: '阿里巴巴',
          companyLogo: '/company-logos/alibaba.png',
          position: '前端开发实习生',
          status: 'submitted',
          summary: '在为期三个月的实习期间，我主要参与了公司电商平台的前端开发工作，使用Vue.js框架进行开发...',
          duration: '2024-01-01 至 2024-03-31',
          submitTime: '2024-03-15',
          feedback: '报告内容详实，实习期间表现优秀，继续保持！'
        }
      ]
    }
  },
  methods: {
    createReport() {
      // 实现新建报告的逻辑
    }
  }
}
</script>

<style scoped>
.internship-report {
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

.add-btn {
  padding: 10px 20px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.content-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.report-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.report-card {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 16px;
  transition: all 0.3s;
}

.report-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.report-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.company-logo {
  width: 24px;
  height: 24px;
  border-radius: 4px;
}

.divider {
  color: #ddd;
}

.status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
}

.status.draft {
  background: rgba(0, 0, 0, 0.1);
  color: #666;
}

.status.submitted {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.status.approved {
  background: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.report-content {
  margin-bottom: 15px;
}

.summary {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 15px 0;
}

.report-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  color: #999;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.report-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.feedback {
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.actions {
  display: flex;
  gap: 10px;
}

.edit-btn, .submit-btn, .view-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.edit-btn {
  background: rgba(202, 68, 153, 0.1);
  color: #CA4499;
}

.submit-btn {
  background: #CA4499;
  color: white;
}

.view-btn {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
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