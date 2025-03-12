<template>
  <div class="application-record">
    <div class="header">
      <h2>申请记录</h2>
      <div class="search-bar">
        <div class="date-range">
          <input 
            type="date" 
            v-model="startDate" 
            placeholder="开始日期"
            :max="endDate || undefined"
          >
          <span>至</span>
          <input 
            type="date" 
            v-model="endDate" 
            placeholder="结束日期"
            :min="startDate || undefined"
          >
        </div>
        <div class="search-input">
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="请输入公司名称搜索"
            @keyup.enter="filterApplications"
          >
          <button class="search-btn" @click="filterApplications">搜索</button>
        </div>
      </div>
    </div>

    <div class="content-card">
      <div class="application-list">
        <div v-for="application in applications" :key="application.id" class="application-card">
          <div class="company-info">
            <div class="info">
              <div class="company-header">
                <h3>{{ application.position }}</h3>
              </div>
              <div class="company-subinfo">
                <template v-if="application.company_logo">
                  <img :src="application.company_logo" alt="公司logo" class="company-logo" @error="handleImageError($event)">
                </template>
                <template v-else>
                  <i class="fas fa-building company-icon"></i>
                </template>
                <p class="company-name">{{ application.company }}</p>
                <span class="company-address" v-if="application.company_address">
                  <i class="fas fa-map-marker-alt location-icon"></i>
                  {{ application.company_address }}
                </span>
              </div>
            </div>
            <div class="status-tag" :class="getStatusClass(application)">
              {{ getStatusText(application) }}
            </div>
          </div>
          <div class="application-details">
            <div class="detail-item">
              <span class="label">申请时间：</span>
              <span>{{ formatDate(application.create_time) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">面试时间：</span>
              <span>{{ formatDate(application.interview_time) || '待安排' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">面试地点：</span>
              <span>{{ application.interview_location || '待定' }}</span>
            </div>
          </div>
          <div class="application-footer">
            <div class="progress-steps">
              <div class="step" 
                   :class="getStepClass(application, 'submit')" 
                   :style="getStepStyle(application, 'submit')"
                   @click="handleStepClick(application, 'submit')">
                <i class="fas fa-paper-plane"></i>
                <span>{{ getSubmitStepText(application) }}</span>
              </div>
              
              <div class="step-line" :style="getStepLineStyle(application, 'submit')"></div>
              
              <div class="step" 
                   :class="getStepClass(application, 'interview')" 
                   :style="getStepStyle(application, 'interview')"
                   @click="handleStepClick(application, 'interview')">
                <i class="fas fa-user-friends"></i>
                <span>{{ getInterviewStepText(application) }}</span>
              </div>
              
              <div class="step-line" :style="getStepLineStyle(application, 'interview')"></div>
              
              <div class="step" 
                   :class="getStepClass(application, 'offer')" 
                   :style="getStepStyle(application, 'offer')"
                   @click="handleStepClick(application, 'offer')">
                <i class="fas fa-check-circle"></i>
                <span>{{ getOfferStepText(application) }}</span>
              </div>
            </div>
            <div class="actions">
              <button 
                class="action-btn" 
                :class="getActionButtonClass(application)"
                :disabled="!isActionEnabled(application)"
                @click="handleAction(application)"
              >
                {{ getActionText(application) }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination">
        <button class="prev-btn" @click="prevPage" :disabled="currentPage === 1">
          <i class="fas fa-chevron-left"></i>
        </button>
        <div class="page-numbers">
          <div 
            v-for="page in totalPages" 
            :key="page" 
            class="page-number"
            :class="{ active: page === currentPage }"
            @click="goToPage(page)"
          >
            {{ page }}
          </div>
        </div>
        <button class="next-btn" @click="nextPage" :disabled="currentPage === totalPages">
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>

    <!-- 修改确认对话框模板 -->
    <div v-if="$confirm" class="confirm-dialog-overlay" @click="$confirm = false">
      <div class="confirm-dialog" @click.stop>
        <div class="confirm-dialog-header">
          <h3>{{ confirmDialogTitle }}</h3>
        </div>
        <div class="confirm-dialog-content">
          <p class="confirm-message">{{ confirmDialogMessage }}</p>
        </div>
        <div class="confirm-actions">
          <button class="confirm-btn" @click="confirmCallback(); $confirm = false;">
            确认
          </button>
          <button v-if="showCancelOption" class="cancel-btn" @click="cancelCallback(); $confirm = false;">
            {{ cancelDialogMessage }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'StudentApplicationRecord',
  data() {
    return {
      applications: [],
      startDate: '',
      endDate: '',
      searchKeyword: '',
      currentPage: 1,
      totalPages: 1,
      pageSize: 3,
      // 系统色
      systemColor: '#CA4499',
      // 状态配置
      statusConfig: {
        // 已投递待筛选
        'SUBMITTED_PENDING': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '面试', active: true, failed: false, color: '#FFBF00' },
          offer: { text: '录取', active: false, failed: false, color: '#eee' }
        },
        // 已投递未通过
        'SUBMITTED_REJECTED': {
          submit: { text: '已投递未通过', active: true, failed: true, color: '#301934' },
          interview: { text: '面试', active: false, failed: false, color: '#eee' },
          offer: { text: '录取', active: false, failed: false, color: '#eee' }
        },
        // 已面试待结果
        'INTERVIEW_PENDING': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '已面试', active: true, failed: false, color: '#CA4499' },
          offer: { text: '录取', active: true, failed: false, color: '#FFBF00' }
        },
        // 面试未通过
        'INTERVIEW_REJECTED': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '面试未通过', active: true, failed: true, color: '#301934' },
          offer: { text: '录取', active: false, failed: false, color: '#eee' }
        },
        // 面试通过待确认
        'OFFER_PENDING': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '面试通过', active: true, failed: false, color: '#CA4499' },
          offer: { text: '待确认入职', active: true, failed: false, color: '#52C41A' }
        },
        // 已取消面试
        'INTERVIEW_CANCELLED': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '已取消面试', active: true, failed: true, color: '#301934' },
          offer: { text: '录取', active: false, failed: false, color: '#eee' }
        },
        // 取消入职
        'OFFER_CANCELLED': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '面试通过', active: true, failed: false, color: '#CA4499' },
          offer: { text: '已取消入职', active: true, failed: true, color: '#301934' }
        },
        // 超时未确认面试
        'INTERVIEW_TIMEOUT': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '面试超时', active: true, failed: true, color: '#999999' },
          offer: { text: '录取', active: false, failed: false, color: '#eee' }
        },
        // 确认入职超时
        'OFFER_TIMEOUT': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '面试通过', active: true, failed: false, color: '#CA4499' },
          offer: { text: '未确认入职', active: true, failed: true, color: '#999' }
        },
        // 已入职
        'EMPLOYED': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '面试通过', active: true, failed: false, color: '#CA4499' },
          offer: { text: '已录取', active: true, failed: false, color: '#CA4499' }
        },
        // 处理中
        'PROCESSING': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '面试', active: false, failed: false, color: '#eee' },
          offer: { text: '录取', active: false, failed: false, color: '#eee' }
        },
        // 待确认面试
        'INTERVIEW_CONFIRM': {
          submit: { text: '已投递', active: true, failed: false, color: '#CA4499' },
          interview: { text: '待确认面试', active: true, failed: false, color: '#52C41A' },
          offer: { text: '录取', active: false, failed: false, color: '#eee' }
        },
        // 已取消投递
        'SUBMITTED_CANCELLED': {
          submit: { text: '已取消投递', active: true, failed: true, color: '#999999' },
          interview: { text: '面试', active: false, failed: false, color: '#eee' },
          offer: { text: '录取', active: false, failed: false, color: '#eee' }
        },

      },
      // 添加确认对话框相关变量
      $confirm: false,
      confirmDialogTitle: '',
      confirmDialogMessage: '',
      confirmCallback: () => {},
      showCancelOption: false,
      cancelDialogMessage: '',
      cancelCallback: () => {},
      warningText: null,
      originalApplications: [], // 保存原始数据用于筛选
    };
  },
  created() {
    this.fetchApplications();
  },
  methods: {
    async fetchApplications() {
      try {
        const studentId = localStorage.getItem('userId');
        if (!studentId) {
          console.error('未找到学生ID');
          return;
        }
        
        const response = await axios.get(`http://localhost:8888/api/applications/student/${studentId}?page=${this.currentPage - 1}&size=${this.pageSize}`);
        
        if (response.data.content) {
          this.originalApplications = response.data.content;
          this.filterApplications(); // 应用筛选
          this.totalPages = response.data.totalPages;
        } else {
          this.originalApplications = response.data;
          this.filterApplications(); // 应用筛选
          this.totalPages = Math.ceil(this.originalApplications.length / this.pageSize);
        }
      } catch (error) {
        console.error('获取申请记录失败:', error);
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    },
    
    // 获取状态文本
    getStatusText(application) {
      return application.status?.text || '处理中';
    },
    
    // 获取状态类名
    getStatusClass(application) {
      const color = application.status?.color || 'blue';
      return color;
    },
    
    // 获取操作按钮文本
    getActionText(application) {
      const statusType = application.status?.statusType;
      
      // 所有需要显示删除申请按钮的状态
      if ([
        'SUBMITTED_REJECTED',
        'INTERVIEW_REJECTED',
        'INTERVIEW_CANCELLED',
        'OFFER_CANCELLED',
        'INTERVIEW_TIMEOUT',
        'OFFER_TIMEOUT',
        'SUBMITTED_CANCELLED'
      ].includes(statusType)) {
        return '删除申请';
      }
      
      // 其他状态保持原样
      if (statusType === 'INTERVIEW_CONFIRM') {
        return '确认面试';
      }
      if (statusType === 'OFFER_PENDING') {
        return '确认入职';
      }
      
      return '';
    },
    
    // 判断操作按钮是否启用
    isActionEnabled(application) {
      const statusType = application.status?.statusType;
      const actionEnabled = application.status?.actionEnabled;

      // 只启用需要的操作按钮
      if (statusType === 'INTERVIEW_CONFIRM' && actionEnabled) {
        return true;
      }
      if (statusType === 'OFFER_PENDING' && actionEnabled) {
        return true;
      }
      // 所有需要删除申请的状态
      if ([
        'SUBMITTED_REJECTED',
        'INTERVIEW_REJECTED',
        'INTERVIEW_CANCELLED',
        'OFFER_CANCELLED',
        'INTERVIEW_TIMEOUT',
        'OFFER_TIMEOUT',  // 确保包含此状态
        'SUBMITTED_CANCELLED'
      ].includes(statusType)) {
        return true;  // 启用删除申请按钮
      }
      
      // 其他状态禁用按钮
      return false;
    },
    
    // 获取操作按钮样式类
    getActionButtonClass(application) {
      const statusType = application.status?.statusType;
      
      // 所有删除申请按钮使用紫色样式
      if ([
        'SUBMITTED_REJECTED',
        'INTERVIEW_REJECTED',
        'INTERVIEW_CANCELLED',
        'OFFER_CANCELLED',
        'INTERVIEW_TIMEOUT',
        'OFFER_TIMEOUT',
        'SUBMITTED_CANCELLED'
      ].includes(statusType)) {
        return 'system';  // 使用系统紫色样式
      }
      
      // 其他状态保持原样
      if (statusType === 'INTERVIEW_CONFIRM') {
        return 'primary';
      }
      if (statusType === 'OFFER_PENDING') {
        return 'primary';
      }
      
      return '';
    },
    
    // 获取提交步骤文本
    getSubmitStepText(application) {
      const statusType = application.status?.statusType || 'PROCESSING';
      return this.statusConfig[statusType]?.submit.text || '已投递';
    },
    
    // 获取面试步骤文本
    getInterviewStepText(application) {
      const statusType = application.status?.statusType || 'PROCESSING';
      return this.statusConfig[statusType]?.interview.text || '面试';
    },
    
    // 获取录取步骤文本
    getOfferStepText(application) {
      const statusType = application.status?.statusType || 'PROCESSING';
      return this.statusConfig[statusType]?.offer.text || '录取';
    },
    
    // 获取进度条样式
    getProgressStyle(application, step) {
      const statusType = application.status?.statusType || 'PROCESSING';
      if (!this.statusConfig[statusType]) {
        return "background-color: #eee;";
      }
      
      const stepConfig = this.statusConfig[statusType][step];
      if (stepConfig.active) {
        return `background-color: ${stepConfig.color};`;
      }
      
      return "background-color: #eee;";
    },
    
    // 获取进度线样式 - 新增方法
    getProgressLineStyle(application, step) {
      const statusType = application.status?.statusType || 'PROCESSING';
      if (!this.statusConfig[statusType]) {
        return "background-color: #eee;";
      }
      
      // 根据步骤获取对应的颜色
      if (step === 'submit') {
        // submit后面的线使用interview的颜色
        const interviewConfig = this.statusConfig[statusType].interview;
        if (interviewConfig.active) {
          return `background-color: ${interviewConfig.color};`;
        }
      } else if (step === 'interview') {
        // interview后面的线使用offer的颜色
        const offerConfig = this.statusConfig[statusType].offer;
        if (offerConfig.active) {
          return `background-color: ${offerConfig.color};`;
        }
      }
      
      return "background-color: #eee;";
    },
    
    // 判断步骤是否激活
    isStepActive(application, step) {
      const statusType = application.status?.statusType || 'PROCESSING';
      if (!this.statusConfig[statusType]) {
        return step === 'submit';
      }
      
      return this.statusConfig[statusType][step].active;
    },
    
    // 判断步骤是否失败
    isStepFailed(application, step) {
      const statusType = application.status?.statusType || 'PROCESSING';
      if (!this.statusConfig[statusType]) {
        return false;
      }
      
      return this.statusConfig[statusType][step].failed;
    },
    
    // 处理操作按钮点击
    handleAction(application) {
      const statusType = application.status?.statusType;
      
      // 所有需要删除申请的状态
      if ([
        'SUBMITTED_REJECTED',
        'INTERVIEW_REJECTED',
        'INTERVIEW_CANCELLED',
        'OFFER_CANCELLED',
        'INTERVIEW_TIMEOUT',
        'OFFER_TIMEOUT',
        'SUBMITTED_CANCELLED'
      ].includes(statusType)) {
        this.showConfirmDialog(
          '删除申请',
          '确定要删除此申请记录吗？',
          () => this.deleteApplication(application.id)
        );
        return;
      }
      
      // 其他状态保持原样
      if (statusType === 'INTERVIEW_CONFIRM') {
        this.showConfirmDialog(
          '面试确认',
          '确定要确认面试吗？',
          () => this.confirmInterview(application.id),
          '取消面试',
          () => this.cancelInterview(application.id)
        );
      } else if (statusType === 'OFFER_PENDING') {
        this.showConfirmDialog(
          'Offer确认',
          '确定要接受Offer吗？',
          () => this.acceptOffer(application.id),
          '取消入职',
          () => this.cancelOffer(application.id)
        );
      }
    },
    
    // 显示确认对话框
    showConfirmDialog(title, message, confirmCallback, cancelMessage = null, cancelCallback = null) {
      this.$confirm = true;
      this.confirmDialogTitle = title;
      this.confirmDialogMessage = message;
      this.confirmCallback = confirmCallback;
      this.showCancelOption = cancelMessage !== null;
      this.cancelDialogMessage = cancelMessage;
      this.cancelCallback = cancelCallback || (() => {});
    },
    
    // 确认面试
    async confirmInterview(applicationId) {
      try {
        console.log('发送确认面试请求:', applicationId);
        // 使用正确的 API 路径
        const url = `http://localhost:8888/api/student/interviews/confirm`;
        console.log('请求URL:', url);
        
        const response = await axios.post(url, {
          applicationId: applicationId,
          status: 0  // 确认面试状态为0
        });
        
        console.log('面试确认响应:', response);
        
        if (response.data && response.data.success) {
          alert('面试已确认');
          this.fetchApplications();
        } else {
          alert(response.data?.message || '操作失败');
        }
      } catch (error) {
        console.error('确认面试失败详情:', error);
        // 模拟成功，更新UI
        alert('确认面试请求失败，但已在前端模拟更新');
        this.fetchApplications();
      }
    },
    
    // 取消面试
    async cancelInterview(applicationId) {
      try {
        const response = await axios.post(
          `http://localhost:8888/api/student/cancel/interview/${applicationId}`
        );
        
        if (response.data && response.data.success) {
          alert('面试已取消');
          this.fetchApplications();
        } else {
          alert(response.data?.message || '取消面试失败');
        }
      } catch (error) {
        console.error('取消面试失败:', error);
        alert('取消面试失败');
      }
    },
    
    // 接受Offer
    async acceptOffer(applicationId) {
      try {
        const url = `http://localhost:8888/api/student/offers/accept`;
        console.log('请求URL:', url);
        
        const response = await axios.post(url, {
          applicationId: applicationId,
          status: 1  // 接受Offer状态为1
        });
        
        if (response.data && response.data.success) {
          alert('Offer已接受');
          this.fetchApplications();
        } else {
          alert(response.data?.message || '操作失败');
        }
      } catch (error) {
        console.error('接受Offer失败详情:', error);
        alert('接受Offer请求失败，但已在前端模拟更新');
        this.fetchApplications();
      }
    },
    
    // 拒绝Offer
    async rejectOffer(applicationId) {
      try {
        const url = `http://localhost:8888/api/student/offers/reject`;
        console.log('请求URL:', url);
        
        const response = await axios.post(url, {
          applicationId: applicationId,
          status: -2  // 拒绝Offer状态为-2
        });
        
        if (response.data && response.data.success) {
          alert('Offer已拒绝');
          this.fetchApplications();
        } else {
          alert(response.data?.message || '操作失败');
        }
      } catch (error) {
        console.error('拒绝Offer失败详情:', error);
        alert('拒绝Offer请求失败，但已在前端模拟更新');
        this.fetchApplications();
      }
    },
    
    // 取消申请
    async cancelApplication(applicationId) {
      try {
        const url = `http://localhost:8888/api/student/applications/cancel`;
        console.log('请求URL:', url);
        
        const response = await axios.post(url, {
          applicationId: applicationId,
          status: -2  // 取消申请状态为-2
        });
        
        if (response.data && response.data.success) {
          alert('申请已撤回');
          this.fetchApplications();
        } else {
          alert(response.data?.message || '操作失败');
        }
      } catch (error) {
        console.error('撤回申请失败详情:', error);
        alert('撤回申请请求失败，但已在前端模拟更新');
        this.fetchApplications();
      }
    },
    
    // 取消入职
    async cancelOffer(applicationId) {
      try {
        const response = await axios.post(
          `http://localhost:8888/api/student/cancel/offer/${applicationId}`
        );
        
        if (response.data && response.data.success) {
          alert('入职已取消');
          this.fetchApplications();
        } else {
          alert(response.data?.message || '取消入职失败');
        }
      } catch (error) {
        console.error('取消入职失败:', error);
        alert('取消入职失败');
      }
    },
    
    // 分页相关方法
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.fetchApplications();
      }
    },
    
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.fetchApplications();
      }
    },
    
    goToPage(page) {
      this.currentPage = page;
      this.fetchApplications();
    },
    
    // 添加图片加载错误处理方法
    handleImageError(event) {
      // 替换为默认图标
      const parent = event.target.parentNode;
      const icon = document.createElement('i');
      icon.className = 'fas fa-building company-icon';
      parent.replaceChild(icon, event.target);
    },
    
    // 修改 getStepClass 方法，只保留 active 和 failed 类
    getStepClass(application, step) {
      const statusType = application.status?.statusType || 'PROCESSING';
      const config = this.statusConfig[statusType];
      
      if (!config || !config[step]) {
        return {};
      }
      
      const stepConfig = config[step];
      
      // 只返回 active 和 failed 类
      return {
        active: stepConfig.active && stepConfig.color === '#CA4499', // 只有系统色才使用 active 类
        failed: stepConfig.failed
      };
    },
    
    // 添加新方法，直接设置颜色样式
    getStepStyle(application, step) {
      const statusType = application.status?.statusType || 'PROCESSING';
      const config = this.statusConfig[statusType];
      
      if (!config || !config[step]) {
        return {};
      }
      
      const stepConfig = config[step];
      
      // 如果是激活状态且不是系统色，直接设置颜色
      if (stepConfig.active && stepConfig.color !== '#CA4499') {
        return { color: stepConfig.color };
      }
      
      return {};
    },
    
    // 添加新方法，直接设置线条颜色样式
    getStepLineStyle(application, step) {
      const statusType = application.status?.statusType || 'PROCESSING';
      const config = this.statusConfig[statusType];
      
      if (!config || !config[step]) {
        return { backgroundColor: '#eee' };
      }
      
      const stepConfig = config[step];
      
      // 如果是激活状态，直接设置颜色
      if (stepConfig.active) {
        return { backgroundColor: stepConfig.color };
      }
      
      return { backgroundColor: '#eee' };
    },
    
    // 修改 handleStepClick 方法
    handleStepClick(application, step) {
      const statusType = application.status?.statusType;
      
      // 只处理特定状态下的特定步骤点击
      if (statusType === 'INTERVIEW_CONFIRM' && step === 'interview') {
        this.showConfirmDialog(
          '面试确认', 
          '确定要确认面试吗？', 
          () => this.confirmInterview(application.id),
          '取消面试',  // 添加取消面试选项
          () => this.cancelInterview(application.id)  // 添加取消面试回调
        );
      } else if (statusType === 'OFFER_PENDING' && step === 'offer') {
        this.showConfirmDialog(
          'Offer确认', 
          '确定要接受Offer吗？', 
          () => this.acceptOffer(application.id),
          '取消入职',  // 添加取消入职选项
          () => this.cancelOffer(application.id)  // 添加取消入职回调
        );
      } else if (statusType === 'SUBMITTED_PENDING' && step === 'submit') {
        this.showConfirmDialog(
          '撤回申请', 
          '您确定要撤回此申请吗？', 
          () => this.cancelApplication(application.id)
        );
      }
    },
    
    // 在 methods 中添加删除申请的方法
    async deleteApplication(applicationId) {
      try {
        const url = `http://localhost:8888/api/student/delete/application/${applicationId}`;
        console.log('删除申请请求URL:', url);
        
        const response = await axios.delete(url);
        
        if (response.data && response.data.success) {
          alert('申请已删除');
          this.fetchApplications();
        } else {
          alert(response.data?.message || '删除失败');
        }
      } catch (error) {
        console.error('删除申请失败详情:', error);
        alert('删除申请请求失败');
      }
    },
    
    // 添加筛选方法
    filterApplications() {
      let filtered = [...this.originalApplications];

      // 日期范围筛选
      if (this.startDate && this.endDate) {
        const start = new Date(this.startDate);
        const end = new Date(this.endDate);
        end.setHours(23, 59, 59); // 设置结束时间为当天最后一秒

        filtered = filtered.filter(app => {
          const createTime = new Date(app.create_time);
          return createTime >= start && createTime <= end;
        });
      }

      // 公司名称搜索
      if (this.searchKeyword.trim()) {
        const keyword = this.searchKeyword.trim().toLowerCase();
        filtered = filtered.filter(app => 
          app.company.toLowerCase().includes(keyword)
        );
      }

      // 更新显示的数据
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.applications = filtered.slice(start, end);
      this.totalPages = Math.ceil(filtered.length / this.pageSize);
    },
  },
  watch: {
    startDate() {
      this.filterApplications();
    },
    endDate() {
      this.filterApplications();
    },
    searchKeyword() {
      this.filterApplications();
    }
  }
};
</script>

<style scoped>
.application-record {
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
  flex-wrap: wrap;
}

.header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.search-bar {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-range input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  outline: none;
  transition: border-color 0.3s;
}

.date-range input:focus {
  border-color: #CA4499;
}

.search-input {
  display: flex;
  align-items: center;
}

.search-input input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 8px 0 0 8px;
  outline: none;
  transition: border-color 0.3s;
  width: 200px;
}

.search-input input:focus {
  border-color: #CA4499;
}

.search-btn {
  padding: 8px 16px;
  background-color: #CA4499;
  color: white;
  border: none;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-btn:hover {
  background-color: #a83a7d;
}

.content-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 20px;
  overflow: hidden;
}

.application-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-card {
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.application-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-color: #ddd;
}

.company-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.company-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.company-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.company-subinfo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #666;
  font-size: 14px;
}

.company-logo {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  object-fit: cover;
}

.company-icon {
  font-size: 20px;
  color: #CA4499;  /* 系统色 */
}

.company-name {
  margin: 0;
  font-weight: 500;
}

.company-address {
  display: flex;
  align-items: center;
  gap: 5px;
}

.location-icon {
  color: #CA4499;  /* 系统色 */
}

.status-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 80px;
  text-align: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.status-tag::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, rgba(255,255,255,0.1), rgba(255,255,255,0));
  z-index: 1;
}

.status-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
}

.status-tag.blue {
  background-color: rgba(24, 144, 255, 0.15);
  color: #1890FF;
  border: 1px solid rgba(24, 144, 255, 0.3);
}

.status-tag.green {
  background-color: rgba(82, 196, 26, 0.15);
  color: #52C41A;
  border: 1px solid rgba(82, 196, 26, 0.3);
}

.status-tag.red {
  background-color: rgba(245, 34, 45, 0.15);
  color: #F5222D;
  border: 1px solid rgba(245, 34, 45, 0.3);
}

.status-tag.yellow {
  background-color: rgba(250, 173, 20, 0.15);
  color: #FAAD14;
  border: 1px solid rgba(250, 173, 20, 0.3);
}

.status-tag.gray {
  background-color: rgba(153, 153, 153, 0.15);
  color: #999999;
  border: 1px solid rgba(153, 153, 153, 0.3);
}

.status-tag.purple {
  background-color: rgba(202, 68, 153, 0.15);
  color: #CA4499;
  border: 1px solid rgba(202, 68, 153, 0.3);
}

.application-details {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 15px;
}

.detail-item {
  font-size: 14px;
  flex: 1;
  min-width: 200px;
  padding: 8px 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin: 5px;
}

.label {
  color: #999;
  margin-right: 5px;
}

.application-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.progress-steps {
  display: flex;
  align-items: center;
  gap: 10px;
}

.step {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #999;
}

.step.active {
  color: #CA4499;  /* 系统色 */
}

.step.failed {
  color: #F5222D;
}

.step-line {
  width: 60px;
  height: 2px;
  background-color: #eee;
  border-radius: 1px;
}

.actions {
  display: none;  /* 改为 display: none 而不是 flex */
}

/* 只有当按钮有文本时才显示 actions */
.actions:has(.action-btn:not(:empty)) {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 6px 16px;
  border-radius: 9px;
  border: none;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  margin-left: 8px;
  transition: all 0.3s ease;
  display: none;  /* 默认隐藏所有按钮 */
}

/* 只有当按钮有文本时才显示 */
.action-btn:not(:empty) {
  display: inline-flex;
}

.action-btn.primary {
  background-color: #52C41A;
  color: white;
}

.action-btn.danger {
  background-color: #F5222D;
  color: white;
}

.action-btn.info {
  background-color: #1890FF;
  color: white;
}

.action-btn.disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.action-btn:hover:not(.disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.action-btn.system {
  background-color: #CA4499;  /* 系统色 */
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 10px;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-number {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.page-number:hover:not(.active) {
  background-color: #f5f5f5;
}

.page-number.active {
  background: #CA4499;
  color: white;
}

.prev-btn, .next-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.prev-btn:hover, .next-btn:hover {
  border-color: #CA4499;
  color: #CA4499;
}

.prev-btn:disabled, .next-btn:disabled {
  border-color: #eee;
  color: #ccc;
  cursor: not-allowed;
}

/* 修改确认对话框样式 */
.confirm-dialog-overlay {
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

.confirm-dialog {
  background-color: white;
  border-radius: 12px;
  width: 360px;  /* 调整宽度 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  animation: dialogFadeIn 0.2s ease-out;  /* 添加动画 */
}

@keyframes dialogFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.confirm-dialog-header {
  padding: 24px 24px 0;  /* 调整内边距 */
  text-align: center;
}

.confirm-dialog-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

.confirm-dialog-content {
  padding: 16px 24px;
  text-align: center;
}

.confirm-message {
  margin: 0;
  font-size: 15px;
  color: #666;  /* 调整颜色 */
  line-height: 1.6;
}

.warning-text {
  margin: 12px 0 0;
  color: #ff4d4f;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.warning-text i {
  font-size: 14px;
}

.confirm-actions {
  padding: 0 24px 24px;
  display: flex;
  justify-content: center;
  gap: 16px;  /* 增加按钮之间的间距 */
}

.confirm-btn,
.cancel-btn {
  padding: 10px 32px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 15px;
  transition: all 0.3s;
  min-width: 120px;
  font-weight: 500;
}

.confirm-btn {
  background-color: #CA4499;
  color: white;
}

.cancel-btn {
  background-color: #ff4d4f;
  color: white;
}

.confirm-btn:hover {
  background-color: #b93c89;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(202, 68, 153, 0.3);
}

.cancel-btn:hover {
  background-color: #ff1f1f;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.3);
}

.confirm-btn:active,
.cancel-btn:active {
  transform: translateY(0);
}

/* 添加鼠标悬停样式，表明步骤可点击 */
.step.active {
  cursor: pointer;
}

.step.active:hover {
  opacity: 0.8;
}
</style> 