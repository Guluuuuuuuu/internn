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
          <button class="search-btn" @click="searchInterns">搜索</button>
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
        <div class="header-item position-filter">
          <span v-if="selectedJobId">{{ selectedJobTitle }}</span>
          <span v-else>实习岗位</span>
          <div class="job-filter-dropdown" @click.stop="showJobFilter = !showJobFilter">
            <i class="fas fa-caret-down"></i>
            <div class="job-filter-menu" v-if="showJobFilter">
              <div 
                class="job-filter-item"
                :class="{ active: selectedJobId === null }"
                @click="handleJobSelect(null)"
              >
                全部岗位
              </div>
              <div 
                v-for="job in jobs" 
                :key="job.id"
                class="job-filter-item"
                :class="{ active: selectedJobId === job.id }"
                @click="handleJobSelect(job.id)"
              >
                {{ job.title }}
              </div>
            </div>
          </div>
        </div>
        <div class="header-item position-filter">
          <span v-if="selectedDepartment">{{ selectedDepartment }}</span>
          <span v-else>所在部门</span>
          <div class="job-filter-dropdown" @click.stop="showDepartmentFilter = !showDepartmentFilter">
            <i class="fas fa-caret-down"></i>
            <div class="job-filter-menu" v-if="showDepartmentFilter">
              <div 
                class="job-filter-item"
                :class="{ active: selectedDepartment === null }"
                @click="handleDepartmentSelect(null)"
              >
                全部部门
              </div>
              <div 
                v-for="department in departments" 
                :key="department"
                class="job-filter-item"
                :class="{ active: selectedDepartment === department }"
                @click="handleDepartmentSelect(department)"
              >
                {{ department }}
              </div>
            </div>
          </div>
        </div>
        <div class="header-item">实习时长</div>
        <div class="header-item">入职时间</div>
        <div class="header-item position-filter">
          <span v-if="selectedStatus">{{ selectedStatus }}</span>
          <span v-else>状态</span>
          <div class="job-filter-dropdown" @click.stop="showStatusFilter = !showStatusFilter">
            <i class="fas fa-caret-down"></i>
            <div class="job-filter-menu" v-if="showStatusFilter">
              <div 
                class="job-filter-item"
                :class="{ active: selectedStatus === null }"
                @click="handleStatusSelect(null)"
              >
                全部状态
              </div>
              <div 
                v-for="status in statuses" 
                :key="status"
                class="job-filter-item"
                :class="{ active: selectedStatus === status }"
                @click="handleStatusSelect(status)"
              >
                {{ status }}
              </div>
            </div>
          </div>
        </div>
        <div class="header-item">操作</div>
      </div>

      <!-- 实习生列表 -->
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
      <div v-else-if="filteredInterns.length === 0" class="empty-state">
        <i class="fas fa-user-graduate"></i>
        <p>暂无实习生数据</p>
      </div>
      <div v-else class="intern-list">
        <div v-for="intern in filteredInterns" :key="intern.id" class="intern-card">
          <div class="delete-btn">
            <i class="fas fa-minus-circle"></i>
          </div>
          <div class="info-item">{{ intern.name }}</div>
          <div class="info-item">{{ intern.position }}</div>
          <div class="info-item">{{ intern.department }}</div>
          <div class="info-item duration-cell" @click="showAttendance(intern)">
            {{ intern.duration }}
          </div>
          <div class="info-item">{{ intern.start_time }}</div>
          <div class="info-item">{{ intern.status }}</div>
          <div class="info-item">
            <button class="notify-btn" @click="notifyIntern(intern)">
              <i class="fas fa-bell"></i> 通知
            </button>
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

    <!-- 考勤日历弹窗 -->
    <div v-if="showAttendanceModal" class="attendance-modal">
      <div class="modal-content" ref="modalContent" :style="modalStyle" @mousedown="startDrag">
        <div class="modal-header">
          <h3>考勤记录 - {{ selectedIntern.name }}</h3>
          <button class="close-btn" @click="closeAttendanceModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <attendance-calendar
            :student-id="selectedIntern.studentId"
            :company-id="companyId"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AttendanceCalendar from '../attendance/AttendanceCalendar.vue'

export default {
  name: 'CompanyInternManagement',
  components: {
    AttendanceCalendar
  },
  data() {
    return {
      startDate: '',
      endDate: '',
      searchKeyword: '',
      showJobFilter: false,
      showDepartmentFilter: false,
      showStatusFilter: false,
      selectedJobId: null,
      selectedJobTitle: '',
      selectedDepartment: null,
      selectedStatus: null,
      jobs: [],
      departments: [],
      statuses: ['实习中', '已结束', '待入职'],
      interns: [],
      loading: false,
      companyId: null,
      showAttendanceModal: false,
      selectedIntern: null,
      modalStyle: {
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)'
      },
      isDragging: false,
      dragOffset: { x: 0, y: 0 }
    }
  },
  computed: {
    filteredInterns() {
      // 基于多个过滤条件筛选实习生
      return this.interns.filter(intern => {
        // 岗位过滤
        const jobMatch = !this.selectedJobId || intern.positionId === this.selectedJobId;
        
        // 部门过滤
        const departmentMatch = !this.selectedDepartment || intern.department === this.selectedDepartment;
        
        // 状态过滤
        const statusMatch = !this.selectedStatus || intern.status === this.selectedStatus;
        
        return jobMatch && departmentMatch && statusMatch;
      });
    }
  },
  created() {
    // 从localStorage获取公司ID
    this.companyId = localStorage.getItem('companyId');
    // 获取实习生数据
    this.fetchInterns();
  },
  mounted() {
    // 添加点击事件监听器，点击页面其他地方关闭下拉菜单
    document.addEventListener('click', this.closeDropdowns);
  },
  beforeUnmount() {
    // 确保在组件销毁前移除所有事件监听器
    document.removeEventListener('mousemove', this.doDrag);
    document.removeEventListener('mouseup', this.stopDrag);
    document.removeEventListener('click', this.closeDropdowns);
  },
  methods: {
    async fetchInterns() {
      if (!this.companyId) {
        console.error('未获取到公司ID');
        return;
      }
      
      this.loading = true;
      try {
        // 获取实习生列表
        const response = await fetch(`http://localhost:8888/api/interns?companyId=${this.companyId}`);
        if (!response.ok) {
          throw new Error('获取实习生数据失败');
        }
        
        const data = await response.json();
        console.log('获取到的实习生数据:', data);
        
        // 确保每个实习生有正确的studentId字段
        const interns = (data.interns || []).map(intern => {
          // 确保使用正确的studentId
          if (!intern.studentId) {
            console.warn(`实习生 ${intern.name} 缺少studentId，尝试从其他字段获取`);
            // 如果没有studentId字段，尝试从其他地方获取
            // 设置默认值为student表中的id 11（根据您的数据库示例）
            intern.studentId = 11;
          }
          
          console.log(`实习生 ${intern.name} 的studentId: ${intern.studentId}`);
          return intern;
        });
        
        // 获取每个实习生的考勤记录并计算实习时长
        for (let intern of interns) {
          if (!intern.studentId) {
            console.warn(`实习生 ${intern.name} 缺少studentId，无法获取考勤记录`);
            intern.duration = '0小时';
            continue;
          }
          
          try {
            console.log(`获取考勤记录 - studentId: ${intern.studentId}, companyId: ${this.companyId}`);
            const attendanceResponse = await fetch(
              `http://localhost:8888/api/attendance?studentId=${intern.studentId}&companyId=${this.companyId}&year=${new Date().getFullYear()}&month=${new Date().getMonth() + 1}`
            );
            
            if (attendanceResponse.ok) {
              const attendanceData = await attendanceResponse.json();
              console.log(`实习生 ${intern.name} 获取到 ${attendanceData.length} 条考勤记录`);
              
              const totalHours = attendanceData.reduce((sum, record) => {
                return sum + (record.workHours || 0);
              }, 0);
              intern.duration = `${totalHours}小时`;
            } else {
              console.warn(`获取实习生 ${intern.name} 的考勤记录失败`);
              intern.duration = '0小时';
            }
          } catch (err) {
            console.error(`获取实习生 ${intern.name} 考勤记录出错:`, err);
            intern.duration = '0小时';
          }
        }
        
        this.interns = interns;
        this.jobs = data.jobs || [];
        
        // 提取所有部门
        this.departments = [...new Set(this.interns.map(intern => intern.department))].filter(Boolean);
        
      } catch (error) {
        console.error('获取实习生数据出错:', error);
      } finally {
        this.loading = false;
      }
    },
    handleJobSelect(jobId) {
      this.selectedJobId = jobId;
      // 如果选择"全部岗位"，清空选中的岗位标题
      if (jobId === null) {
        this.selectedJobTitle = '';
      } else {
        // 否则，查找选中岗位的标题
        const selectedJob = this.jobs.find(job => job.id === jobId);
        if (selectedJob) {
          this.selectedJobTitle = selectedJob.title;
        }
      }
      this.showJobFilter = false;
    },
    handleDepartmentSelect(department) {
      this.selectedDepartment = department;
      this.showDepartmentFilter = false;
    },
    handleStatusSelect(status) {
      this.selectedStatus = status;
      this.showStatusFilter = false;
    },
    closeDropdowns(event) {
      // 如果点击的不是下拉菜单，关闭所有下拉菜单
      if (!event.target.closest('.job-filter-dropdown')) {
        this.showJobFilter = false;
        this.showDepartmentFilter = false;
        this.showStatusFilter = false;
      }
    },
    searchInterns() {
      // 搜索功能，直接在现有数据中过滤
      console.log('搜索实习生，关键词:', this.searchKeyword);
      // 实际应用中可以调用后端API进行搜索
    },
    notifyIntern(intern) {
      // 通知功能，后续实现
      console.log('通知实习生:', intern.name);
    },
    showAttendance(intern) {
      console.log('原始实习生数据:', intern);
      
      // 确认使用正确的studentId
      // 这里需要使用studentId字段，而不是id字段
      if (!intern.studentId) {
        console.warn(`实习生 ${intern.name} 缺少studentId，使用默认值11`);
        intern.studentId = 11; // 根据您的数据库示例设置默认值
      }
      
      this.selectedIntern = intern;
      console.log('显示考勤日历，实习生信息:', this.selectedIntern);
      this.showAttendanceModal = true;
    },
    closeAttendanceModal() {
      this.showAttendanceModal = false;
      this.selectedIntern = null;
    },
    startDrag(event) {
      // 如果点击的是关闭按钮，不启动拖动
      if (event.target.closest('.close-btn')) return;
      
      this.isDragging = true;
      const rect = this.$refs.modalContent.getBoundingClientRect();
      this.dragOffset = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
      };
      
      // 添加移动和停止拖动的事件监听器
      document.addEventListener('mousemove', this.doDrag);
      document.addEventListener('mouseup', this.stopDrag);
    },
    doDrag(event) {
      if (!this.isDragging) return;
      
      // 计算新位置
      const x = event.clientX - this.dragOffset.x;
      const y = event.clientY - this.dragOffset.y;
      
      // 更新模态框位置
      this.modalStyle = {
        position: 'absolute',
        left: x + 'px',
        top: y + 'px',
        transform: 'none'
      };
    },
    stopDrag() {
      this.isDragging = false;
      // 移除事件监听器
      document.removeEventListener('mousemove', this.doDrag);
      document.removeEventListener('mouseup', this.stopDrag);
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
  grid-template-columns: 40px 0.8fr 1.2fr 1fr 0.8fr 1fr 0.6fr 0.8fr;
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
  position: relative;
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
  grid-template-columns: 40px 0.8fr 1.2fr 1fr 0.8fr 1fr 0.6fr 0.8fr;
  gap: 10px;
  background: white;
  padding: 20px;
  border-radius: 16px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}

.intern-card:hover {
  background: rgba(202, 68, 153, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
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
  display: flex;
  gap: 8px;
}

.notify-btn {
  padding: 4px 12px;
  height: 28px;
  border: none;
  border-radius: 6px;
  background: #CA4499;
  color: white;
  cursor: pointer;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
}

.notify-btn:hover {
  background: #b33b87;
  transform: translateY(-1px);
}

.notify-btn i {
  font-size: 12px;
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
  border-radius: 4px;
  cursor: pointer;
}

.page-number.active {
  background: #CA4499;
  color: white;
}

.ellipsis {
  display: flex;
  align-items: center;
  justify-content: center;
}

.prev-btn, .next-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: white;
}

/* 实习岗位下拉框样式 */
.position-filter {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.job-filter-dropdown {
  position: relative;
  margin-left: 8px;
}

.job-filter-menu {
  position: absolute;
  top: 100%;
  left: -20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 180px;
  z-index: 100;
  margin-top: 5px;
  overflow: hidden;
}

.job-filter-item {
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.job-filter-item:last-child {
  border-bottom: none;
}

.job-filter-item:hover {
  background: #f5f5f5;
}

.job-filter-item.active {
  background: #CA4499;
  color: white;
}

.job-filter-item:first-child {
  border-radius: 8px 8px 0 0;
}

.job-filter-item:last-child {
  border-radius: 0 0 8px 8px;
}

.job-filter-item:only-child {
  border-radius: 8px;
}

.job-filter-item.active:first-child {
  border-radius: 8px 8px 0 0;
}

.job-filter-item.active:last-child {
  border-radius: 0 0 8px 8px;
}

.job-filter-item.active:only-child {
  border-radius: 8px;
}

.header-minus {
  color: #9B55D3;
  font-size: 20px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #CA4499;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.empty-state i {
  font-size: 40px;
  color: #9B55D3;
  margin-bottom: 10px;
}

.empty-state p {
  font-size: 16px;
  color: #666;
}

.duration-cell {
  cursor: pointer;
  color: #CA4499;
  font-weight: 500;
  transition: all 0.3s ease;
}

.duration-cell:hover {
  opacity: 0.8;
  text-decoration: underline;
}

.attendance-modal {
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

.attendance-modal .modal-content {
  background: white;
  border-radius: 16px;
  padding: 24px;
  width: 420px;
  min-width: 420px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  position: absolute;
  cursor: move;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
}

.attendance-modal .modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  cursor: move;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.attendance-modal .modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.attendance-modal .close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  font-size: 20px;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 8px;
}

.attendance-modal .close-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.attendance-modal .modal-body {
  position: relative;
  padding: 0;
  display: flex;
  justify-content: center;
}
</style> 