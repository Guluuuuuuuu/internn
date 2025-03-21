<template>
  <div class="resume-management">
    <div class="header">
      <div class="title-container">
        <h2>简历筛选</h2>
        <!-- 将图标放在标题旁边 -->
        <i class="fas fa-map-marker-alt address-icon" @click="showTemplateModal = true"></i>
      </div>
      <div class="search-bar">
        <div class="date-range">
          <input type="date" v-model="startDate" placeholder="开始日期">
          <span>至</span>
          <input type="date" v-model="endDate" placeholder="结束日期">
        </div>
        <div class="search-input">
          <input type="text" v-model="searchKeyword" placeholder="请输入姓名搜索">
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
      </div>
    </div>

    <div class="content-card">
      <div class="table-header">
        <div class="header-item">
          <i class="fas fa-minus-circle"></i>
        </div>
        <div class="header-item">姓名</div>
        <div class="header-item position-filter">
          <span v-if="selectedJobId">{{ selectedJobTitle }}</span>
          <span v-else>应聘岗位</span>
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
                :key="job.jobId"
                class="job-filter-item"
                :class="{ active: selectedJobId === job.jobId }"
                @click="handleJobSelect(job.jobId)"
              >
                {{ job.jobTitle }}
              </div>
            </div>
          </div>
        </div>
        <div class="header-item">学校</div>
        <div class="header-item">专业</div>
        <div class="header-item">投递时间</div>
        <div class="header-item">状态</div>
        <div class="header-item">操作</div>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
      </div>
      
      <div v-else class="resume-list">
        <div 
          v-for="resume in resumes" 
          :key="resume.applicationId" 
          class="resume-item"
          :class="{ 'hover-effect': true }"
          @click="showResumeDetail(resume)"
        >
          <div class="item-cell"><i class="fas fa-minus-circle"></i></div>
          <div class="item-cell">{{ resume.studentName }}</div>
          <div class="item-cell">{{ resume.jobTitle }}</div>
          <div class="item-cell">{{ resume.schoolName }}</div>
          <div class="item-cell">{{ resume.major }}</div>
          <div class="item-cell">{{ new Date(resume.applyTime).toLocaleDateString() }}</div>
          <div class="item-cell">
            <span 
              class="status-tag" 
              :class="{ 
                'status-pending': resume.applicationStatus === 0, 
                'status-processed': resume.applicationStatus !== 0 
              }"
            >
              {{ resume.applicationStatus === 0 ? '待处理' : '已处理' }}
            </span>
          </div>
          <div class="item-cell">
            <button 
              v-if="resume.applicationStatus === 0"
              class="interview-btn" 
              @click.stop="openInterviewInvite(resume)"
            >通知面试</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 简历详情弹窗 -->
    <div v-if="showResumeModal" class="company-resume-modal" @click.self="closeResumeModal">
      <!-- 将按钮移到弹窗外部 -->
      <div class="company-resume-actions-outside" v-if="selectedResume.applicationStatus === 0">
        <button class="company-interview-btn" @click="openInterviewInvite(selectedResume)">通知面试</button>
        <button class="company-reject-btn" @click="rejectApplication(selectedResume)">不通过</button>
      </div>
      
      <div class="company-resume-view">
        <div class="company-resume-basic-info">
          <div class="company-info-left">
            <h2>{{ selectedResume.name || selectedResume.studentName || '姓名' }} 
              <span>{{ selectedResume.age || '' }} {{ selectedResume.education || '' }}</span>
            </h2>
            <div class="company-certificates">
              <p v-if="selectedResume.certificates"><strong>资格证书:</strong> {{ selectedResume.certificates }}</p>
              <p v-if="selectedResume.english_level || selectedResume.englishLevel">
                <strong>英语能力:</strong> {{ selectedResume.english_level || selectedResume.englishLevel }}
              </p>
            </div>
          </div>
          <div class="company-info-right">
            <div class="company-photo">
              <img v-if="selectedResume.photo" :src="selectedResume.photo" alt="个人照片">
              <div v-else class="company-photo-placeholder">
                <i class="fas fa-user"></i>
              </div>
            </div>
          </div>
        </div>

        <div class="company-resume-section">
          <div class="company-section-header">
            <h3><i class="fas fa-user"></i> 个人简介</h3>
          </div>
          <div class="company-section-content company-markdown-content" 
               v-html="renderMarkdown(selectedResume.introduction || '')">
          </div>
        </div>

        <div class="company-resume-section">
          <div class="company-section-header">
            <h3><i class="fas fa-graduation-cap"></i> 教育背景</h3>
          </div>
          <div class="company-section-content company-markdown-content" 
               v-html="renderMarkdown(selectedResume.education_background || '')">
          </div>
        </div>

        <div class="company-resume-section">
          <div class="company-section-header">
            <h3><i class="fas fa-trophy"></i> 竞赛经历</h3>
          </div>
          <div class="company-section-content company-markdown-content" 
               v-html="renderMarkdown(selectedResume.competition_experience || '')">
          </div>
        </div>

        <div class="company-resume-section">
          <div class="company-section-header">
            <h3><i class="fas fa-briefcase"></i> 实习实践经历</h3>
          </div>
          <div class="company-section-content company-markdown-content" 
               v-html="renderMarkdown(selectedResume.internship_experience || selectedResume.experience || '')">
          </div>
        </div>

        <div class="company-resume-section">
          <div class="company-section-header">
            <h3><i class="fas fa-flask"></i> 科研经历</h3>
          </div>
          <div class="company-section-content company-markdown-content" 
               v-html="renderMarkdown(selectedResume.research_experience || '')">
          </div>
        </div>

        <div class="company-resume-section">
          <div class="company-section-header">
            <h3><i class="fas fa-star"></i> 文体特长</h3>
          </div>
          <div class="company-section-content company-markdown-content" 
               v-html="renderMarkdown(selectedResume.skills || '')">
          </div>
        </div>
      </div>
    </div>
    
    <!-- 面试地址管理弹窗 -->
    <div v-if="showTemplateModal" class="address-modal">
      <div class="address-modal-content">
        <div class="address-modal-header">
          <div class="header-left">
            <h3>面试地址</h3>
          </div>
          <div class="header-right">
            <button class="manage-btn" @click="toggleManageMode">
              <i class="fas fa-sliders-h"></i> {{ isManageMode ? '退出管理' : '管理' }}
            </button>
            <button class="add-address-btn" @click="showAddTemplate">
              <i class="fas fa-plus"></i> 新增地址
            </button>
            <button class="close-btn" @click="showTemplateModal = false">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        
        <div class="address-list">
          <div 
            v-for="template in interviewTemplates" 
            :key="template.id"
            class="address-item"
            :class="{ 'default-address': template.isDefault }"
          >
            <div class="address-info">
              <div class="address-main">
                {{ template.address }}
                <span v-if="template.isDefault" class="default-tag">默认</span>
              </div>
              <div class="address-detail">
                {{ template.interviewerDepartment }} {{ template.interviewerName }} {{ template.interviewerEmail }}
              </div>
            </div>
            
            <div class="address-actions">
              <div v-if="isManageMode" class="default-checkbox">
                <label class="custom-checkbox">
                  <input 
                    type="checkbox" 
                    :checked="template.isDefault" 
                    @change="setDefaultTemplate(template.id)"
                    :disabled="template.isDefault"
                  >
                  <span class="checkmark"></span>
                  <span class="checkbox-label">默认</span>
                </label>
              </div>
              
              <button v-if="!isManageMode" class="edit-btn" @click="editTemplate(template)">
                <i class="fas fa-pencil-alt"></i>
              </button>
              <button v-else class="delete-btn" @click="confirmDeleteTemplate(template)">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 添加/编辑地址表单 -->
      <div v-if="showTemplateForm" class="address-form-overlay">
        <div class="address-form">
          <div class="form-header">
            <h4>{{ isEditingTemplate ? '编辑面试地址' : '新增面试地址' }}</h4>
            <button class="close-form-btn" @click="cancelTemplateForm">
              <i class="fas fa-times"></i>
            </button>
          </div>
          
          <div class="form-content">
            <div class="form-section">
              <h5>基本信息</h5>
              <div class="form-group">
                <label>面试地点</label>
                <input type="text" v-model="templateForm.address" placeholder="请输入面试地点">
              </div>
              
              <div class="form-group">
                <label>模板名称</label>
                <input type="text" v-model="templateForm.templateName" placeholder="请输入模板名称">
              </div>

              <!-- 修改为并排布局 -->
              <div class="form-row">
                <div class="form-group half">
                  <label>默认面试时间</label>
                  <input 
                    type="datetime-local" 
                    v-model="templateForm.interviewTime"
                    class="form-input"
                    placeholder="请选择默认面试时间"
                  >
                </div>
                
                <div class="form-group half">
                  <label>接受邀请时效</label>
                  <div class="expire-days-wrapper">
                    <input 
                      type="number" 
                      v-model="templateForm.expireDays"
                      class="form-input short-input"
                      min="1"
                      max="30"
                      placeholder="请输入"
                    >
                    <span class="expire-days-unit">天</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="form-section">
              <h5>面试官信息</h5>
              <div class="form-row">
                <div class="form-group half">
                  <label>面试官姓名</label>
                  <input 
                    type="text" 
                    v-model="templateForm.interviewerName" 
                    placeholder="请输入姓名"
                    class="custom-input"
                  >
                </div>
                
                <div class="form-group half">
                  <label>面试官部门</label>
                  <input 
                    type="text" 
                    v-model="templateForm.interviewerDepartment" 
                    placeholder="请输入部门"
                    class="custom-input"
                  >
                </div>
              </div>
              
              <div class="form-group">
                <label>面试官邮箱</label>
                <input type="text" v-model="templateForm.interviewerEmail" placeholder="请输入邮箱">
              </div>
            </div>
            
            <div class="form-group checkbox-group">
              <input type="checkbox" id="defaultAddress" v-model="templateForm.isDefault">
              <label for="defaultAddress">设为默认面试地址</label>
            </div>
          </div>
          
          <div class="form-footer">
            <button class="cancel-btn" @click="cancelTemplateForm">取消</button>
            <button class="save-btn" @click="saveTemplate">保存</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 通知面试弹窗 -->
    <div v-if="showInterviewModal" class="interview-modal" @click.self="showInterviewModal = false">
      <div class="interview-modal-content">
        <div class="interview-modal-header">
          <h3>通知面试</h3>
          <button class="interview-close-btn" @click="showInterviewModal = false">×</button>
        </div>
        
        <div class="interview-form">
          <div class="student-info">
            <p>应聘者: <strong>{{ selectedResume.studentName }}</strong></p>
            <p>应聘岗位: <strong>{{ selectedResume.jobTitle }}</strong></p>
          </div>
          
          <div class="form-group">
            <label>面试模板</label>
            <select v-model="interviewForm.templateId">
              <option v-for="template in interviewTemplates" :key="template.id" :value="template.id">
                {{ template.templateName }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label>面试时间</label>
            <input type="datetime-local" v-model="interviewForm.interviewTime">
          </div>
          
          <div class="form-group">
            <label>面试地点</label>
            <input type="text" v-model="interviewForm.address" placeholder="请输入面试地点">
          </div>
          
          <div class="form-group">
            <label>面试官</label>
            <input type="text" v-model="interviewForm.interviewerName" placeholder="请输入面试官姓名">
          </div>
          
          <div class="form-group">
            <label>备注</label>
            <textarea v-model="interviewForm.notes" placeholder="请输入备注信息"></textarea>
          </div>
          
          <div class="form-actions">
            <button class="cancel-btn" @click="showInterviewModal = false">取消</button>
            <button class="send-btn" @click="sendInterviewNotification">发送通知</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="delete-confirm-modal" @click.self="showDeleteConfirm = false">
      <div class="delete-confirm-content">
        <div class="delete-confirm-header">
          <i class="fas fa-exclamation-circle"></i>
          <h3>删除确认</h3>
        </div>
        <div class="delete-confirm-body">
          <p>确定要删除该面试地址模板吗？</p>
          <p class="delete-template-name">{{ templateToDelete?.address }}</p>
        </div>
        <div class="delete-confirm-footer">
          <button class="cancel-btn" @click="showDeleteConfirm = false">取消</button>
          <button class="confirm-btn" @click="deleteTemplate">确认删除</button>
        </div>
      </div>
    </div>

    <!-- 面试邀请弹窗 -->
    <div v-if="showInterviewInvite" class="interview-invite-wrapper" @click.self="closeInterviewInvite">
      <div class="interview-invite-container">
        <div class="interview-invite-header">
          <h3>面试邀请</h3>
          <button class="invite-close-btn" @click="closeInterviewInvite">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <!-- 地址选择区域 -->
        <div class="invite-address-selection" @click="openAddressSelect">
          <div class="invite-address-info">
            <i class="fas fa-map-marker-alt"></i>
            <span>{{ selectedAddress ? selectedAddress.address : '请选择面试地点' }}</span>
          </div>
          <i class="fas fa-chevron-right"></i>
        </div>
        
        <!-- 面试邀请函 -->
        <div class="invite-letter">
          <h4>面试邀请函</h4>
          <div class="invite-letter-content">
            <p>尊敬的 <span class="invite-highlight">{{ currentCandidate ? currentCandidate.studentName : '应聘者' }}</span> 同学：</p>
            <p>  感谢您对我公司的关注与支持！经过初步筛选，我们诚挚地邀请您参加面试。</p>
            <div class="invite-details-center">
              <p class="invite-detail-item"><span class="detail-line"></span>面试岗位：<span class="invite-highlight">{{ currentCandidate ? currentCandidate.jobTitle : '职位名称' }}</span></p>
              <p class="invite-detail-item"><span class="detail-line"></span>面试地点：<span class="invite-highlight">{{ selectedAddress ? selectedAddress.address : '待定' }}</span></p>
              <p class="invite-detail-item"><span class="detail-line"></span>面试时间：<span class="invite-highlight">{{ selectedDateTime ? formatDateTime(selectedDateTime) : '待定' }}</span></p>
              <p class="invite-detail-item"><span class="detail-line"></span>面试官：<span class="invite-highlight">{{ selectedAddress ? selectedAddress.interviewerName : '待定' }}</span></p>
              <p class="invite-detail-item"><span class="detail-line"></span>部门：<span class="invite-highlight">{{ selectedAddress ? selectedAddress.interviewerDepartment : '待定' }}</span></p>
            </div>
            <br>
            <p>  请您在收到邀请后 <span class="invite-highlight">{{ inviteExpireDays }}</span> 天内确认是否参加面试。</p>
            <p>  期待与您的见面！</p>
          </div>
        </div>
        
        <!-- 底部按钮 -->
        <div class="interview-invite-actions">
          <button class="invite-confirm-btn" @click="confirmInterview">确认邀请</button>
        </div>
      </div>
    </div>

    <!-- 地址选择弹窗 -->
    <div v-if="showAddressSelect" class="address-select-wrapper" @click.self="closeAddressSelect">
      <div class="address-select-container">
        <div class="address-select-header">
          <h3>选择面试地点</h3>
          <button class="address-close-btn" @click="closeAddressSelect">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="address-select-list">
          <div 
            v-for="template in interviewTemplates" 
            :key="template.id"
            class="address-select-item"
            :class="{'address-selected': selectedAddress && selectedAddress.id === template.id}"
            @click="selectAddress(template)"
          >
            <div class="address-select-content">
              <div class="address-select-name">{{ template.address }}</div>
              <div class="address-select-detail">
                <span>面试官: {{ template.interviewerName }}</span>
                <span>部门: {{ template.interviewerDepartment || '未设置' }}</span>
              </div>
            </div>
            <div class="address-select-check" v-if="selectedAddress && selectedAddress.id === template.id">
              <i class="fas fa-check"></i>
            </div>
          </div>
        </div>
        
        <div class="address-select-actions">
          <button class="address-cancel-btn" @click="closeAddressSelect">取消</button>
          <button class="address-confirm-btn" @click="confirmAddressSelect">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

export default {
  name: 'CompanyResumeManagement',
  data() {
    return {
      resumes: [],
      jobs: [],
      selectedJobId: null,
      searchKeyword: '',
      startDate: '',
      endDate: '',
      loading: false,
      showJobFilter: false,
      selectedJobTitle: '',
      showResumeModal: false,
      selectedResume: {},
      // 面试模板相关
      showTemplateModal: false,
      interviewTemplates: [],
      showTemplateForm: false,
      isEditingTemplate: false,
      templateForm: {
        templateName: '',
        address: '',
        interviewerName: '',
        interviewerDepartment: '',
        interviewerEmail: '',
        isDefault: false,
        interviewTime: null,
        expireDays: 3, // 默认3天
      },
      
      // 通知面试相关
      showInterviewModal: false,
      interviewForm: {
        templateId: null,
        interviewTime: '',
        address: '',
        interviewerName: '',
        notes: ''
      },
      // 添加管理模式状态
      isManageMode: false,
      // 添加删除确认对话框状态
      showDeleteConfirm: false,
      // 要删除的模板
      templateToDelete: null,
      companyId: null, // 添加companyId字段
      showInterviewInvite: false,
      selectedAddress: null,
      inviteExpireDays: 3,
      currentCandidate: null,
      showAddressSelect: false,
      selectedDateTime: null,
      isSubmitting: false,
      newTemplate: {
        templateName: '',
        address: '',
        interviewerName: '',
        interviewerDepartment: '',
        interviewTime: null,
        expireDays: 3, // 默认3天
      },
    }
  },
  created() {
    this.companyId = parseInt(localStorage.getItem('companyId'), 10);
    this.fetchJobs();
    this.fetchResumes();
    this.fetchInterviewTemplates();
  },
  methods: {
    async fetchJobs() {
      try {
        const companyId = localStorage.getItem('companyId'); // 从登录信息中获取
        const response = await axios.get(`http://localhost:8888/api/company/resumes/jobs?companyId=${companyId}`);
        this.jobs = response.data;
      } catch (error) {
        console.error('获取岗位列表失败:', error);
      }
    },
    async fetchResumes() {
      try {
        // 从本地存储获取公司ID
        const companyId = localStorage.getItem('companyId');
        if (!companyId) {
          console.error('未找到公司ID');
          return;
        }
        
        const params = {
          companyId: companyId,
          startDate: this.startDate,
          endDate: this.endDate,
          keyword: this.searchKeyword,
          jobId: this.selectedJobId
        };
        
        const response = await axios.get('http://localhost:8888/api/company/resumes', { params });
        this.resumes = response.data;
      } catch (error) {
        console.error('获取简历列表失败:', error);
      }
    },
    handleSearch() {
      this.fetchResumes();
    },
    handleJobSelect(jobId) {
      this.selectedJobId = jobId;
      
      if (jobId === null) {
        this.selectedJobTitle = '';
      } else {
        const selectedJob = this.jobs.find(job => job.jobId === jobId);
        if (selectedJob) {
          this.selectedJobTitle = selectedJob.jobTitle;
        }
      }
      
      this.fetchResumes();
    },
    // 处理点击其他区域关闭下拉菜单
    mounted() {
      document.addEventListener('click', () => {
        this.showJobFilter = false
      })
    },
    beforeUnmount() {
      document.removeEventListener('click', () => {
        this.showJobFilter = false
      })
    },
    async showResumeDetail(resume) {
      try {
        // 从申请表中获取学生ID
        const studentId = resume.studentId;
        
        if (studentId) {
          // 使用现有的简历接口获取完整简历
          const response = await axios.get(`http://localhost:8888/api/resume/${studentId}`);
          
          if (response.data && response.data.id) {
            // 合并简历数据和申请表数据
            this.selectedResume = {
              ...resume,
              ...response.data,
              schoolName: resume.schoolName || response.data.schoolName, // 保留申请表中的学校名称
              major: resume.major || response.data.major // 保留申请表中的专业信息
            };
          } else {
            this.selectedResume = resume;
          }
        } else {
          this.selectedResume = resume;
        }
        
        this.showResumeModal = true;
      } catch (error) {
        console.error('获取简历详情失败:', error);
        // 如果获取失败，仍然显示已有的基本信息
        this.selectedResume = resume;
        this.showResumeModal = true;
      }
    },
    closeResumeModal() {
      this.showResumeModal = false;
    },
    // 添加Markdown渲染方法
    renderMarkdown(text) {
      if (!text) return '';
      const rawHtml = marked(text);
      return DOMPurify.sanitize(rawHtml);
    },
    // 获取面试模板列表
    async fetchInterviewTemplates() {
      try {
        const response = await this.$axios.get('http://localhost:8888/api/interview-templates', {
          params: { companyId: this.companyId }
        });
        this.interviewTemplates = response.data;
        
        // 打印日志以检查数据
        console.log('获取到的模板数据:', this.interviewTemplates);
      } catch (error) {
        console.error('获取面试模板失败:', error);
        this.$message.error('获取面试地址失败');
      }
    },
    
    // 显示添加模板表单
    showAddTemplate() {
      this.isEditingTemplate = false;
      this.templateForm = {
        templateName: '',
        address: '',
        interviewerName: '',
        interviewerDepartment: '',
        interviewerEmail: '',
        isDefault: false,
        interviewTime: null,
        expireDays: 3
      };
      this.showTemplateForm = true;
    },
    
    // 显示编辑模板表单
    editTemplate(template) {
      this.isEditingTemplate = true;
      this.templateForm = {
        ...template,
        interviewTime: template.interviewTime || null,
        expireDays: template.expireDays || 3
      };
      this.showTemplateForm = true;
    },
    
    // 取消表单
    cancelTemplateForm() {
      this.showTemplateForm = false;
    },
    
    // 保存模板
    async saveTemplate() {
      try {
        // 表单验证
        if (!this.templateForm.templateName) {
          this.$message.error('请输入模板名称');
          return;
        }
        if (!this.templateForm.address) {
          this.$message.error('请输入面试地点');
          return;
        }
        if (!this.templateForm.interviewerName) {
          this.$message.error('请输入面试官姓名');
          return;
        }
        
        // 确保companyId存在且为数字
        if (!this.companyId) {
          this.companyId = parseInt(localStorage.getItem('companyId'), 10);
        }
        
        if (!this.companyId) {
          this.$message.error('无法获取公司ID');
          return;
        }

        const templateData = {
          companyId: this.companyId,
          templateName: this.templateForm.templateName,
          address: this.templateForm.address,
          interviewerName: this.templateForm.interviewerName,
          interviewerDepartment: this.templateForm.interviewerDepartment || '',
          interviewerEmail: this.templateForm.interviewerEmail || '',
          isDefault: this.templateForm.isDefault === true,
          // 添加新字段
          interviewTime: this.templateForm.interviewTime,
          expireDays: this.templateForm.expireDays || 3
        };

        console.log('发送的模板数据:', templateData);
        
        if (this.isEditingTemplate) {
          templateData.id = this.templateForm.id;
          await this.$axios.put(`http://localhost:8888/api/interview-templates/${templateData.id}`, templateData);
          this.$message.success('面试地址更新成功');
        } else {
          const response = await this.$axios.post('http://localhost:8888/api/interview-templates', templateData);
          console.log('保存成功，响应数据:', response.data);
          this.$message.success('面试地址添加成功');
        }
        
        this.fetchInterviewTemplates();
        this.showTemplateForm = false;
      } catch (error) {
        console.error('保存面试模板失败:', error);
        if (error.response) {
          console.error('错误响应数据:', error.response.data);
          console.error('错误状态码:', error.response.status);
        }
        this.$message.error(`保存失败: ${error.response?.data || '请稍后重试'}`);
      }
    },
    
    // 设置默认模板
    async setDefaultTemplate(templateId) {
      try {
        const companyId = parseInt(localStorage.getItem('companyId'), 10);
        
        // 设置为默认 - 后端会自动处理将其他模板设为非默认
        await this.$axios.put(`http://localhost:8888/api/interview-templates/${templateId}/default`, { companyId });
        this.$message.success('默认面试地址设置成功');
        
        // 刷新模板列表
        this.fetchInterviewTemplates();
      } catch (error) {
        console.error('设置默认模板失败:', error);
        this.$message.error('设置失败，请稍后重试');
      }
    },
    
    // 删除模板
    async deleteTemplate() {
      try {
        if (!this.templateToDelete) return;
        
        await this.$axios.delete(`http://localhost:8888/api/interview-templates/${this.templateToDelete.id}`);
        this.$message.success('面试地址删除成功');
        
        this.showDeleteConfirm = false;
        this.templateToDelete = null;
        this.fetchInterviewTemplates();
      } catch (error) {
        console.error('删除模板失败:', error);
        if (error.response && error.response.data && error.response.data.message) {
          this.$message.error(`删除失败: ${error.response.data.message}`);
        } else {
          this.$message.error('删除失败，请稍后重试');
        }
      }
    },
    
    // 通知面试相关方法
    showInterviewNotification(resume) {
      this.selectedResume = resume;
      
      // 重置面试表单
      this.interviewForm = {
        templateId: null,
        interviewTime: this.formatDateForInput(new Date()),
        address: '',
        interviewerName: '',
        notes: ''
      };
      
      // 如果有默认模板，使用默认模板
      const defaultTemplate = this.interviewTemplates.find(t => t.isDefault);
      if (defaultTemplate) {
        this.interviewForm.templateId = defaultTemplate.id;
        this.updateInterviewFormFromTemplate(defaultTemplate);
      }
      
      this.showInterviewModal = true;
    },
    
    updateInterviewFormFromTemplate(template) {
      this.interviewForm.address = template.address;
      this.interviewForm.interviewerName = template.interviewerName;
    },
    
    async sendInterviewNotification() {
      try {
        const notificationData = {
          applicationId: this.selectedResume.applicationId,
          interviewTime: this.interviewForm.interviewTime,
          address: this.interviewForm.address,
          interviewerName: this.interviewForm.interviewerName,
          notes: this.interviewForm.notes
        };
        
        await axios.post(`http://localhost:8888/api/interview/notify`, notificationData);
        this.showInterviewModal = false;
        
        // 刷新简历列表
        this.fetchResumes();
      } catch (error) {
        console.error('发送面试通知失败:', error);
      }
    },
    
    formatDateForInput(date) {
      try {
        const d = date instanceof Date ? date : new Date(date);
        if (isNaN(d.getTime())) {
          // 如果日期无效，返回当前时间
          return new Date().toISOString().slice(0, 16);
        }
        d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
        return d.toISOString().slice(0, 16);
      } catch (e) {
        console.error('日期格式化错误:', e);
        return new Date().toISOString().slice(0, 16);
      }
    },
    // 切换管理模式
    toggleManageMode() {
      this.isManageMode = !this.isManageMode;
    },
    
    // 确认删除模板
    confirmDeleteTemplate(template) {
      this.templateToDelete = template;
      this.showDeleteConfirm = true;
    },
    openAddressSelect() {
      this.showAddressSelect = true;
    },
    closeAddressSelect() {
      this.showAddressSelect = false;
    },
    // 修改选择地址的方法
    async selectAddress(template) {
      try {
        this.selectedAddress = template;
        // 从模板中获取默认面试时间
        if (template.interviewTime) {
          this.selectedDateTime = this.formatDateTime(template.interviewTime);
        } else {
          // 如果模板没有默认时间，设置为当前时间
          this.selectedDateTime = this.formatDateTime(new Date());
        }
        // 从模板中获取时效天数，同时更新inviteExpireDays
        this.inviteExpireDays = template.expireDays || 3;
        this.showAddressSelect = false;

        console.log('选中的模板数据:', {
          template,
          interviewTime: template.interviewTime,
          expireDays: template.expireDays,
          formattedDateTime: this.selectedDateTime
        });
      } catch (error) {
        console.error('选择地址时出错:', error);
      }
    },
    confirmAddressSelect() {
      // 处理地址选择后的逻辑
      this.showAddressSelect = false;
    },
    openInterviewInvite(resume) {
      this.currentCandidate = resume;
      
      // 获取默认模板
      if (this.interviewTemplates && this.interviewTemplates.length > 0) {
        const defaultTemplate = this.interviewTemplates.find(t => t.isDefault);
        if (defaultTemplate) {
          // 直接调用selectAddress来设置所有相关信息
          this.selectAddress(defaultTemplate);
        }
      }
      
      this.showInterviewInvite = true;
    },
    closeInterviewInvite() {
      this.showInterviewInvite = false;
      this.currentCandidate = null;
    },
    // 确认面试邀请
    async confirmInterview() {
      if (!this.selectedDateTime) {
        this.$message.error('请选择面试时间');
        return;
      }
      
      if (!this.selectedAddress) {
        this.$message.error('请选择面试地点');
        return;
      }
      
      try {
        this.isSubmitting = true;
        
        const response = await fetch('/api/interviews', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            applicationId: this.currentCandidate.applicationId,
            location: this.selectedAddress.address,
            interviewer: this.selectedAddress.interviewerName,
            department: this.selectedAddress.interviewerDepartment,
            interviewTime: this.selectedDateTime,
            expireDays: this.inviteExpireDays
          })
        });
        
        const result = await response.json();
        
        if (response.ok) {
          this.$message.success('面试邀请已发送');
          this.closeInterviewInvite();
          this.fetchResumes(); // 刷新简历列表
        } else {
          this.$message.error(result.message || '操作失败');
        }
      } catch (error) {
        console.error('发送面试邀请失败:', error);
        this.$message.error('发送面试邀请失败');
      } finally {
        this.isSubmitting = false;
      }
    },
    
    // 拒绝申请
    async rejectApplication(resume) {
      try {
        const confirmed = await this.$confirm('确定要拒绝该申请吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          customClass: 'custom-confirm-box',
          distinguishCancelAndClose: true,
          showClose: false
        });
        
        if (!confirmed) return;
        
        const response = await fetch(`/api/applications/${resume.applicationId}/reject`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          }
        });
        
        const result = await response.json();
        
        if (response.ok) {
          this.$message.success('已拒绝该申请');
          this.closeResumeModal();
          this.fetchResumes(); // 刷新简历列表
        } else {
          this.$message.error(result.message || '操作失败');
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('拒绝申请失败:', error);
          this.$message.error('拒绝申请失败');
        }
      }
    },
    // 添加格式化日期时间的方法
    formatDateTime(date) {
      try {
        const d = date instanceof Date ? date : new Date(date);
        if (isNaN(d.getTime())) {
          return this.formatDateTime(new Date());
        }
        return d.toISOString().slice(0, 16);
      } catch (error) {
        console.error('日期格式化错误:', error);
        return new Date().toISOString().slice(0, 16);
      }
    },
    // 重置表单时也重置新增的字段
    resetTemplateForm() {
      this.newTemplate = {
        templateName: '',
        address: '',
        interviewerName: '',
        interviewerDepartment: '',
        interviewTime: null,
        expireDays: 3,
      };
      this.isDefault = false;
    },
    // 修改生成邀请函内容的方法
    generateInvitationContent() {
      const dateStr = this.selectedDateTime ? 
        new Date(this.selectedDateTime).toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          hour12: false
        }) : '待定';

      return `尊敬的 ${this.currentCandidate.studentName} 同学：

感谢您对我公司的关注与支持！经过初步筛选，我们诚挚地邀请您参加面试。

面试岗位：${this.currentCandidate.jobTitle}

面试地点：${this.selectedAddress.address}

面试时间：${dateStr}

面试官：${this.selectedAddress.interviewerName}

部门：${this.selectedAddress.interviewerDepartment || ''}


请您在收到邀请后 ${this.inviteExpireDays} 天内确认是否参加面试。`;
    }
  }
}
</script>

<style scoped>
.resume-management {
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
  position: relative;
}

.title-container {
  display: flex;
  align-items: center;
  gap: 15px; /* 标题和图标之间的间距 */
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

.content-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: grid;
  grid-template-columns: 40px 1fr 1.5fr 1.5fr 1.5fr 1fr 1fr 80px;
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

.resume-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.resume-item {
  display: grid;
  grid-template-columns: 40px 1fr 1.5fr 1.5fr 1.5fr 1fr 1fr 80px;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 16px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.item-cell {
  font-size: 14px;
  color: #333;
}

.interview-btn {
  background-color: #CA4499;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.interview-btn:hover {
  background-color: #a156d3;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.2);
}

.position-filter {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
}

.job-filter-dropdown {
  cursor: pointer;
  padding: 2px 4px;
  border-radius: 4px;
}

.job-filter-dropdown:hover {
  background-color: rgba(202, 68, 153, 0.1);
}

.job-filter-dropdown i {
  color: #666;
  font-size: 14px;
  transition: color 0.2s;
}

.job-filter-dropdown:hover i {
  color: #CA4499;
}

.job-filter-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 200px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  margin-top: 8px;
  padding: 8px 0;
}

.job-filter-item {
  padding: 8px 16px;
  color: #333;
  transition: all 0.2s;
  font-size: 14px;
  cursor: pointer;
}

.job-filter-item:hover {
  background-color: #f6f6f6;
}

.job-filter-item.active {
  color: #CA4499;
  background-color: rgba(202, 68, 153, 0.1);
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.position-filter span {
  color: #CA4499;
  font-weight: 500;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-pending {
  background-color: #FFF7E6;
  color: #FAAD14;
}

.status-processed {
  background-color: #E6F7FF;
  color: #1890FF;
}

/* 添加悬停效果 */
.resume-item.hover-effect {
  cursor: pointer;
  transition: all 0.3s ease;
}

.resume-item.hover-effect:hover {
  background-color: #f9f0f6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 简历详情弹窗样式 */
.company-resume-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding-left: 100px;
}

.company-resume-view {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 0;
  width: 90%;
  max-width: 900px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.company-close-btn {
  display: none; /* 隐藏关闭按钮 */
}

.company-resume-basic-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  gap: 24px;
  background-color: transparent;
  padding: 0;
  box-shadow: none;
  border-radius: 0;
}

.company-info-left {
  flex: 1;
  background-color: #f3f0f0;
  padding: 15px 20px;
  border-radius: 6px;
  width: 700px; 
  min-width: 700px; 
}

.company-info-left h2 {
  margin-top: 0;
  margin-bottom: 12px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(82, 80, 80, 0.1);
}

.company-info-left h2 span {
  font-size: 16px;
  color: #666;
  margin-left: 10px;
  font-weight: normal;
}

.company-certificates p {
  margin: 5px 0;
  color: #666;
}

.company-info-right {
  flex: 1;
  text-align: right;
}

.company-photo {
  width: 120px;
  height: 160px;
  overflow: hidden;
  border: 1px solid #eee;
  display: inline-block;
}

.company-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.company-photo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
  color: #ddd;
}

.company-photo-placeholder i {
  font-size: 48px;
}

.company-resume-section {
  margin-bottom: 25px;
}

.company-section-header {
  margin-bottom: 15px;
}

.company-section-header h3 {
  font-size: 18px;
  color: #CA4499;
  display: flex;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.company-section-header h3 i {
  margin-right: 10px;
}

.company-section-content {
  color: #333;
  line-height: 1.6;
}

.company-education-item {
  margin-bottom: 15px;
}

.company-school-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.company-school-info h3 {
  font-size: 16px;
  margin: 0;
  color: #333;
}

.company-major-info p {
  margin: 5px 0;
  color: #666;
}

.company-skills-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.company-experience-content {
  white-space: pre-line;
}

.company-markdown-content h1,
.company-markdown-content h2,
.company-markdown-content h3,
.company-markdown-content h4,
.company-markdown-content h5,
.company-markdown-content h6 {
  margin-top: 16px;
  margin-bottom: 8px;
  color: #333;
}

.company-markdown-content p {
  margin-bottom: 16px;
}

.company-markdown-content ul {
  padding-left: 20px;
}

.company-resume-actions {
  display: none;
}

.company-resume-actions-outside {
  position: absolute;
  top: 50%;
  right: calc(5% - 10px);
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 30px;
  z-index: 1010;
}

.company-interview-btn {
  background-color: #CA4499;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 120px;
  text-align: center;
  transition: all 0.3s;
}

.company-interview-btn:hover {
  background-color: #d65ba9;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(202, 68, 153, 0.3);
}

.company-reject-btn {
  background-color: #0c1e7797;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 120px;
  text-align: center;
  transition: all 0.3s;
}

.company-reject-btn:hover {
  background-color: #e351df;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

/* 面试地址图标 */
.address-icon {
  font-size: 20px;
  color: #CA4499;
  cursor: pointer;
  transition: all 0.3s;
}

.address-icon:hover {
  color: #d65ba9;
  transform: scale(1.1);
}

/* 面试地址弹窗 */
.address-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.address-modal-content {
  background-color: white;
  border-radius: 12px;
  width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.address-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.header-left h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.manage-btn, .add-address-btn, .close-btn {
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #333;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.2s;
}

.manage-btn i, .add-address-btn i {
  margin-right: 6px;
  font-size: 18px;
}

.manage-btn {
  color: #CA4499;
}

.add-address-btn {
  color: #CA4499;
  
  border-radius: 20px;
  padding: 8px 16px;
}

.close-btn {
  font-size: 16px;
  padding: 6px;
}

.manage-btn:hover {
  background-color: rgba(202, 68, 153, 0.1);
}

.add-address-btn:hover {
  background-color: rgba(202, 68, 153, 0.2);
}

.close-btn:hover {
  background-color: #f5f5f5;
}

.address-list {
  padding: 10px 20px 20px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.address-info {
  flex: 1;
}

.address-main {
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}

.default-tag {
  background-color: #CA4499;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  margin-left: 8px;
}

.address-detail {
  color: #999;
  font-size: 14px;
}

.address-actions {
  display: flex;
  align-items: center;
}

.edit-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
  font-size: 16px;
  padding: 5px;
  transition: all 0.2s;
}

.edit-btn:hover {
  color: #CA4499;
}

/* 地址表单 */
.address-form-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1010;
}

.address-form {
  background-color: white;
  border-radius: 16px;
  width: 500px;
  max-width: 90vw;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.form-header h4 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 500;
}

.close-form-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #999;
  transition: all 0.2s;
}

.close-form-btn:hover {
  color: #666;
}

.form-content {
  padding: 20px 24px;
}

.form-section {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #f0f0f0;
}

.form-section h5 {
  margin: 0 0 15px 0;
  color: #CA4499;
  font-size: 15px;
  font-weight: 500;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group.half {
  flex: 1;
  min-width: 0; /* 防止flex子项溢出 */
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

.form-group input[type="text"],
.form-group input[type="email"] {
  width: 100%;
  padding: 10px 12px;
  width: 420px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
  background-color: #f9f9f9;
}

.form-group input[type="text"]:focus,
.form-group input[type="email"]:focus {
  border-color: #CA4499;
  outline: none;
  box-shadow: 0 0 0 3px rgba(202, 68, 153, 0.1);
  background-color: white;
}

.checkbox-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 5px;
  justify-content: center;
}

.checkbox-group input[type="checkbox"] {
  margin: 0;
  width: 16px;
  height: 16px;
  accent-color: #CA4499;
  cursor: pointer;
}

.checkbox-group label {
  margin: 0;
  cursor: pointer;
  font-weight: normal;
  color: #333;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 15px 24px;
  border-top: 1px solid #f0f0f0;
  background-color: #fafafa;
  border-radius: 0 0 16px 16px;
}

.cancel-btn, .save-btn {
  padding: 8px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.cancel-btn {
  background-color: white;
  border: 1px solid #e0e0e0;
  color: #666;
}

.save-btn {
  background-color: #CA4499;
  border: none;
  color: white;
  box-shadow: 0 2px 8px rgba(202, 68, 153, 0.3);
}

.cancel-btn:hover {
  background-color: #f5f5f5;
}

.save-btn:hover {
  background-color: #d65ba9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(202, 68, 153, 0.4);
}

.save-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(202, 68, 153, 0.3);
}

/* 通知面试弹窗 */
.interview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.interview-modal-content {
  background-color: white;
  border-radius: 8px;
  width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

.interview-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.interview-modal-header h3 {
  margin: 0;
  color: #333;
}

.interview-close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.interview-form {
  padding: 20px;
}

.student-info {
  background-color: #f9f9f9;
  padding: 12px 15px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.student-info p {
  margin: 5px 0;
  color: #666;
}

.student-info strong {
  color: #333;
}

.form-group.half input[type="text"] {
  width: 180px;
  padding: 10px 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
  background-color: #f9f9f9;
}

/* 自定义复选框样式 */
.default-checkbox {
  display: inline-flex;
  align-items: center;
  margin-right: 15px;
}

.custom-checkbox {
  display: flex;
  align-items: center;
  position: relative;
  cursor: pointer;
  user-select: none;
}

.custom-checkbox input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: relative;
  height: 18px;
  width: 18px;
  background-color: #fff;
  border: 2px solid #dcdfe6;
  border-radius: 50%;
  margin-right: 6px;
  transition: all 0.3s;
}

.custom-checkbox:hover input ~ .checkmark {
  border-color: #CA4499;
}

.custom-checkbox input:checked ~ .checkmark {
  background-color: #CA4499;
  border-color: #CA4499;
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
  left: 5px;
  top: 2px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.custom-checkbox input:checked ~ .checkmark:after {
  display: block;
}

.custom-checkbox input:disabled ~ .checkmark {
  background-color: #CA4499;
  border-color: #e4e7ed;
  cursor: not-allowed;
}

.checkbox-label {
  color: #909399;
  font-size: 14px;
}

/* 删除按钮样式 */
.delete-btn {
  background: transparent;
  border: none;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.delete-btn:hover {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.delete-btn i {
  font-size: 16px;
}

/* 删除确认弹窗样式 */
.delete-confirm-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.delete-confirm-content {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 400px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.delete-confirm-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.delete-confirm-header i {
  color: #ff4d4f;
  font-size: 24px;
  margin-right: 12px;
}

.delete-confirm-header h3 {
  color: #303133;
  margin: 0;
  font-size: 18px;
}

.delete-confirm-body {
  margin-bottom: 24px;
}

.delete-confirm-body p {
  margin: 0;
  color: #606266;
}

.delete-template-name {
  color: #303133;
  font-weight: 500;
  margin-top: 8px !important;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.delete-confirm-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.delete-confirm-footer button {
  padding: 8px 20px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.cancel-btn {
  background-color: #f5f7fa;
  color: #606266;
}

.cancel-btn:hover {
  background-color: #e4e7ed;
}

.confirm-btn {
  background-color: #ff4d4f;
  color: white;
}

.confirm-btn:hover {
  background-color: #ff7875;
}

/* 面试邀请弹窗样式 - Apple风格 */
.interview-invite-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.interview-invite-container {
  width: 600px;
  max-width: 90%;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  overflow-y: auto;
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

.interview-invite-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.interview-invite-header h3 {
  margin: 0;
  color: #1d1d1f;
  font-size: 22px;
  font-weight: 500;
  letter-spacing: -0.5px;
}

.invite-close-btn {
  background: transparent;
  border: none;
  font-size: 20px;
  color: #86868b;
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.invite-close-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #1d1d1f;
}

/* 地址选择区域 */
.invite-address-selection {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 28px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.2s;
}

.invite-address-selection:hover {
  background-color: rgba(0, 0, 0, 0.02);
}

.invite-address-info {
  display: flex;
  align-items: center;
}

.invite-address-info i {
  color: #CA4499;
  margin-right: 14px;
  font-size: 20px;
}

.invite-address-info span {
  color: #1d1d1f;
  font-size: 17px;
  font-weight: 400;
}

.invite-address-selection .fa-chevron-right {
  color: #86868b;
  font-size: 14px;
}

/* 面试邀请函 */
.invite-letter {
  padding: 20px 28px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.invite-letter h4 {
  margin: 0 0 16px 0;
  color: #1d1d1f;
  font-size: 17px;
  font-weight: 500;
}

.invite-letter-content {
  background-color: rgba(0, 0, 0, 0.02);
  border-radius: 12px;
  padding: 20px;
  line-height: 1.5;
}

.invite-letter-content p {
  margin: 10px 0;
  color: #1d1d1f;
  font-size: 15px;
  line-height: 1.6;
}

.invite-highlight {
  color: #CA4499;
  font-weight: 500;
}

/* 底部按钮 */
.interview-invite-actions {
  padding: 20px 28px;
  display: flex;
  justify-content: center;
}

.invite-confirm-btn {
  padding: 12px 40px;
  background: #CA4499;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.invite-confirm-btn:hover {
  background: #d65ba7;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(202, 68, 153, 0.3);
}

.invite-confirm-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(202, 68, 153, 0.2);
}

/* 地址选择弹窗样式 */
.address-select-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2100;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.address-select-container {
  width: 600px;
  max-width: 90%;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  overflow-y: auto;
  animation: modal-appear 0.3s ease-out;
}

.address-select-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.address-select-header h3 {
  margin: 0;
  color: #1d1d1f;
  font-size: 22px;
  font-weight: 500;
  letter-spacing: -0.5px;
}

.address-close-btn {
  background: transparent;
  border: none;
  font-size: 20px;
  color: #86868b;
  cursor: pointer;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.address-close-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #1d1d1f;
}

.address-select-list {
  padding: 10px 20px;
  max-height: 400px;
  overflow-y: auto;
}

.address-select-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 12px;
  margin: 6px 0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.address-select-item:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

.address-selected {
  background-color: rgba(202, 68, 153, 0.08);
}

.address-select-content {
  display: flex;
  flex-direction: column;
}

.address-select-name {
  font-size: 17px;
  color: #1d1d1f;
  margin-bottom: 6px;
  font-weight: 500;
}

.address-selected .address-select-name {
  color: #CA4499;
}

.address-select-detail {
  font-size: 14px;
  color: #86868b;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.address-select-check {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #CA4499;
  color: white;
}

.address-select-check i {
  font-size: 14px;
}

.address-select-actions {
  display: flex;
  justify-content: flex-end;
  padding: 20px 28px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  gap: 16px;
}

.address-cancel-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 22px;
  background-color: rgba(0, 0, 0, 0.05);
  color: #1d1d1f;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.address-cancel-btn:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

.address-confirm-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 22px;
  background-color: #CA4499;
  color: white;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(202, 68, 153, 0.2);
}

.address-confirm-btn:hover {
  background-color: #b53c88;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(202, 68, 153, 0.3);
}

.address-confirm-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(202, 68, 153, 0.2);
}

/* 添加新的样式 */
.invite-details-center {
  width: 80%;
  margin: 20px auto;
  text-align: left;
}

.invite-detail-item {
  position: relative;
  padding-left: 15px;
  margin: 10px 0;
}

.detail-line {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background-color: #CA4499;
  border-radius: 2px;
}

/* 添加新的样式 */
.address-time-selection,
.address-expire-selection {
  padding: 20px 28px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.address-time-selection h4,
.address-expire-selection h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #1d1d1f;
  font-weight: 500;
}

.time-picker-container {
  display: flex;
  align-items: center;
}

.time-picker-input {
  width: 100%;
  height: 40px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 0 12px;
  font-size: 15px;
  color: #1d1d1f;
  transition: all 0.2s;
}

.time-picker-input:focus {
  outline: none;
  border-color: #CA4499;
  box-shadow: 0 0 0 3px rgba(202, 68, 153, 0.1);
}

.expire-days-container {
  display: flex;
  align-items: center;
}

.expire-days-input {
  width: 80px;
  height: 40px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 0 12px;
  font-size: 15px;
  color: #1d1d1f;
  text-align: center;
  transition: all 0.2s;
}

.expire-days-input:focus {
  outline: none;
  border-color: #CA4499;
  box-shadow: 0 0 0 3px rgba(202, 68, 153, 0.1);
}

.expire-days-unit {
  margin-left: 8px;
  font-size: 15px;
  color: #1d1d1f;
}

/* 添加新的样式，保持与原有样式一致 */
.time-input {
  width: 100%;
  height: 40px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  width: 120px;
  color: #606266;
  transition: border-color 0.2s;
}

.time-input:focus {
  outline: none;
  border-color: #CA4499;
}

.expire-days-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.expire-days-input {
  width: 170px;
  height: 40px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  transition: border-color 0.2s;
}

.expire-days-input:focus {
  outline: none;
  border-color: #CA4499;
}

.expire-days-unit {
  color: #606266;
  font-size: 14px;
}

/* 更新样式以匹配现有的表单样式 */
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.form-group.half {
  flex: 1;
}

.expire-days-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.expire-days-wrapper input {
  width: 100%;
  width: 170px;
  padding-right: 10px; /* 为"天"字留出空间 */
  background-color: #f9f9f9;
}

.expire-days-unit {
  position: absolute;
  right: 10px;
  color: #606266;
  font-size: 14px;
  pointer-events: none;
}

/* 移除之前的独立样式 */
.expire-days-input {
  width: 100% !important;
}

/* 更新样式以匹配现有的表单样式 */
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: -5px;
}

.form-group.half {
  flex: 1;
}

.form-input {
  width: 100%;
  height: 20px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
  width: 180px;
  background-color: #f9f9f9;
  transition: all 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #CA4499;
  background-color: #fff;
}

.form-input:hover {
  border-color: #c0c4cc;
}

.expire-days-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.short-input {
  width: 160px !important;
}

.expire-days-unit {
  color: #606266;
  font-size: 14px;
}

/* 移除之前的样式 */
.expire-days-input,
.time-input {
  display: none;
}



</style> 

<style>
/* Element Plus 确认框样式覆盖 */
.el-message-box {
  border-radius: 16px !important;
  padding: 24px !important;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2) !important;
  max-width: 360px !important;
  border: none !important;
}

.el-message-box__header {
  padding: 0 !important;
  margin-bottom: 20px !important;
}

.el-message-box__title {
  font-size: 20px !important;
  font-weight: 500 !important;
  color: #1d1d1f !important;
}

.el-message-box__content {
  padding: 0 !important;
  margin-bottom: 24px !important;
  color: #1d1d1f !important;
  font-size: 15px !important;
  line-height: 1.5 !important;
}

.el-message-box__btns {
  display: flex !important;
  justify-content: flex-end !important;
  gap: 12px !important;
  padding: 0 !important;
}

.el-message-box__btns .el-button--default {
  background: rgba(0, 0, 0, 0.05) !important;
  border: none !important;
  padding: 10px 20px !important;
  border-radius: 22px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  color: #1d1d1f !important;
  transition: all 0.2s !important;
}

.el-message-box__btns .el-button--default:hover {
  background: rgba(0, 0, 0, 0.1) !important;
}

.el-message-box__btns .el-button--primary {
  background: #CA4499 !important;
  border: none !important;
  padding: 10px 24px !important;
  border-radius: 22px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  color: white !important;
  transition: all 0.3s !important;
  box-shadow: 0 4px 12px rgba(202, 68, 153, 0.2) !important;
}

.el-message-box__btns .el-button--primary:hover {
  background: #b53c88 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 16px rgba(202, 68, 153, 0.3) !important;
}

.el-message-box__btns .el-button--primary:active {
  transform: translateY(0) !important;
  box-shadow: 0 2px 8px rgba(202, 68, 153, 0.2) !important;
}

/* 遮罩层样式 */
.el-overlay {
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}
</style>