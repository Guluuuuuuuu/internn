<template>
  <div class="internship-report">
    <div class="header">
      <h2>实习报告</h2>
      <div class="header-actions">
        <button class="draft-btn" @click="viewDrafts" title="草稿箱">
          <Vue3Lottie
            :animationData="draftBoxAnimation"
            :height="32"
            :width="32"
          />
        </button>
        <button class="add-btn" @click="createReport">
          <i class="fas fa-plus"></i> 新建报告
        </button>
      </div>
    </div>

    <div class="content-card">
      <!-- 报告列表 -->
      <div class="report-list" v-if="!showEditor">
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
              <button class="edit-btn" v-if="report.status === 'draft'" @click="editReport(report)">
                <i class="fas fa-edit"></i> 编辑
              </button>
              <button class="submit-btn" v-if="report.status === 'draft'" @click="submitReport(report.id)">
                <i class="fas fa-paper-plane"></i> 提交
              </button>
              <button class="view-btn" v-if="report.status !== 'draft'" @click="viewReport(report)">
                <i class="fas fa-eye"></i> 查看
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 编辑器 -->
      <div v-else class="editor-container">
        <div class="editor-header">
          <button class="back-btn" @click="closeEditor">
            <i class="fas fa-arrow-left"></i>
          </button>
          <h3>{{ isNewReport ? '新建实习报告' : '编辑实习报告' }}</h3>
        </div>
        
        <!-- 使用报告信息表单组件 -->
        <div class="editor-content">
          <ReportFormInfo v-model="currentReport" />
          
          <!-- 使用 MilkdownEditor 组件 -->
          <div class="milkdown-editor-wrapper">
            <MilkdownEditor 
              :value="currentReport.content"
              @update:value="currentReport.content = $event"
              @save="saveAsDraft"
            />
          </div>
          
          <div class="editor-actions">
            <button class="save-btn" @click="saveAsDraft">
              <i class="fas fa-save"></i> 保存草稿
            </button>
            <button class="submit-btn" @click="confirmSubmit()">
              <i class="fas fa-paper-plane"></i> 提交报告
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 提交确认对话框 -->
    <div class="confirmation-dialog" v-if="showConfirmDialog">
      <div class="confirm-dialog">
        <h3>确认提交报告？</h3>
        <div class="confirm-actions">
          <button class="cancel-btn" @click="showConfirmDialog = false">取消</button>
          <button class="confirm-btn" @click="doSubmitReport">确认</button>
        </div>
      </div>
    </div>
    
    <!-- 系统消息 -->
    <SystemMessage ref="systemMessage" />
  </div>
</template>

<script>
import { Vue3Lottie } from 'vue3-lottie';
import SystemMessage from '../common/SystemMessage.vue';
import ReportFormInfo from '../common/ReportFormInfo.vue';
import MilkdownEditor from '../common/MilkdownEditor.vue';
// 使用本地动画文件
import draftBoxAnimation from '@/assets/animations/draft-box.json';

export default {
  name: 'InternshipReport',
  components: {
    Vue3Lottie,
    SystemMessage,
    ReportFormInfo,
    MilkdownEditor
  },
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
      ],
      showEditor: false,
      isNewReport: true,
      showConfirmDialog: false,
      currentReport: {
        id: null,
        title: '',
        company: '',
        position: '',
        summary: '',
        content: '',
        startDate: '',
        endDate: '',
        status: 0,
        submitTime: null,
        feedback: null
      },
      draftBoxAnimation
    };
  },
  computed: {
    formattedReports() {
      return this.reports.map(report => {
        return {
          ...report,
          statusText: this.getStatusText(report.status),
          submitTimeText: report.submitTime || '尚未提交'
        };
      });
    }
  },
  methods: {
    createReport() {
      this.isNewReport = true;
      this.currentReport = {
        id: null,
        title: '',
        company: '',
        position: '',
        summary: '',
        content: '',
        startDate: '',
        endDate: '',
        status: 0,
        submitTime: null,
        feedback: null
      };
      this.showEditor = true;
    },
    editReport(report) {
      this.isNewReport = false;
      this.currentReport = JSON.parse(JSON.stringify(report));
      this.showEditor = true;
    },
    viewReport(report) {
      this.isNewReport = false;
      this.currentReport = JSON.parse(JSON.stringify(report));
      this.showEditor = true;
    },
    closeEditor() {
      this.showEditor = false;
    },
    saveAsDraft() {
      // 保存草稿逻辑
      if (!this.currentReport.title) {
        alert('请输入报告标题');
        return;
      }
      
      // 如果是新建报告，生成一个临时ID
      if (this.isNewReport) {
        this.currentReport.id = Date.now();
        this.reports.unshift(this.currentReport);
      } else {
        // 更新已有报告
        const index = this.reports.findIndex(r => r.id === this.currentReport.id);
        if (index !== -1) {
          this.reports[index] = { ...this.currentReport };
        }
      }
      
      alert('草稿已保存');
    },
    confirmSubmit() {
      // 验证表单
      if (!this.currentReport.title || !this.currentReport.company || 
          !this.currentReport.position || !this.currentReport.startDate || 
          !this.currentReport.endDate || !this.currentReport.summary || 
          !this.currentReport.content) {
        alert('请填写完整的报告内容');
        return;
      }
      
      this.showConfirmDialog = true;
    },
    doSubmitReport() {
      // 提交报告逻辑
      this.currentReport.status = 1;
      this.currentReport.submitTime = new Date().toLocaleString();
      
      const index = this.reports.findIndex(r => r.id === this.currentReport.id);
      if (index !== -1) {
        this.reports[index] = { ...this.currentReport };
      } else {
        this.currentReport.id = Date.now();
        this.reports.unshift(this.currentReport);
      }
      
      this.showConfirmDialog = false;
      this.showEditor = false;
      alert('报告已提交');
    },
    viewDrafts() {
      alert('查看草稿箱');
    },
    getStatusText(status) {
      switch (status) {
        case 0: return '草稿';
        case 1: return '已提交';
        case 2: return '已批阅';
        default: return '未知';
      }
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
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

.draft-btn {
  width: 42px;
  height: 42px;
  border-radius: 8px;
  background: rgba(202, 68, 153, 0.1);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.draft-btn:hover {
  background: rgba(202, 68, 153, 0.2);
  transform: scale(1.05);
}

.content-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  min-height: 600px;
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

.edit-btn, .submit-btn, .view-btn, .back-btn {
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

.back-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  transition: all 0.2s;
}

.back-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.editor-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
}

.editor-header {
  display: flex;
  align-items: center;
  padding: 10px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.editor-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.editor-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.report-basic-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.milkdown-editor-wrapper {
  flex: 1;
  min-height: 500px;
  margin-bottom: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  background-color: #fff;
}

.editor-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.save-btn, .submit-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.save-btn {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.submit-btn {
  background: #CA4499;
  color: white;
}

.confirmation-dialog {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.confirm-dialog {
  background: white;
  border-radius: 12px;
  padding: 20px;
  width: 400px;
  text-align: center;
}

.confirm-dialog h3 {
  margin-top: 0;
}

.confirm-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.cancel-btn, .confirm-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.confirm-btn {
  background: #CA4499;
  color: white;
}
</style> 