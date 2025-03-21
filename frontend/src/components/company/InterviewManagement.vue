<template>
  <div class="interview-management">
    <div class="header">
      <h2>面试管理</h2>
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
        <button class="export-btn" @click="exportInterviews">
          <i class="fas fa-file-export"></i>
          导出记录
        </button>
      </div>
    </div>

    <div class="content-card">
      <div class="table-header">
        <div class="header-item">
          <i class="fas fa-minus-circle"></i>
        </div>
        <div class="header-item">姓名</div>
        <div class="header-item">应聘岗位</div>
        <div class="header-item">面试时间</div>
        <div class="header-item">面试官</div>
        <div class="header-item">技术评分</div>
        <div class="header-item">综合评分</div>
        <div class="header-item">状态</div>
        <div class="header-item">操作</div>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
          </div>

      <div v-else class="interview-list">
        <div 
          v-for="interview in interviews" 
          :key="interview.id" 
          class="interview-item"
          :class="{ 'hover-effect': true }"
          @click="handleRowClick(interview)"
        >
          <div class="item-cell"><i class="fas fa-minus-circle"></i></div>
          <div class="item-cell">{{ interview.studentName }}</div>
          <div class="item-cell">{{ interview.jobTitle }}</div>
          <div class="item-cell">{{ formatDateTime(interview.interviewTime) }}</div>
          <div class="item-cell">{{ interview.evaluator || interview.interviewer }}</div>
          <div class="item-cell">
            <div class="score-stars">
              <i v-for="n in 5" :key="n" 
                 :class="['fas', n <= (interview.technicalScore || 0) ? 'fa-star' : 'fa-star-o']"
              ></i>
            </div>
          </div>
          <div class="item-cell">
            <div class="score-stars">
              <i v-for="n in 5" :key="n" 
                 :class="['fas', n <= (interview.comprehensiveScore || 0) ? 'fa-star' : 'fa-star-o']"
              ></i>
            </div>
          </div>
          <div class="item-cell">
            <span 
              class="status-tag" 
              :class="{ 
                'status-pending': interview.status === 0,
                'status-completed': interview.status === 1,
                'status-rejected': interview.status === -1
              }"
            >
              {{ getStatusText(interview.status) }}
            </span>
          </div>
          <div class="item-cell">
            <button 
              v-if="interview.status === 0"
              class="result-btn" 
              @click.stop="openResultModal(interview)"
            >面试结果</button>
            <button 
              v-else
              class="view-btn" 
              @click.stop="viewResult(interview)"
            >查看详情</button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 面试结果弹窗 -->
  <el-dialog
    v-model="showResultModal"
    width="500px"
    center
    :close-on-click-modal="false"
    class="result-dialog"
  >
  <template #title>
    <span class="custom-title">面试结果</span>
  </template>
    <div class="result-content">
      <div class="candidate-info">
        
          <div class="info-item">
            <i class="fas fa-user"></i>
            <span class="label">应聘者</span>
            <span class="value">{{ selectedInterview?.studentName }}</span>
          </div>
          <div class="info-item">
            <i class="fas fa-briefcase"></i>
            <span class="label">应聘岗位</span>
            <span class="value">{{ selectedInterview?.jobTitle }}</span>
        </div>
        
      </div>
      <div class="result-actions">
        <el-button type="primary" @click="handlePass" :loading="submitting">
          <i class="fas fa-check-circle"></i> 通过
        </el-button>
        <el-button @click="handleFail" :loading="submitting">
          <i class="fas fa-times-circle"></i> 不通过
        </el-button>
      </div>
    </div>
  </el-dialog>

  <!-- Offer发送弹窗 -->
  <el-dialog
    v-model="showOfferModal"
    center
    width="700px"
    :close-on-click-modal="false"
    class="offer-dialog"
  >
  <template #title>
    <span class="custom-title">发送Offer</span>
  </template>

    <div class="offer-content">
      <div class="candidate-info">
        <div class="offer-info-grid">
          <div class="offer-info-item">     
            <i class="fas fa-user"></i>
            <span class="offer-label">应聘者</span>
            <span class="offer-value">{{ selectedInterview?.studentName }}</span>
        </div>
          <div class="offer-info-item">
            <i class="fas fa-briefcase"></i>
            <span class="offer-label">应聘岗位</span>
            <span class="offer-value">{{ selectedInterview?.jobTitle }}</span>
      </div>
    </div>
  </div>
    
    <div class="offer-form-container">
      <div class="offer-form-row">
        <div class="offer-form-item">
          <div class="offer-form-label">
            <i class="fas fa-calendar-alt"></i>
            <span>入职时间</span>
          </div>
          <el-date-picker
            v-model="offerForm.startDate"
            type="date"
            class="offer-input"
            placeholder="选择入职时间"
          />
        </div>
        <div class="offer-form-item">
          <div class="offer-form-label">
            <i class="fas fa-money-bill-wave"></i>
            <span>薪资待遇</span>
          </div>
          <el-input 
            v-model="offerForm.salary" 
            class="offer-input"
            placeholder="请输入薪资待遇" 
          />
        </div>
      </div>
      
      <div class="offer-form-row">
        <div class="offer-form-item">
          <div class="offer-form-label">
            <i class="fas fa-hourglass-half"></i>
            <span>试用期</span>
          </div>
          <div class="offer-input-with-unit">
            <el-input-number 
              v-model="offerForm.probationMonths" 
              :min="1" 
              :max="6"
              class="offer-number-input"
            />
            <span class="offer-unit">个月</span>
          </div>
        </div>
        <div class="offer-form-item">
          <div class="offer-form-label">
            <i class="fas fa-calendar-check"></i>
            <span>接受期限</span>
          </div>
          <div class="offer-input-with-unit">
            <el-input-number 
              v-model="offerForm.acceptDays" 
              :min="1" 
              :max="30"
              class="offer-number-input"
            />
            <span class="offer-unit">天</span>
          </div>
        </div>
      </div>
      
      <div class="offer-form-row">
        <div class="offer-form-item full-width">
          <div class="offer-form-label">
            <i class="fas fa-comment-alt"></i>
            <span>其他说明</span>
          </div>
          <el-input
            v-model="offerForm.remarks"
            type="textarea"
            :rows="4"
            class="offer-textarea"
            placeholder="请输入其他说明"
          />
        </div>
      </div>
    </div>
  </div>
  <template #footer>
    <div class="offer-dialog-footer">
      <el-button @click="showOfferModal = false" class="offer-cancel-btn">取消</el-button>
      <el-button type="primary" @click="sendOffer" :loading="submitting" class="offer-submit-btn">
        发送Offer
      </el-button>
  </div>
  </template>
</el-dialog>

  <!-- 不通过确认弹窗 -->
  <el-dialog
    v-model="showRejectModal"
    center
    width="600px"
    :close-on-click-modal="false"
    class="reject-dialog"
  >
  <template #title>
    <span class="custom-title">确认不通过</span>
  </template>
    <div class="reject-content">
      <div class="candidate-info">
        <div class="reject-info-grid">
          <div class="reject-info-item">     
            <i class="fas fa-user"></i>
            <span class="reject-label">应聘者</span>
            <span class="reject-value">{{ selectedInterview?.studentName }}</span>
          </div>
          <div class="reject-info-item">
            <i class="fas fa-briefcase"></i>
            <span class="reject-label">应聘岗位</span>
            <span class="reject-value">{{ selectedInterview?.jobTitle }}</span>
          </div>
        </div>
      </div>
      
      <div class="reject-form-container">
        <div class="reject-form-item">
          <div class="reject-form-label">
            <i class="fas fa-times-circle"></i>
            <span>不通过原因</span>
          </div>
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="5"
            class="reject-textarea"
            placeholder="请输入不通过原因，此信息将会反馈给候选人"
          />
        </div>
      </div>
    </div>
    <template #footer>
      <div class="reject-dialog-footer">
        <el-button @click="showRejectModal = false" class="reject-cancel-btn">取消</el-button>
        <el-button type="primary" @click="confirmReject" :loading="submitting" class="reject-submit-btn">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 查看详情弹窗 -->
  <el-dialog
    v-model="showDetailModal"
    :title="getDetailTitle(selectedInterview?.status)"
    center
    width="700px"
    class="detail-dialog"
  >
    <div class="detail-content">
      <div class="detail-section">
        <h4><i class="fas fa-id-card"></i> 基本信息</h4>
        <div class="info-grid">
          <div class="info-item">
            <i class="fas fa-user"></i>
            <span class="label">应聘者</span>
            <span class="value">{{ selectedInterview?.studentName }}</span>
          </div>
          <div class="info-item">
            <i class="fas fa-briefcase"></i>
            <span class="label">应聘岗位</span>
            <span class="value">{{ selectedInterview?.jobTitle }}</span>
          </div>
          <div class="info-item">
            <i class="fas fa-clock"></i>
            <span class="label">面试时间</span>
            <span class="value">{{ formatDateTime(selectedInterview?.interviewTime) }}</span>
          </div>
          <div class="info-item">
            <i class="fas fa-user-tie"></i>
            <span class="label">面试官</span>
            <span class="value">{{ selectedInterview?.interviewer }}</span>
          </div>
        </div>
      </div>

      <div class="detail-section" v-if="selectedInterview?.technicalScore">
        <h4><i class="fas fa-star"></i> 面试评价</h4>
        <div class="evaluation-grid">
          <div class="score-row">
            <div class="score-item">
              <div class="score-header">
                <i class="fas fa-code"></i>
                <span class="label">技术评分</span>
              </div>
              <el-rate
                v-model="selectedInterview.technicalScore"
                disabled
                :colors="['#CA4499', '#CA4499', '#CA4499']"
              />
            </div>
            <div class="score-item">
              <div class="score-header">
                <i class="fas fa-user-check"></i>
                <span class="label">综合评分</span>
              </div>
              <el-rate
                v-model="selectedInterview.comprehensiveScore"
                disabled
                :colors="['#CA4499', '#CA4499', '#CA4499']"
              />
            </div>
          </div>
          <div class="feedback-row">
            <div class="feedback-item">
              <div class="feedback-header">
                <i class="fas fa-code-branch"></i>
                <span class="label">技术评价</span>
              </div>
              <p class="feedback">{{ selectedInterview?.technicalFeedback }}</p>
            </div>
            <div class="feedback-item">
              <div class="feedback-header">
                <i class="fas fa-comments"></i>
                <span class="label">综合评价</span>
              </div>
              <p class="feedback">{{ selectedInterview?.comprehensiveFeedback }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section" v-if="selectedInterview?.status === 1">
        <h4><i class="fas fa-file-contract"></i> Offer信息</h4>
        <div class="offer-info-grid">
          <div class="offer-info-item">
            <i class="fas fa-calendar-alt"></i>
            <span class="label">入职时间</span>
            <span class="value">{{ formatDate(selectedInterview?.offerStartDate) }}</span>
          </div>
          <div class="offer-info-item">
            <i class="fas fa-money-bill-wave"></i>
            <span class="label">薪资待遇</span>
            <span class="value">{{ selectedInterview?.offerSalary }}</span>
          </div>
          <div class="offer-info-item">
            <i class="fas fa-hourglass-half"></i>
            <span class="label">试用期</span>
            <span class="value">{{ selectedInterview?.offerProbationMonths }} 个月</span>
          </div>
          <div class="offer-info-item">
            <i class="fas fa-calendar-check"></i>
            <span class="label">接受期限</span>
            <span class="value">{{ formatDate(selectedInterview?.offerAcceptDeadline) }}</span>
          </div>
        </div>
      </div>

      <div class="detail-section" v-if="selectedInterview?.status === -1">
        <h4><i class="fas fa-times-circle"></i> 不通过原因</h4>
        <div class="reject-reason-container">
          <i class="fas fa-comment-alt"></i>
          <p class="reject-reason">{{ selectedInterview?.rejectReason }}</p>
        </div>
      </div>
    </div>
  </el-dialog>

  <!-- 评价弹窗 -->
  <el-dialog
    v-model="showEvaluationModal"
    center
    width="800px"
    :close-on-click-modal="false"
    class="evaluation-dialog"
  >
  <template #title>
    <span class="custom-title">面试评价</span>
  </template>

    <div class="evaluation-content">
      <div class="candidate-info">
        <div class="info-grid">
          <div class="info-item">     
            <i class="fas fa-user"></i>
            <span class="label">应聘者</span>
            <span class="value">{{ selectedInterview?.studentName }}</span>
          </div>
          <div class="info-item">
            <i class="fas fa-briefcase"></i>
            <span class="label">应聘岗位</span>
            <span class="value">{{ selectedInterview?.jobTitle }}</span>
          </div>
          <div class="info-item">
            <i class="fas fa-clock"></i>
            <span class="label">面试时间</span>
            <span class="value">{{ formatDateTime(selectedInterview?.interviewTime) }}</span>
          </div>
          <div class="info-item">
            <i class="fas fa-user-tie"></i>
            <span class="label">面试官</span>
            <span class="value">{{ selectedInterview?.interviewer }}</span>
          </div>
        </div>
      </div>

      <div class="evaluation-form">
        <el-form :model="evaluationForm" label-width="100px">
          <div class="evaluation-sections">
            <div class="form-section">
              <h4><i class="fas fa-code"></i> 技术能力评价</h4>
              <el-form-item label="技术评分">
                <el-rate
                  v-model="evaluationForm.technicalScore"
                  :colors="['#CA4499', '#CA4499', '#CA4499']"
                  :texts="['较差', '一般', '良好', '优秀', '极佳']"
                  show-text
                />
              </el-form-item>
              <el-form-item label="技术反馈">
                <el-input
                  v-model="evaluationForm.technicalFeedback"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入技术能力评价内容..."
                />
              </el-form-item>
            </div>

            <div class="form-section">
              <h4><i class="fas fa-user-check"></i> 综合素质评价</h4>
              <el-form-item label="综合评分">
                <el-rate
                  v-model="evaluationForm.comprehensiveScore"
                  :colors="['#CA4499', '#CA4499', '#CA4499']"
                  :texts="['较差', '一般', '良好', '优秀', '极佳']"
                  show-text
                />
              </el-form-item>
              <el-form-item label="综合反馈">
                <el-input
                  v-model="evaluationForm.comprehensiveFeedback"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入综合素质评价内容..."
                />
              </el-form-item>
            </div>
          </div>
        </el-form>
      </div>
    </div>
    <template #footer>
      <div class="result-actions " style="text-align: center;">
        <el-button @click="showEvaluationModal = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluation" :loading="submitting">
          提交评价
        </el-button>
  </div>
    </template>
  </el-dialog>
</template>

<script>
export default {
  name: 'CompanyInterviewManagement',
  data() {
    return {
      startDate: '',
      endDate: '',
      searchKeyword: '',
      loading: false,
      interviews: [],
      selectedInterview: null,
      showResultModal: false,
      showOfferModal: false,
      showRejectModal: false,
      showDetailModal: false,
      showEvaluationModal: false,
      submitting: false,
      offerForm: {
        startDate: '',
        salary: '',
        probationMonths: 3,
        acceptDays: 7,
        remarks: ''
      },
      rejectForm: {
        reason: ''
      },
      evaluationForm: {
        technicalScore: 0,
        comprehensiveScore: 0,
        technicalFeedback: '',
        comprehensiveFeedback: ''
      }
    }
  },
  created() {
    this.fetchInterviews();
  },
  methods: {
    async fetchInterviews() {
      try {
        this.loading = true;
        const companyId = localStorage.getItem('companyId');
        const params = {
          companyId,
          startDate: this.startDate ? `${this.startDate} 00:00:00` : null,
          endDate: this.endDate ? `${this.endDate} 23:59:59` : null,
          keyword: this.searchKeyword
        };
        
        const response = await this.$axios.get('http://localhost:8888/api/interviews', { params });
        this.interviews = response.data.map(interview => ({
          ...interview,
          studentName: interview.application?.student?.name || '-',
          jobTitle: interview.application?.job?.title || '-',
          studentId: interview.application?.studentId,
          jobId: interview.application?.jobId
        }));
      } catch (error) {
        console.error('获取面试列表失败:', error);
        this.$message.error('获取面试列表失败');
      } finally {
        this.loading = false;
      }
    },

    handleSearch() {
      this.fetchInterviews();
    },

    formatDateTime(date) {
      if (!date) return '-';
      return new Date(date).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      });
    },

    getStatusText(status) {
      const statusMap = {
        '0': '待审批',
        '1': '已通过',
        '-1': '未通过'
      };
      return statusMap[status] || '未知';
    },

    async exportInterviews() {
      try {
        const companyId = localStorage.getItem('companyId');
        const response = await this.$axios.get('http://localhost:8888/api/interviews/export', {
          params: {
            companyId,
            startDate: this.startDate ? `${this.startDate} 00:00:00` : null,
            endDate: this.endDate ? `${this.endDate} 23:59:59` : null,
            keyword: this.searchKeyword
          },
          responseType: 'blob'
        });

        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `面试记录${new Date().toLocaleDateString()}.xlsx`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        this.$message.success('导出成功');
      } catch (error) {
        console.error('导出失败:', error);
        this.$message.error('导出失败，请检查后端接口是否正确实现');
      }
    },

    openResultModal(interview) {
      this.selectedInterview = interview;
      this.showResultModal = true;
    },

    async handlePass() {
      this.showResultModal = false;
      this.showOfferModal = true;
    },

    handleFail() {
      this.showResultModal = false;
      this.showRejectModal = true;
    },

    async sendOffer() {
      try {
        this.submitting = true;
        await this.$axios.post(`http://localhost:8888/api/interviews/${this.selectedInterview.id}/offer`, {
          ...this.offerForm,
          applicationId: this.selectedInterview.applicationId
        });
        
        this.$message.success('Offer发送成功');
        this.showOfferModal = false;
        this.fetchInterviews();
      } catch (error) {
        console.error('发送Offer失败:', error);
        this.$message.error('发送Offer失败');
      } finally {
        this.submitting = false;
      }
    },

    async confirmReject() {
      try {
        this.submitting = true;
        await this.$axios.post(`http://localhost:8888/api/interviews/${this.selectedInterview.id}/reject`, {
          reason: this.rejectForm.reason,
          applicationId: this.selectedInterview.applicationId
        });
        
        this.$message.success('已设置为不通过');
        this.showRejectModal = false;
        this.fetchInterviews();
      } catch (error) {
        console.error('操作失败:', error);
        this.$message.error('操作失败');
      } finally {
        this.submitting = false;
      }
    },

    viewResult(interview) {
      this.selectedInterview = interview;
      this.showDetailModal = true;
    },

    getDetailTitle(status) {
      const statusMap = {
        '1': 'Offer详情',
        '-1': '不通过详情',
        '0': '面试详情'
      };
      return statusMap[status] || '详情';
    },

    formatDate(date) {
      if (!date) return '-';
      return new Date(date).toLocaleDateString('zh-CN');
    },

    handleRowClick(interview) {
      // 如果是通过按钮点击查看详情或面试结果，不触发行点击事件
      if (event.target.closest('.result-btn') || event.target.closest('.view-btn')) {
        return;
      }
      
      this.selectedInterview = interview;
      if (!interview.technicalScore && !interview.comprehensiveScore) {
        this.showEvaluationModal = true;
        this.evaluationForm = {
          technicalScore: 0,
          comprehensiveScore: 0,
          technicalFeedback: '',
          comprehensiveFeedback: ''
        };
      } else {
        this.viewResult(interview);
      }
    },

    async submitEvaluation() {
      if (!this.evaluationForm.technicalScore || !this.evaluationForm.comprehensiveScore) {
        this.$message.warning('请完成评分');
        return;
      }

      if (!this.evaluationForm.technicalFeedback || !this.evaluationForm.comprehensiveFeedback) {
        this.$message.warning('请填写评价内容');
        return;
      }

      try {
        this.submitting = true;
        const companyId = localStorage.getItem('companyId');
        const evaluationData = {
          studentId: this.selectedInterview.application.studentId,
          jobId: this.selectedInterview.application.jobId,
          companyId: companyId,
          type: 1,
          technicalScore: this.evaluationForm.technicalScore,
          comprehensiveScore: this.evaluationForm.comprehensiveScore,
          technicalFeedback: this.evaluationForm.technicalFeedback,
          comprehensiveFeedback: this.evaluationForm.comprehensiveFeedback,
          evaluator: this.selectedInterview.interviewer
        };

        await this.$axios.post(
          `http://localhost:8888/api/evaluations?applicationId=${this.selectedInterview.applicationId}`,
          evaluationData
        );

        this.$message.success('评价提交成功');
        this.showEvaluationModal = false;
        this.fetchInterviews();
      } catch (error) {
        console.error('提交评价失败:', error);
        this.$message.error('提交评价失败');
      } finally {
        this.submitting = false;
      }
    }
  }
}
</script>

<style scoped>
.interview-management {
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
  transition: all 0.3s;
}

.search-btn:hover {
  background: #b53c88;
  transform: translateY(-1px);
}

.export-btn {
  padding: 8px 20px;
  background: #CA4499;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.export-btn:hover {
  background: #b53c88;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(202, 68, 153, 0.3);
}

.content-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: grid;
  grid-template-columns: 40px 1fr 1.5fr 1.5fr 1fr 1fr 1fr 1fr 80px;
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

.interview-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.interview-item {
  display: grid;
  grid-template-columns: 40px 1fr 1.5fr 1.5fr 1fr 1fr 1fr 1fr 80px;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 16px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: all 0.3s;
  cursor: pointer;
}

.interview-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background: rgba(218, 116, 216, 0.1);
}

.item-cell {
  font-size: 14px;
  color: #333;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.score {
  font-weight: 500;
  color: #1d1d1f;
  margin-left: 20px; /* 向右移动 10px */
}

.score-stars {
  color: #CA4499;
  font-size: 12px;
}

.status-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending {
  background: rgba(202, 68, 153, 0.1);
  color: #CA4499;
}

.status-completed {
  background: rgba(52, 199, 89, 0.1);
  color: #34c759;
}

.status-rejected {
  background: rgba(255, 59, 48, 0.1);
  color: #ff3b30;
}

.evaluate-btn, .view-btn {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.evaluate-btn {
  background: #CA4499;
  color: white;
}

.evaluate-btn:hover {
  background: #b53c88;
  transform: translateY(-1px);
}

.view-btn {
  background: rgba(0, 0, 0, 0.05);
  color: #1d1d1f;
  width: 100px;
  margin-left: -10px; /* 向左移动 10px */
}

.view-btn:hover {
  background: rgba(113, 23, 111, 0.1);
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #CA4499;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.evaluation-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.custom-title {
  font-size: 22px;
}

.evaluation-content {
  padding: 24px;
  margin-bottom: 0;
  background: #fff;
}

.candidate-info {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #666;
  width: 100px;
}

.value {
  color: #1d1d1f;
  font-weight: 500;
}

.evaluation-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h4 {
  margin: 0;
  color: #1d1d1f;
  font-size: 16px;
  font-weight: 500;
}

.evaluation-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin: 16px;
  overflow: hidden;
}

.detail-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-section h4::before {
  content: '\f007';
  font-family: 'Font Awesome 5 Free';
  color: #CA4499;
}

.evaluation-grid {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-top: 16px;
}

.feedback-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-top: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.feedback-content {
  color: #1d1d1f;
  line-height: 1.6;
  margin-top: 12px;
  white-space: pre-wrap;
}

.result-tag {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.result-tag.pass {
  background: rgba(52, 199, 89, 0.1);
  color: #34c759;
}

.result-tag.fail {
  background: rgba(255, 59, 48, 0.1);
  color: #ff3b30;
}

/* 覆盖Element Plus的一些样式 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  text-align: center;
  margin: 0;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(to bottom, #fff, #f8f9fa);
}

:deep(.el-dialog__title) {
  font-size: 26px;
  font-weight: 600;
  text-align: center;
  color: #1d1d1f;
  position: relative;
}

:deep(.el-dialog__title)::after {
  content: '';
  position: absolute;
  bottom: -24px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: #CA4499;
  border-radius: 2px;
}

:deep(.el-dialog__body) {
  padding: 0;
  max-height: calc(90vh - 150px);
  overflow-y: auto;
}

:deep(.el-dialog__footer) {
  padding: 0;
  margin: 0;
  background: #fff;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 16px 24px;
}

.form-section h4 {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 0 0 20px;
  color: #CA4499;
  font-size: 16px;
  font-weight: 600;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  text-align: center;
}

.evaluation-dialog :deep(.el-dialog__title),
.result-dialog :deep(.el-dialog__title),
.offer-dialog :deep(.el-dialog__title),
.reject-dialog :deep(.el-dialog__title),
.detail-dialog :deep(.el-dialog__title) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 100%;
  font-size: 30px;
  text-align: center;
}

.evaluation-content,
.result-content,
.offer-content,
.reject-content,
.detail-content {
  padding: 24px;
  margin-bottom: 0;
  background: #fff;
}

.interview-item {
  cursor: pointer;
}

.result-btn {
  padding: 6px 16px;
  background: #CA4499;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  width: 100px;
  margin-left: -10px; /* 向左移动 10px */
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.result-btn:hover {
  background: #b53c88;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(202, 68, 153, 0.2);
}

.result-dialog,
.offer-dialog,
.reject-dialog,
.detail-dialog :deep(.el-dialog) {
  border-radius: 20px !important;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0,0,0,0.15) !important;
}

.detail-content {
  padding: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  border-radius: 12px;
  background: #f8f9fa;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.info-item i {
  color: #CA4499;
  font-size: 16px;
  width: 20px;
  text-align: center;
  flex-shrink: 0;
}

.info-item .label {
  color: #86868b;
  font-size: 13px;
  min-width: 60px;
  flex-shrink: 0;
}

.info-item .value {
  color: #1d1d1f;
  font-size: 14px;
  font-weight: 500;
  flex: 1;
}

.input-with-icon {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.input-with-icon i {
  color: #CA4499;
  font-size: 16px;
  width: 20px;
  text-align: center;
  flex-shrink: 0;
}

.input-with-icon .el-input,
.input-with-icon .el-date-editor,
.input-with-icon .el-input-number {
  flex: 1;
}

.input-with-icon.textarea {
  align-items: flex-start;
}

.input-with-icon.textarea i {
  margin-top: 12px;
}

.input-with-icon.textarea .el-textarea {
  flex: 1;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-item-half {
  flex: 1;
  min-width: 0;
}

:deep(.el-dialog) {
  border-radius: 24px !important;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0,0,0,0.15);
}

:deep(.el-dialog__body) {
  padding: 24px;
  max-height: calc(90vh - 150px);
  overflow-y: auto;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-textarea__inner) {
  min-height: 80px;
}

.offer-form {
  padding: 20px 0;
}

.offer-form .el-form-item {
  margin-bottom: 20px;
}

.offer-form .el-form-item:last-child {
  margin-bottom: 0;
}

.offer-form .el-form-item__label {
  text-align: left;
  padding-right: 12px;
  font-weight: 500;
  color: #1d1d1f;
  line-height: 32px;
}

.offer-form .el-form-item__content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.offer-form .form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.offer-form .form-item-half {
  flex: 1;
  min-width: 0;
}

.offer-form .unit {
  margin-left: 8px;
  color: #86868b;
  font-size: 14px;
  flex-shrink: 0;
}

:deep(.el-form-item__content) {
  margin-left: 0 !important;
}

:deep(.el-input-number) {
  width: 120px;
}

:deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-textarea__inner) {
  min-height: 80px;
}

.offer-dialog :deep(.el-dialog) {
  border-radius: 24px !important;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0,0,0,0.15);
}

.offer-dialog :deep(.el-form-item__label) {
  text-align: left !important;
  padding-right: 12px !important;
  font-weight: 500 !important;
  color: #1d1d1f !important;
  line-height: 36px !important; /* 固定行高 */
  margin-bottom: 0 !important;
}

.offer-dialog .custom-form-item {
  margin-bottom: 20px !important;
}

.offer-dialog .custom-form-item:last-child {
  margin-bottom: 0 !important;
}

.offer-dialog :deep(.el-form-item__content) {
  line-height: 36px !important; /* 与标签保持一致 */
  margin-left: 0 !important;
}

.offer-dialog .form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 0;
}

.offer-dialog .form-row .el-form-item {
  flex: 1;
  min-width: 0;
  width: 50%;
  box-sizing: border-box;
}

.offer-dialog .input-with-icon {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.offer-dialog .input-with-icon i {
  color: #CA4499;
  font-size: 16px;
  width: 20px;
  text-align: center;
  flex-shrink: 0;
  position: relative;
  top: 1px; /* 微调图标位置 */
  vertical-align: middle;
}

.offer-dialog .input-with-icon .el-input,
.offer-dialog .input-with-icon .el-date-editor {
  flex: 1;
  width: 100% !important;
}

.offer-dialog .custom-number-input {
  width: 120px !important;
}

.offer-dialog .unit {
  margin-left: 8px;
  color: #86868b;
  font-size: 14px;
  flex-shrink: 0;
}

.offer-dialog .input-with-icon.textarea {
  flex-direction: column;
  align-items: flex-start;
}

.offer-dialog .input-with-icon.textarea i {
  margin-bottom: 8px;
  margin-top: 4px;
}

.offer-dialog .input-with-icon.textarea .el-textarea {
  width: 100%;
}

.offer-dialog-footer {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 16px 0;
}

.offer-dialog-footer .cancel-btn {
  min-width: 100px;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 500;
  border-color: #999999; /* 提高对比度 */
  transition: all 0.3s;
}

.offer-dialog-footer .submit-btn {
  min-width: 100px;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 500;
  background-color: #CA4499 !important;
  border-color: #CA4499 !important;
  transition: all 0.3s;
}

.offer-dialog-footer .submit-btn:hover {
  background-color: #b53c88 !important;
  border-color: #b53c88 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(202,68,153,0.2);
}

.offer-dialog-footer .cancel-btn:hover {
  border-color: #CA4499;
  color: #CA4499;
  transform: translateY(-1px);
}

.offer-dialog :deep(.el-input__inner),
.offer-dialog :deep(.el-textarea__inner) {
  border: 1.5px solid #CCCCCC !important;
  border-radius: 8px !important;
}

.offer-dialog :deep(.el-input-number__decrease),
.offer-dialog :deep(.el-input-number__increase) {
  width: 32px !important;
  justify-content: center !important;
  align-items: center !important;
  display: flex !important;
}

.offer-dialog :deep(.el-input-number__decrease i),
.offer-dialog :deep(.el-input-number__increase i) {
  position: relative;
  top: 1px;
  font-weight: bold;
}

.offer-dialog :deep(.el-textarea__inner) {
  min-height: 80px !important;
}

.offer-dialog :deep(.el-input__placeholder),
.offer-dialog :deep(.el-textarea__placeholder) {
  color: rgba(0, 0, 0, 0.35) !important;
  transition: opacity 0.2s;
}

.result-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 24px;
}

.result-actions .el-button {
  min-width: 120px;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 500;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.result-actions .el-button i {
  font-size: 16px;
}

.result-actions .el-button--primary {
  background: #CA4499!important;
  border-color: #CA4499!important;
}

.result-actions .el-button--primary:hover {
  background: #b53c88!important;
  transform: translateY(-1px)!important;
  box-shadow: 0 4px 12px rgba(202,68,153,0.2)!important;
}

.result-actions .el-button--default:hover {
  border-color: #CA4499!important;
  color: #CA4499!important;
  transform: translateY(-1px)!important;
}

/* Offer弹窗完全重新设计的CSS */
.offer-dialog :deep(.el-dialog) {
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0,0,0,0.15);
}

.offer-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
  margin: 0;
}

.offer-dialog :deep(.el-dialog__title) {
  font-size: 22px;
  font-weight: 600;
  color: #1d1d1f;
}

.offer-dialog :deep(.el-dialog__body) {
  padding: 30px;
  max-height: calc(90vh - 150px);
  overflow-y: auto;
}

.offer-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.offer-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.offer-info-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8f9fa;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.offer-info-item i {
  color: #CA4499;
  font-size: 16px;
  width: 20px;
  text-align: center;
  margin-right: 12px;
}

.offer-label {
  color: #86868b;
  font-size: 14px;
  min-width: 70px;
}

.offer-value {
  color: #1d1d1f;
  font-size: 15px;
  font-weight: 500;
  flex: 1;
}

.offer-form-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 0 6px;
}

.offer-form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.offer-form-row .full-width {
  grid-column: span 2;
}

.offer-form-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.offer-form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1d1d1f;
  font-weight: 500;
  font-size: 15px;
}

.offer-form-label i {
  color: #CA4499;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.offer-input {
  width: 100%;
}

.offer-input-with-unit {
  display: flex;
  align-items: center;
  gap: 12px;
}

.offer-number-input {
  width: 140px !important;
}

.offer-unit {
  color: #86868b;
  font-size: 15px;
}

.offer-textarea {
  width: 100%;
}

.offer-dialog :deep(.el-input__inner),
.offer-dialog :deep(.el-textarea__inner) {
  border: 1.5px solid #CCCCCC;
  border-radius: 10px;
  padding: 12px;
  font-size: 15px;
}

.offer-dialog :deep(.el-textarea__inner) {
  min-height: 100px;
}

.offer-dialog :deep(.el-input-number__decrease),
.offer-dialog :deep(.el-input-number__increase) {
  width: 32px;
  justify-content: center;
  align-items: center;
  display: flex;
  border-color: #CCCCCC;
}

.offer-dialog :deep(.el-input-number__decrease i),
.offer-dialog :deep(.el-input-number__increase i) {
  position: relative;
  top: 1px;
  font-weight: bold;
}

.offer-dialog :deep(.el-input__placeholder),
.offer-dialog :deep(.el-textarea__placeholder) {
  color: rgba(0, 0, 0, 0.35);
  transition: opacity 0.2s;
}

.offer-dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 10px 0 20px;
}

.offer-cancel-btn,
.offer-submit-btn {
  min-width: 120px;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 500;
  font-size: 15px;
  transition: all 0.3s;
}

.offer-cancel-btn {
  border-color: #999999;
}

.offer-cancel-btn:hover {
  border-color: #CA4499;
  color: #CA4499;
  transform: translateY(-1px);
}

.offer-submit-btn {
  background-color: #CA4499 !important;
  border-color: #CA4499 !important;
}

.offer-submit-btn:hover {
  background-color: #b53c88 !important;
  border-color: #b53c88 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(202,68,153,0.2);
}

/* 不通过弹窗的CSS */
.reject-dialog :deep(.el-dialog) {
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0,0,0,0.15);
}

.reject-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
  margin: 0;
}

.reject-dialog :deep(.el-dialog__title) {
  font-size: 22px;
  font-weight: 600;
  color: #1d1d1f;
}

.reject-dialog :deep(.el-dialog__body) {
  padding: 30px;
  max-height: calc(90vh - 150px);
  overflow-y: auto;
}

.reject-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.reject-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.reject-info-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8f9fa;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.reject-info-item i {
  color: #CA4499;
  font-size: 16px;
  width: 20px;
  text-align: center;
  margin-right: 12px;
}

.reject-label {
  color: #86868b;
  font-size: 14px;
  min-width: 70px;
}

.reject-value {
  color: #1d1d1f;
  font-size: 15px;
  font-weight: 500;
  flex: 1;
}

.reject-form-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 0 6px;
}

.reject-form-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reject-form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1d1d1f;
  font-weight: 500;
  font-size: 15px;
}

.reject-form-label i {
  color: #ff3b30;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.reject-textarea {
  width: 100%;
}

.reject-dialog :deep(.el-textarea__inner) {
  border: 1.5px solid #CCCCCC;
  border-radius: 10px;
  padding: 12px;
  font-size: 15px;
  min-height: 120px;
}

.reject-dialog :deep(.el-input__placeholder),
.reject-dialog :deep(.el-textarea__placeholder) {
  color: rgba(0, 0, 0, 0.35);
  transition: opacity 0.2s;
}

.reject-dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 10px 0 20px;
}

.reject-cancel-btn,
.reject-submit-btn {
  min-width: 120px;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 500;
  font-size: 15px;
  transition: all 0.3s;
}

.reject-cancel-btn {
  border-color: #999999;
}

.reject-cancel-btn:hover {
  border-color: #CA4499;
  color: #CA4499;
  transform: translateY(-1px);
}

.reject-submit-btn {
  background-color: #CA4499 !important;
  border-color: #CA4499 !important;
}

.reject-submit-btn:hover {
  background-color: #b53c88 !important;
  border-color: #b53c88 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(202,68,153,0.2);
}
</style> 