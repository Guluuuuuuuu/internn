<template>
  <div class="job-management">
    <div class="header">
      <h2>岗位管理与发布</h2>
      <div class="search-bar">
        <div class="date-range">
          <input type="date" v-model="startDate" placeholder="开始日期">
          <span>至</span>
          <input type="date" v-model="endDate" placeholder="结束日期">
        </div>
        <div class="search-input">
          <input type="text" v-model="searchKeyword" placeholder="请输入岗位名称搜索">
          <button class="search-btn">搜索</button>
        </div>
        <button class="add-btn" @click="openAddModal">
          <i class="fas fa-plus"></i>
        </button>
      </div>
    </div>

    <div class="content-card">
      <!-- 表头 -->
      <div class="table-header">
        <div class="header-cell"><i class="fas fa-minus-circle"></i></div>
        <div class="header-cell">岗位名称</div>
        <div class="header-cell">部门</div>
        <div class="header-cell">工作地点</div>
        <div class="header-cell">薪资待遇</div>
        <div class="header-cell">状态</div>
      </div>

      <!-- 岗位列表 -->
      <div class="job-list">
        <div v-for="job in jobs" 
             :key="job.jobId" 
             class="job-item"
             @click="openEditModal(job)">
          <div class="item-cell">
            <i class="fas fa-minus-circle delete-icon" @click.stop="confirmDelete(job)"></i>
          </div>
          <div class="item-cell">
            <span class="job-title">{{ job.jobTitle }}</span>
            <div class="job-stats">
              <span class="stat-item">
                <i class="fas fa-file-alt"></i> {{ job.applicationCount || 0 }}份简历
              </span>
              <span class="stat-item">
                <i class="fas fa-users"></i> {{ job.interviewCount || 0 }}位面试
              </span>
              <span class="stat-item">
                <i class="fas fa-check-circle"></i> {{ job.hiredCount || 0 }}/{{ job.planCount }}录取
              </span>
            </div>
          </div>
          <div class="item-cell">{{ job.department }}</div>
          <div class="item-cell">{{ job.location }}</div>
          <div class="item-cell">{{ job.salary }}</div>
          <div class="item-cell">
            <span :class="['status-badge', job.status === 0 ? 'ended' : '']">
              {{ job.status === 1 ? '招聘中' : '已关闭' }}
            </span>
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

    <!-- 新增/编辑岗位弹窗 -->
    <div v-if="showAddModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>
            <i class="fas fa-briefcase"></i> 
            {{ isEditing ? '编辑岗位' : '发布新岗位' }}
          </h3>
          <button class="close-btn" @click="closeAddModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="form-grid">
            <div class="form-group">
              <label><i class="fas fa-tag"></i> 岗位名称</label>
              <input v-model="newJob.title" required placeholder="请输入岗位名称">
            </div>
            <div class="form-group">
              <label><i class="fas fa-sitemap"></i> 所属部门</label>
              <input v-model="newJob.department" required placeholder="请输入所属部门">
            </div>
            <div class="form-group">
              <label><i class="fas fa-map-marker-alt"></i> 工作地点</label>
              <input v-model="newJob.location" required placeholder="请输入工作地点">
            </div>
            <div class="form-group">
              <label><i class="fas fa-money-bill-wave"></i> 薪资范围</label>
              <input v-model="newJob.salary" required placeholder="例如：150-200/天">
            </div>
            <div class="form-row">
              <div class="form-group small">
                <label><i class="fas fa-user-tie"></i> 工作类型</label>
                <select v-model="newJob.jobType" required>
                  <option value="">请选择工作类型</option>
                  <option value="全职">全职</option>
                  <option value="实习">实习</option>
                  <option value="兼职">兼职</option>
                </select>
              </div>
              <div class="form-group small">
                <label><i class="fas fa-graduation-cap"></i> 学历要求</label>
                <select v-model="newJob.education" required>
                  <option value="">请选择学历要求</option>
                  <option value="不限">不限</option>
                  <option value="大专">大专</option>
                  <option value="本科">本科</option>
                  <option value="硕士">硕士</option>
                  <option value="博士">博士</option>
                </select>
              </div>
              <div class="form-group small">
                <label><i class="fas fa-users"></i> 拟招录人数</label>
                <input type="number" v-model="newJob.planCount" required min="1" placeholder="请输入">
              </div>
            </div>
          </div>

          <div class="form-group">
            <label><i class="fas fa-align-left"></i> 职位描述</label>
            <textarea v-model="newJob.description" required placeholder="请详细描述该岗位的工作内容、职责等"></textarea>
          </div>
          <div class="form-group">
            <label><i class="fas fa-clipboard-list"></i> 任职要求</label>
            <textarea v-model="newJob.requirements" required placeholder="请描述该岗位所需的技能、经验等要求"></textarea>
          </div>
          
          <div class="form-buttons">
            <button type="button" class="cancel-btn" @click="closeAddModal">
              <i class="fas fa-times"></i> 取消
            </button>
            <button type="submit" class="submit-btn">
              <i class="fas fa-check"></i> 发布
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteModal" class="modal">
      <div class="modal-content confirm-modal">
        <div class="confirm-icon">
          <i class="fas fa-exclamation-circle"></i>
        </div>
        <h3>确认删除</h3>
        <p>确定要删除该岗位吗？此操作不可恢复。</p>
        <div class="confirm-buttons">
          <button class="cancel-btn" @click="showDeleteModal = false">
            取消
          </button>
          <button class="confirm-btn" @click="handleDelete">
            确认删除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'CompanyJobManagement',
  data() {
    return {
      jobs: [],
      searchKeyword: '',
      startDate: '',
      endDate: '',
      showAddModal: false,
      showDeleteModal: false,
      jobToDelete: null,
      newJob: {
        title: '',
        companyName: '',
        location: '',
        salary: '',
        jobType: '',
        education: '',
        description: '',
        requirements: '',
        companyId: null, // 将从登录信息中获取
        department: '',  // 添加部门字段
        planCount: 1,
        status: 1
      },
      isEditing: false,
    }
  },
  created() {
    this.fetchJobs();
    this.newJob.companyId = localStorage.getItem('companyId'); // 从登录信息获取公司ID
  },
  methods: {
    async fetchJobs() {
      try {
        const companyId = localStorage.getItem('companyId');
        const response = await axios.get(`http://localhost:8888/api/company/jobs?companyId=${companyId}`);
        this.jobs = response.data;
      } catch (error) {
        console.error('获取岗位列表失败:', error);
      }
    },
    openAddModal() {
      this.isEditing = false;
      this.showAddModal = true;
    },
    closeAddModal() {
      this.showAddModal = false;
      this.isEditing = false;
      this.resetNewJob();
    },
    resetNewJob() {
      this.newJob = {
        title: '',
        companyName: '',
        location: '',
        salary: '',
        jobType: '',
        education: '',
        description: '',
        requirements: '',
        companyId: localStorage.getItem('companyId'),
        department: '',
        planCount: 1,
        status: 1
      };
    },
    async handleSubmit() {
      try {
        const companyId = localStorage.getItem('companyId');
        const jobData = {
          ...this.newJob,
          companyId: parseInt(companyId),
          companyName: localStorage.getItem('companyName')
        };
        
        if (this.isEditing) {
          // 如果是编辑，使用PUT请求
          await axios.put(`http://localhost:8888/api/company/jobs/${this.newJob.id}`, jobData);
        } else {
          // 如果是新增，使用POST请求
          await axios.post('http://localhost:8888/api/company/jobs', jobData);
        }
        
        this.closeAddModal();
        this.fetchJobs();
      } catch (error) {
        console.error(this.isEditing ? '更新岗位失败:' : '创建岗位失败:', error);
      }
    },
    confirmDelete(job) {
      this.jobToDelete = job;
      this.showDeleteModal = true;
    },
    async handleDelete() {
      try {
        await axios.delete(`http://localhost:8888/api/company/jobs/${this.jobToDelete.jobId}`);
        this.showDeleteModal = false;
        this.jobToDelete = null;
        this.fetchJobs();
      } catch (error) {
        console.error('删除岗位失败:', error);
      }
    },
    openEditModal(job) {
      this.isEditing = true;
      this.showAddModal = true;
      this.newJob = {
        id: job.jobId,
        title: job.jobTitle,
        department: job.department,
        location: job.location,
        salary: job.salary,
        jobType: job.jobType,
        education: job.education,
        description: job.description,
        requirements: job.requirements,
        companyId: job.companyId,
        companyName: job.companyName,
        planCount: job.planCount,
        status: job.status
      };
    }
  }
}
</script>

<style scoped>
.job-management {
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
  grid-template-columns: 60px 1.5fr 1fr 1fr 1fr 100px;
  gap: 20px;
  padding: 15px 20px;
  background: #f8f9fa;
  border-radius: 10px;
  margin-bottom: 15px;
}

.header-cell,
.item-cell {
  padding: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.job-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.job-item {
  display: grid;
  grid-template-columns: 60px 1.5fr 1fr 1fr 1fr 100px;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 16px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s ease;
}

.job-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.delete-icon {
  color: #ff4d4f;
  font-size: 18px;
  cursor: pointer;
  position: relative;
  z-index: 2;
}

.job-stats {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.stat-item {
  margin-right: 12px;
  white-space: nowrap;
}

.stat-item i {
  margin-right: 4px;
  color: #CA4499;
}

.status-badge {
  background: #e6f7ff;
  color: #1890ff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-badge.ended {
  background: #f5f5f5;
  color: #999;
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

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 16px;
  width: 800px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.modal-header h3 {
  color: #CA4499;
  font-size: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.close-btn {
  background: none;
  border: none;
  color: #666;
  font-size: 20px;
  cursor: pointer;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group label i {
  color: #CA4499;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  height: 44px;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #CA4499;
  box-shadow: 0 0 0 2px rgba(202, 68, 153, 0.1);
  outline: none;
}

.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  height: 120px;
  resize: vertical;
  min-height: 120px;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

.submit-btn,
.cancel-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn {
  background: #CA4499;
  color: white;
  border: none;
}

.submit-btn:hover {
  background: #b93c89;
  transform: translateY(-1px);
}

.cancel-btn {
  background: #f5f5f5;
  border: 1px solid #ddd;
  color: #666;
}

.cancel-btn:hover {
  background: #ebebeb;
}

.confirm-modal {
  width: 400px;
  padding: 30px;
  text-align: center;
  border-radius: 16px;
  background: white;
}

.confirm-icon {
  font-size: 48px;
  color: #ff4d4f;
  margin-bottom: 20px;
}

.confirm-modal h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 16px;
}

.confirm-modal p {
  color: #666;
  margin-bottom: 24px;
}

.confirm-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.confirm-buttons button {
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.confirm-btn {
  background: #ff4d4f;
  color: white;
  border: none;
}

.confirm-btn:hover {
  background: #ff7875;
}

.cancel-btn {
  background: white;
  border: 1px solid #ddd;
  color: #666;
}

.cancel-btn:hover {
  background: #f5f5f5;
}

.job-title {
  font-weight: bold;
  display: inline;
  padding: 2px 0;
  background: linear-gradient(transparent 60%, rgba(202, 68, 153, 0.1) 40%);
}

/* 修改表单布局相关样式 */
.form-row {
  grid-column: span 2;
  display: flex;
  gap: 20px;
  justify-content: space-between;
}

.form-group.small {
  flex: 1;
  margin-bottom: 20px;
}

.form-group.small select,
.form-group.small input {
  width: 100%;
  padding: 8px 12px;
  height: 36px;
}

/* 删除之前的均分样式 */
.form-grid .form-group:nth-last-child(-n+3) {
  grid-column: unset;
  width: unset;
  display: unset;
}
</style> 