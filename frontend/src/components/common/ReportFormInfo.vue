<!-- ReportFormInfo.vue -->
<template>
  <div class="report-form-info">
    <div class="form-group">
      <label>报告标题</label>
      <input type="text" v-model="reportData.title" placeholder="请输入报告标题">
    </div>
    <div class="form-row">
      <div class="form-group">
        <label>公司名称</label>
        <input type="text" v-model="reportData.company" placeholder="请输入公司名称">
      </div>
      <div class="form-group">
        <label>实习岗位</label>
        <input type="text" v-model="reportData.position" placeholder="请输入实习岗位">
      </div>
    </div>
    <div class="form-row">
      <div class="form-group">
        <label>开始日期</label>
        <input type="date" v-model="reportData.startDate">
      </div>
      <div class="form-group">
        <label>结束日期</label>
        <input type="date" v-model="reportData.endDate">
      </div>
    </div>
    <div class="form-group">
      <label>摘要</label>
      <textarea 
        v-model="reportData.summary" 
        placeholder="请简要描述您的实习内容和收获（100字以内）" 
        maxlength="100"
      ></textarea>
      <div class="char-count">{{ reportData.summary ? reportData.summary.length : 0 }}/100</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReportFormInfo',
  props: {
    value: {
      type: Object,
      default: () => ({
        title: '',
        company: '',
        position: '',
        startDate: '',
        endDate: '',
        summary: ''
      })
    }
  },
  computed: {
    reportData: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
      }
    }
  }
}
</script>

<style scoped>
.report-form-info {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 5px;
  color: #333;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-row .form-group {
  flex: 1;
}

input, textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

input:focus, textarea:focus {
  border-color: #CA4499;
  outline: none;
}

textarea {
  resize: vertical;
  min-height: 80px;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style> 