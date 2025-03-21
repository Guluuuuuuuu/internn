<template>
  <div class="attendance-calendar">
    <div class="calendar-header">
      <div class="month-selector">
        <button class="month-btn" @click="previousMonth">
          <i class="fas fa-chevron-left"></i>
        </button>
        <span class="current-month">{{ currentMonthText }}</span>
        <button class="month-btn" @click="nextMonth">
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
      <button class="export-btn" @click="exportAttendance">
        <i class="fas fa-download"></i> 导出记录
      </button>
    </div>

    <div class="calendar-grid">
      <div class="weekday-header" v-for="day in weekDays" :key="day">{{ day }}</div>
      <div
        v-for="(day, index) in calendarDays"
        :key="index"
        class="calendar-day"
        :class="{
          'current-day': isCurrentDay(day),
          'worked-day': isWorkedDay(day),
          'scheduled-day': isScheduledDay(day),
          'other-month': !isSameMonth(day)
        }"
        :style="getDayStyle(day)"
        @click="showDayDetails(day)"
      >
        <span class="day-number" :style="getDayNumberStyle(day)">{{ day.getDate() }}</span>
        <span v-if="isScheduledDay(day)" class="schedule-dot"></span>
      </div>
    </div>

    <!-- 状态调试信息 -->
    <div class="debug-info" v-if="false">
      <h4>日历状态调试</h4>
      <p>考勤记录数量: {{ attendanceRecords.length }}</p>
      <p>当前月份: {{ currentDate.getFullYear() }}年{{ currentDate.getMonth() + 1 }}月</p>
      <div v-for="record in attendanceRecords" :key="record.id">
        <p>{{ record.workDate }} - 状态: {{ record.status === 1 ? '已上班' : '预排班' }}</p>
      </div>
    </div>

    <!-- 打卡详情弹窗 -->
    <div class="detail-overlay" v-if="showDetails">
      <div 
        class="detail-modal" 
        ref="detailModalContent" 
        :style="detailModalStyle" 
        @mousedown="startDetailDrag($event)"
      >
        <div class="detail-header">
          <div class="drag-hint">
            <i class="fas fa-grip-lines"></i>
          </div>
          <h3>{{ selectedDate ? formatDateToYYYYMMDD(selectedDate) : '' }}</h3>
          <button @click="closeDetails" class="close-btn" title="关闭">&times;</button>
        </div>
        <div class="detail-body">
          <div class="detail-item">
            <div class="icon-wrapper">
              <i class="fas fa-sign-in-alt"></i>
            </div>
            <div class="content">
              <div class="label">签到时间</div>
              <div class="value">{{ selectedDayDetails.checkInTime || '暂无签到' }}</div>
            </div>
          </div>
          <div class="detail-item">
            <div class="icon-wrapper">
              <i class="fas fa-sign-out-alt"></i>
            </div>
            <div class="content">
              <div class="label">签退时间</div>
              <div class="value">{{ selectedDayDetails.checkOutTime || '暂无签退' }}</div>
            </div>
          </div>
          <div class="detail-item">
            <div class="icon-wrapper">
              <i class="fas fa-clock"></i>
            </div>
            <div class="content">
              <div class="label">工作时长</div>
              <div class="value">{{ selectedDayDetails.workHours || '0' }} 小时</div>
            </div>
          </div>
          <div class="detail-item">
            <div class="icon-wrapper">
              <i class="fas fa-comment-alt"></i>
            </div>
            <div class="content">
              <div class="label">备注</div>
              <div class="value">{{ selectedDayDetails.remarks || '无' }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <system-message ref="messageRef"></system-message>
  </div>
</template>

<script>
import SystemMessage from '../common/SystemMessage.vue'

export default {
  name: 'AttendanceCalendar',
  components: {
    SystemMessage
  },
  props: {
    studentId: {
      type: Number,
      required: true
    },
    companyId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      currentDate: new Date(),
      weekDays: ['日', '一', '二', '三', '四', '五', '六'],
      attendanceRecords: [],
      showDetails: false,
      selectedDate: null,
      selectedDayDetails: {},
      detailModalStyle: {
        right: '20px',
        top: '50%',
        transform: 'translateY(-50%)'
      },
      detailDragging: false,
      detailDragOffset: { x: 0, y: 0 }
    }
  },
  computed: {
    currentMonthText() {
      return this.currentDate.toLocaleString('zh-CN', { year: 'numeric', month: 'long' });
    },
    selectedDateText() {
      if (!this.selectedDate) return '';
      return this.selectedDate.toLocaleString('zh-CN', { month: 'long', day: 'numeric', weekday: 'long' });
    },
    calendarDays() {
      const year = this.currentDate.getFullYear();
      const month = this.currentDate.getMonth();
      const firstDay = new Date(year, month, 1);
      const lastDay = new Date(year, month + 1, 0);
      
      const days = [];
      
      // 添加上月的日期
      for (let i = firstDay.getDay(); i > 0; i--) {
        days.push(new Date(year, month, 1 - i));
      }
      
      // 添加当月的日期
      for (let i = 1; i <= lastDay.getDate(); i++) {
        days.push(new Date(year, month, i));
      }
      
      // 添加下月的日期以填满日历网格
      const remainingDays = 42 - days.length;
      for (let i = 1; i <= remainingDays; i++) {
        days.push(new Date(year, month + 1, i));
      }
      
      return days;
    }
  },
  methods: {
    showMessage(options) {
      this.$refs.messageRef.show(options);
    },
    async fetchAttendanceRecords() {
      try {
        // 检查必需的参数是否存在
        if (!this.studentId || !this.companyId) {
          console.warn('缺少必需参数：', { studentId: this.studentId, companyId: this.companyId });
          return;
        }
        
        const apiUrl = `http://localhost:8888/api/attendance?studentId=${this.studentId}&companyId=${this.companyId}&year=${this.currentDate.getFullYear()}&month=${this.currentDate.getMonth() + 1}`;
        console.log(`获取考勤记录: ${apiUrl}`);
        
        const response = await fetch(apiUrl);
        if (!response.ok) throw new Error('获取考勤记录失败');
        
        this.attendanceRecords = await response.json();
        console.log(`获取到${this.attendanceRecords.length}条考勤记录`);
        
        if (this.attendanceRecords.length > 0) {
          // 打印前三条记录样例
          console.log('考勤记录样例:', this.attendanceRecords.slice(0, 3));
        }
      } catch (error) {
        console.error('获取考勤记录失败:', error);
      }
    },
    previousMonth() {
      this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1);
      this.fetchAttendanceRecords();
    },
    nextMonth() {
      this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1);
      this.fetchAttendanceRecords();
    },
    isCurrentDay(date) {
      const today = new Date();
      return date.getDate() === today.getDate() &&
             date.getMonth() === today.getMonth() &&
             date.getFullYear() === today.getFullYear();
    },
    isSameMonth(date) {
      return date.getMonth() === this.currentDate.getMonth();
    },
    isWorkedDay(date) {
      // 跳过不是当前月份的日期，减少不必要的计算
      if (!this.isSameMonth(date)) return false;
      
      // 将日期对象转换为 YYYY-MM-DD 格式字符串
      const formatDate = date => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      };
      
      const dateStr = formatDate(date);
      
      for (const record of this.attendanceRecords) {
        if (record.workDate === dateStr && record.status === 1) {
          return true;
        }
      }
      
      return false;
    },
    isScheduledDay(date) {
      // 跳过不是当前月份的日期，减少不必要的计算
      if (!this.isSameMonth(date)) return false;
      
      // 将日期对象转换为 YYYY-MM-DD 格式字符串
      const formatDate = date => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      };
      
      const dateStr = formatDate(date);
      
      for (const record of this.attendanceRecords) {
        if (record.workDate === dateStr && record.status === 0) {
          return true;
        }
      }
      
      return false;
    },
    getWorkHours(date) {
      // 将日期对象转换为 YYYY-MM-DD 格式字符串
      const formatDate = date => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      };
      
      const dateStr = formatDate(date);
      const record = this.attendanceRecords.find(record => record.workDate === dateStr);
      return record && record.workHours ? record.workHours : 0;
    },
    showDayDetails(date) {
      // 将日期对象转换为 YYYY-MM-DD 格式字符串
      const formatDate = date => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      };
      
      const dateStr = formatDate(date);
      const record = this.attendanceRecords.find(record => record.workDate === dateStr);
      
      if (record) {
        this.selectedDate = date;
        this.selectedDayDetails = {
          checkInTime: record.checkInTime ? new Date(record.checkInTime).toLocaleTimeString('zh-CN', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
          }) : '暂无签到',
          checkOutTime: record.checkOutTime ? new Date(record.checkOutTime).toLocaleTimeString('zh-CN', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
          }) : '暂无签退',
          workHours: record.workHours ? record.workHours.toFixed(1) : 0,
          remarks: record.remarks || '无'
        };
        
        // 设置弹窗位置在右侧
        this.detailModalStyle = {
          right: '20px',
          top: '50%',
          transform: 'translateY(-50%)'
        };
        
        this.showDetails = true;
      }
    },
    closeDetails() {
      this.showDetails = false;
      this.selectedDayDetails = {};
    },
    async exportAttendance() {
      try {
        const response = await fetch(
          `http://localhost:8888/api/attendance/export?studentId=${this.studentId}&companyId=${this.companyId}`,
          { method: 'GET' }
        );
        
        if (!response.ok) throw new Error('导出失败');
        
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = '考勤记录.xlsx';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        this.showMessage({
          message: '导出成功',
          type: 'success',
          duration: 2000
        });
      } catch (error) {
        console.error('导出考勤记录失败:', error);
        this.showMessage({
          message: '导出失败',
          type: 'error',
          duration: 2000
        });
      }
    },
    getDayStyle(day) {
      // 使用内联样式确保样式优先级
      let style = { background: 'white' };
      
      // 当前日期使用品牌色背景
      if (this.isCurrentDay(day)) {
        style.background = '#CA4499';
      }
      
      return style;
    },
    getDayNumberStyle(day) {
      // 使用内联样式确保样式优先级
      let style = { 
        color: '#CCCCCC',  // 默认未上班日期使用灰色字
        fontWeight: 'normal' 
      };
      
      // 已上班日期文字使用品牌色
      if (this.isWorkedDay(day)) {
        style.color = '#CA4499';
      }
      
      // 当前日期文字为白色加粗
      if (this.isCurrentDay(day)) {
        style.color = 'white';
        style.fontWeight = 'bold';
      }
      
      return style;
    },
    startDetailDrag(event) {
      // 如果点击的是关闭按钮，不启动拖动
      if (event.target.closest('.close-btn')) return;
      
      // 阻止事件冒泡，避免触发考勤弹窗的拖动
      event.stopPropagation();
      
      this.detailDragging = true;
      const rect = this.$refs.detailModalContent.getBoundingClientRect();
      this.detailDragOffset = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
      };
      
      // 添加移动和停止拖动的事件监听器
      document.addEventListener('mousemove', this.doDetailDrag);
      document.addEventListener('mouseup', this.stopDetailDrag);
      
      // 添加拖动样式
      if (this.$refs.detailModalContent) {
        this.$refs.detailModalContent.classList.add('dragging');
      }
    },
    doDetailDrag(event) {
      if (!this.detailDragging) return;
      
      // 计算新位置
      const x = event.clientX - this.detailDragOffset.x;
      const y = event.clientY - this.detailDragOffset.y;
      
      // 获取窗口尺寸
      const windowWidth = window.innerWidth;
      const windowHeight = window.innerHeight;
      
      // 获取弹窗尺寸
      const modalWidth = this.$refs.detailModalContent.offsetWidth;
      const modalHeight = this.$refs.detailModalContent.offsetHeight;
      
      // 限制弹窗在视口内
      const maxX = windowWidth - modalWidth - 10;
      const maxY = windowHeight - modalHeight - 10;
      
      // 更新弹窗位置，确保在视口边界内
      this.detailModalStyle = {
        position: 'fixed',
        top: Math.max(10, Math.min(maxY, y)) + 'px',
        transform: 'none'
      };
      
      // 如果拖到了窗口左侧区域，使用左侧定位
      if (x < windowWidth / 2) {
        this.detailModalStyle.left = Math.max(10, Math.min(maxX, x)) + 'px';
        this.detailModalStyle.right = 'auto';
      } else {
        // 否则使用右侧定位
        this.detailModalStyle.right = Math.max(10, Math.min(maxX, windowWidth - x - modalWidth)) + 'px';
        this.detailModalStyle.left = 'auto';
      }
    },
    stopDetailDrag() {
      this.detailDragging = false;
      
      // 移除事件监听器
      document.removeEventListener('mousemove', this.doDetailDrag);
      document.removeEventListener('mouseup', this.stopDetailDrag);
      
      // 移除拖动样式
      if (this.$refs.detailModalContent) {
        this.$refs.detailModalContent.classList.remove('dragging');
      }
    },
    formatDateToYYYYMMDD(date) {
      // 使用本地时区格式化日期，避免时区偏移问题
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  },
  beforeUnmount() {
    // 确保在组件销毁前移除所有事件监听器
    document.removeEventListener('mousemove', this.doDetailDrag);
    document.removeEventListener('mouseup', this.stopDetailDrag);
  },
  mounted() {
    console.log('AttendanceCalendar组件已挂载，Props:', { studentId: this.studentId, companyId: this.companyId });
    // 仅在参数完整时请求数据
    if (this.studentId && this.companyId) {
      this.fetchAttendanceRecords();
    } else {
      console.warn('参数不完整，无法获取考勤记录');
    }
  }
}
</script>

<style scoped>
.attendance-calendar {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 360px;
  margin: 0 auto;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.month-selector {
  display: flex;
  align-items: center;
  gap: 16px;
}

.month-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.month-btn:hover {
  background: #f5f5f5;
}

.current-month {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.export-btn {
  padding: 6px 12px;
  background: #CA4499;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.export-btn:hover {
  background: #b33b87;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}

.weekday-header {
  text-align: center;
  padding: 6px;
  font-weight: 500;
  color: #666;
  font-size: 14px;
}

.calendar-day {
  aspect-ratio: 1;
  padding: 8px;
  border-radius: 8px;
  background: white;  /* 默认白色背景 */
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.calendar-day:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.day-number {
  font-size: 16px;
  color: #CCCCCC !important;  /* 未上班日期使用灰色字 */
  font-weight: 500;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* 已上班日期样式 */
.worked-day {
  background: white !important;  /* 已上班日期使用白色背景 */
  font-weight: bold !important;
}

.worked-day .day-number {
  color: #CA4499 !important;  /* 已上班日期文字使用品牌色 */
}

/* 当前日期样式 */
.current-day {
  background-color: #CA4499 !important;  /* 当前日期使用品牌色背景 */
}

.current-day .day-number {
  color: white !important;  /* 当前日期文字为白色 */
  font-weight: bold !important;
}

/* 预排班日期样式 */
.scheduled-day .schedule-dot {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 6px;
  height: 6px;
  background-color: #CA4499;
  border-radius: 50%;
  display: block;
  z-index: 2;
}

.other-month {
  opacity: 0.5;
}


/* 调试信息样式 */
.debug-info {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #f9f9f9;
  font-size: 12px;
}

/* 打卡详情弹窗样式 */
.detail-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2000;
  pointer-events: none;
}

.detail-modal {
  position: fixed;
  width: 280px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.12), 0 0 1px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  pointer-events: auto;
  cursor: grab;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  transition: box-shadow 0.3s ease, transform 0.05s ease;
  animation: detail-slide-in 0.3s forwards;
  z-index: 2100; /* 确保在最上层 */
  user-select: none; /* 防止拖动时选择文本 */
}

.detail-modal:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f5f7;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  cursor: grab;
}

.detail-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  flex: 1;
  text-align: center;
}

.drag-hint {
  font-size: 12px;
  color: #86868b;
  opacity: 0.5;
  transition: opacity 0.3s;
}

.detail-header:hover .drag-hint {
  opacity: 1;
}

.close-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #86868b;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.close-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #ff3b30;
}

.detail-body {
  padding: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  background: #f5f5f7;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.detail-item:hover {
  background: #efeff1;
  transform: translateY(-1px);
}

.icon-wrapper {
  width: 28px;
  height: 28px;
  min-width: 28px;
  border-radius: 50%;
  background: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 12px;
  transition: all 0.2s ease;
}

.detail-item:nth-child(1) .icon-wrapper {
  background: rgba(52, 199, 89, 0.1);
  color: #34C759;
}

.detail-item:nth-child(2) .icon-wrapper {
  background: rgba(255, 149, 0, 0.1);
  color: #FF9500;
}

.detail-item:nth-child(3) .icon-wrapper {
  background: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.detail-item:nth-child(4) .icon-wrapper {
  background: rgba(175, 82, 222, 0.1);
  color: #AF52DE;
}

.content {
  flex: 1;
}

.content .label {
  font-size: 12px;
  color: #86868b;
  margin-bottom: 2px;
}

.content .value {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

.detail-item:last-child {
  margin-bottom: 0;
}

@keyframes detail-slide-in {
  from {
    opacity: 0;
    transform: translateY(-50%) translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateY(-50%) translateX(0);
  }
}

.detail-modal.dragging {
  cursor: grabbing !important;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  transform: translateY(-50%) scale(1.01);
}
</style> 