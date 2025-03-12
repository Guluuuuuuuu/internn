<template>
  <div class="favorite-jobs">
    <div class="header">
      <h2>收藏岗位</h2>
      <div class="search-bar">
        <div class="search-input">
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="请输入岗位名称搜索"
            @keyup.enter="handleSearch"
          >
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
      </div>
    </div>

    <div class="content-card">
      <div class="job-list">
        <div v-for="job in favoriteJobs" :key="job.id" class="job-card">
          <div class="job-header">
            <h3>{{ job.name }}</h3>
            <span class="salary">{{ job.salary }}</span>
          </div>
          <div class="company-info">
            <template v-if="job.company_logo">
              <img 
                :src="job.company_logo" 
                alt="公司logo" 
                class="company-logo"
                @error="handleImageError($event)"
              >
            </template>
            <template v-else>
              <i class="fas fa-building company-logo-icon"></i>
            </template>
            <span class="company-name">{{ job.company }}</span>
            <span class="location"><i class="fas fa-map-marker-alt"></i> {{ job.location }}</span>
          </div>
          <div class="job-tags">
            <span v-for="tag in job.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="job-description">
            <p>{{ job.description }}</p>
          </div>
          <div class="job-footer">
            <span class="collect-time">收藏时间：{{ job.collectTime }}</span>
            <div class="actions">
              <button class="uncollect-btn" @click="uncollectJob(job)">
                <i class="fas fa-star"></i>
                取消收藏
              </button>
              <button class="apply-btn" @click.stop="handleApplyJob(job)">申请岗位</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 0">
        <button class="prev-btn" @click="changePage(currentPage - 1)" :disabled="currentPage === 1">
          <i class="fas fa-chevron-left"></i>
        </button>
        <div class="page-numbers">
          <template v-for="pageNum in displayedPages" :key="pageNum">
            <span v-if="pageNum === '...'" class="ellipsis">...</span>
            <span v-else 
                  class="page-number" 
                  :class="{ active: pageNum === currentPage }"
                  @click="changePage(pageNum)">
              {{ pageNum }}
            </span>
          </template>
        </div>
        <button class="next-btn" @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>

    <!-- 优化申请确认弹窗 -->
    <div class="modal-overlay" v-if="showApplyModal" @click="closeApplyModal">
      <div class="apply-modal" @click.stop>
        <div class="modal-header">
          <h3>申请岗位</h3>
          <button class="close-btn" @click="closeApplyModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="job-info">
            <div class="job-title">{{ selectedJob.name }}</div>
            <div class="job-company">
              <i class="fas fa-building"></i>
              {{ selectedJob.company }}
            </div>
            <div class="job-salary">
              <i class="fas fa-yen-sign"></i>
              {{ selectedJob.salary }}
            </div>
          </div>
          
          <div class="confirm-message">
            <i class="fas fa-info-circle"></i>
            <span>确认要申请该岗位吗？</span>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeApplyModal">取消</button>
          <button class="confirm-btn" @click="confirmApply" :disabled="isSubmitting">
            <i class="fas fa-paper-plane" v-if="!isSubmitting"></i>
            <i class="fas fa-spinner fa-spin" v-else></i>
            {{ isSubmitting ? '申请中...' : '确认申请' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus'; // 导入Element Plus的消息组件

export default {
  name: 'StudentFavoriteJobs',
  data() {
    return {
      searchKeyword: '',
      favoriteJobs: [],
      currentPage: 1,
      totalPages: 0,
      pageSize: 3, //每页最多放三个
      defaultCompanyLogo: '/images/default-company.png', // 默认公司图标
      showApplyModal: false,
      selectedJob: {},
      isSubmitting: false
    }
  },
  computed: {
    displayedPages() {
      const pages = [];
      if (this.totalPages <= 7) {
        for (let i = 1; i <= this.totalPages; i++) {
          pages.push(i);
        }
      } else {
        pages.push(1);
        if (this.currentPage > 3) {
          pages.push('...');
        }
        for (let i = Math.max(2, this.currentPage - 1); 
             i <= Math.min(this.currentPage + 1, this.totalPages - 1); i++) {
          pages.push(i);
        }
        if (this.currentPage < this.totalPages - 2) {
          pages.push('...');
        }
        pages.push(this.totalPages);
      }
      return pages;
    }
  },
  methods: {
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return '';
      const date = new Date(dateTimeStr);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },

    async loadFavoriteJobs() {
      try {
        const studentId = localStorage.getItem('userId');
        if (!studentId) {
          console.error('No student ID found');
          return;
        }

        const params = new URLSearchParams({
          page: this.currentPage.toString(),
          size: this.pageSize.toString()
        });

        if (this.searchKeyword) {
          params.append('keyword', this.searchKeyword);
        }

        const response = await fetch(
          `http://localhost:8888/api/job-favorites/student/${studentId}/jobs?${params}`
        );
        
        if (!response.ok) {
          throw new Error('Failed to fetch favorite jobs');
        }

        const data = await response.json();
        this.favoriteJobs = data.jobs.map(job => ({
          id: job.id,
          name: job.title,
          salary: job.salary,
          company: job.company_name,
          company_logo: job.company_logo,
          location: job.location,
          tags: [job.job_type, job.education].filter(Boolean),
          description: job.description || '暂无描述',
          collectTime: this.formatDateTime(job.collect_time)
        }));
        
        this.totalPages = data.totalPages;
        this.currentPage = data.currentPage;
      } catch (error) {
        console.error('Error loading favorite jobs:', error);
      }
    },

    async uncollectJob(job) {
      try {
        const studentId = localStorage.getItem('userId');
        if (!studentId) return;

        const response = await fetch(`http://localhost:8888/api/job-favorites/student/${studentId}/job/${job.id}`, {
          method: 'DELETE'
        });

        if (response.ok) {
          // 从列表中移除该职位
          this.favoriteJobs = this.favoriteJobs.filter(j => j.id !== job.id);
        }
      } catch (error) {
        console.error('Error uncollecting job:', error);
      }
    },

    handleImageError(event) {
      // 替换为 FontAwesome 图标
      const parent = event.target.parentNode;
      const icon = document.createElement('i');
      icon.className = 'fas fa-building company-logo-icon';
      parent.replaceChild(icon, event.target);
    },

    async changePage(page) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
      await this.loadFavoriteJobs();
    },

    async handleSearch() {
      this.currentPage = 1; // 重置到第一页
      await this.loadFavoriteJobs();
    },

    // 新增处理申请岗位的方法
    handleApplyJob(job) {
      console.log('申请岗位:', job); // 添加调试日志
      this.selectedJob = job;
      this.showApplyModal = true;
    },
    
    // 修改关闭弹窗的方法
    closeApplyModal() {
      console.log('关闭弹窗'); // 添加调试日志
      this.showApplyModal = false;
    },
    
    // 修改确认申请的方法
    confirmApply() {
      console.log('确认申请:', this.selectedJob); // 添加调试日志
      this.isSubmitting = true;
      
      // 获取当前登录的学生ID
      const studentId = localStorage.getItem('userId');
      console.log('学生ID:', studentId);
      
      // 准备申请数据
      const applicationData = {
        studentId: studentId,
        jobId: this.selectedJob.id,
        status: 0 // 初始状态为已投递
      };
      
      // 发送申请请求
      axios.post('http://localhost:8888/api/applications', applicationData)
        .then(response => {
          console.log('申请成功:', response.data);
          ElMessage.success('申请成功！');
          this.closeApplyModal();
          this.loadFavoriteJobs();
        })
        .catch(error => {
          console.error('申请失败:', error);
          if (error.response && error.response.data && error.response.data.message) {
            ElMessage.error(error.response.data.message);
          } else {
            ElMessage.error('申请失败，请稍后重试');
          }
        })
        .finally(() => {
          this.isSubmitting = false;
        });
    },
    
    // 修改现有的操作按钮方法，添加申请功能
    handleAction(job, action) {
      if (action === 'apply') {
        this.handleApplyJob(job);
      } else if (action === 'view') {
        // 现有的查看详情逻辑
      } else if (action === 'remove') {
        // 现有的移除收藏逻辑
      }
    }
  },
  mounted() {
    this.loadFavoriteJobs();
  }
}
</script>

<style scoped>
.favorite-jobs {
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

.content-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.job-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.job-card {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 16px;
  transition: all 0.3s;
}

.job-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.job-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.salary {
  color: #f5222d;
  font-weight: bold;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.company-logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.company-logo-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #CA4499;
  background: rgba(202, 68, 153, 0.1);
  border-radius: 8px;
}

.company-name {
  color: #666;
}

.location {
  color: #666;
  font-size: 14px;
}

.location i {
  margin-right: 5px;
  color: #CA4499;
}

.job-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.tag {
  padding: 4px 12px;
  background: rgba(202, 68, 153, 0.1);
  color: #CA4499;
  border-radius: 20px;
  font-size: 12px;
}

.job-description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.collect-time {
  color: #999;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 10px;
}

.uncollect-btn, .apply-btn {
  padding: 8px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.uncollect-btn {
  background: rgba(245, 34, 45, 0.1);
  color: #f5222d;
}

.uncollect-btn i {
  color: #f5222d;
}

.apply-btn {
  background: #CA4499;
  color: white;
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

/* 优化申请弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
}

.apply-modal {
  background-color: white;
  border-radius: 12px;
  width: 450px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  animation: modal-appear 0.3s ease-out;
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #999;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
}

.close-btn:hover {
  background-color: #f5f5f5;
  color: #666;
}

.modal-body {
  padding: 24px;
}

.job-info {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.job-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.job-company, .job-salary {
  display: flex;
  align-items: center;
  color: #666;
  margin-bottom: 8px;
  font-size: 14px;
}

.job-company i, .job-salary i {
  width: 20px;
  color: #CA4499;
  margin-right: 8px;
}

.confirm-message {
  display: flex;
  align-items: center;
  background-color: rgba(202, 68, 153, 0.08);
  padding: 12px 16px;
  border-radius: 8px;
  color: #CA4499;
}

.confirm-message i {
  font-size: 18px;
  margin-right: 10px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.cancel-btn {
  padding: 10px 20px;
  background-color: white;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn:hover {
  border-color: #bbb;
  color: #333;
}

.confirm-btn {
  padding: 10px 24px;
  background-color: #CA4499;
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.confirm-btn:hover {
  background-color: #b93c89;
  transform: translateY(-1px);
  box-shadow: 0 2px 5px rgba(202, 68, 153, 0.3);
}

.confirm-btn:active {
  transform: translateY(0);
}

.confirm-btn:disabled {
  background-color: #e0e0e0;
  color: #a0a0a0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
</style> 