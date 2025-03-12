<template>
    <div class="main-content">
      <div class="header">
        <h2>个人中心</h2>
        <div class="header-right">
          <span class="welcome">欢迎回来，{{ studentInfo.name }}</span>
        </div>
      </div>
  
      <div class="dashboard-container">
        <!-- 左栏 -->
        <div class="left-column">
          <div class="chart-card">
            <h3><i class="fas fa-chart-pie"></i> 实习进度概览</h3>
            <div ref="pieChart" class="pie-chart"></div>
          </div>
          <div class="chart-card">
            <h3><i class="fas fa-chart-line"></i> 投递进度追踪</h3>
            <div ref="trendChart" class="trend-chart"></div>
          </div>
        </div>
  
        <!-- 右栏 -->
        <div class="right-column">
          <div class="stat-summary">
            <div class="stat-item" @click="showDetail('time')">
              <div class="stat-icon">
                <i class="fas fa-clock"></i>
              </div>
              <div class="stat-content">
                <h4>实习时长</h4>
                <div class="stat-number">
                  <span class="number">120</span>
                  <span class="unit">小时</span>
                </div>
              </div>
            </div>
  
            <div class="stat-item" @click="showDetail('reports')">
              <div class="stat-icon">
                <i class="fas fa-file-alt"></i>
              </div>
              <div class="stat-content">
                <h4>实习报告</h4>
                <div class="stat-number">
                  <span class="number">8</span>
                  <span class="trend up">↑ 2</span>
                </div>
              </div>
            </div>
  
            <div class="stat-item" @click="showDetail('applications')">
              <div class="stat-icon">
                <i class="fas fa-paper-plane"></i>
              </div>
              <div class="stat-content">
                <h4>投递申请</h4>
                <div class="stat-number">
                  <span class="number">15</span>
                  <span class="trend up">↑ 3</span>
                </div>
              </div>
            </div>
  
            <!-- 考勤记录图表 -->
            <div class="rating-chart">
              <h4>考勤记录</h4>
              <div ref="attendanceChart" class="rating-chart-container"></div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 添加背景图容器 -->
      <div class="background-image"></div>
  
      <!-- 添加弹窗组件 -->
      <div class="modal" v-if="showModal" @click.self="closeModal">
        <div class="modal-content">
          <h3>{{ modalTitle }}</h3>
          <div class="modal-body">
            {{ modalContent }}
          </div>
          <button class="modal-close" @click="closeModal">关闭</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import * as echarts from 'echarts'
  
  export default {
    name: 'StudentDashboard',
    data() {
      return {
        studentInfo: JSON.parse(localStorage.getItem('user') || '{}'),
        showModal: false,
        modalTitle: '',
        modalContent: ''
      }
    },
    mounted() {
      this.$nextTick(() => {
        this.initPieChart()
        this.initTrendChart()
        this.initAttendanceChart()
        window.addEventListener('resize', this.resizeCharts)
      })
    },
    beforeUnmount() {
      window.removeEventListener('resize', this.resizeCharts)
    },
    methods: {
      resizeCharts() {
        const charts = ['pieChart', 'trendChart', 'attendanceChart'].map(ref => 
          echarts.init(this.$refs[ref])
        )
        charts.forEach(chart => chart.resize())
      },
      initPieChart() {
        const pieChart = echarts.init(this.$refs.pieChart)
        pieChart.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            right: 10,
            top: 'center',
            textStyle: {
              color: '#666'
            }
          },
          series: [{
            name: '实习进度',
            type: 'pie',
            radius: ['50%', '70%'],
            center: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}\n{d}%',
              color: '#666'
            },
            labelLine: {
              show: true,
              length: 15,
              length2: 10
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            data: [
              { value: 120, name: '已完成', itemStyle: { color: '#4CAF50' } },
              { value: 80, name: '进行中', itemStyle: { color: '#CA4499' } },
              { value: 100, name: '待完成', itemStyle: { color: '#9B55D3' } }
            ]
          }]
        })
      },
      initTrendChart() {
        const trendChart = echarts.init(this.$refs.trendChart)
        trendChart.setOption({
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: ['投递数', '面试数', '通过数'],
            top: 10,
            textStyle: {
              color: '#666'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: ['1月', '2月', '3月', '4月', '5月', '6月'],
            axisLine: {
              lineStyle: { color: '#666' }
            }
          },
          yAxis: {
            type: 'value',
            axisLine: {
              lineStyle: { color: '#666' }
            }
          },
          series: [
            {
              name: '投递数',
              type: 'bar',
              data: [5, 8, 12, 15, 10, 8],
              itemStyle: { color: '#CA4499' }
            },
            {
              name: '面试数',
              type: 'bar',
              data: [3, 5, 8, 10, 6, 5],
              itemStyle: { color: '#9B55D3' }
            },
            {
              name: '通过数',
              type: 'bar',
              data: [1, 2, 4, 6, 3, 2],
              itemStyle: { color: '#E391C3' }
            }
          ]
        })
      },
      initAttendanceChart() {
        const attendanceChart = echarts.init(this.$refs.attendanceChart)
        attendanceChart.setOption({
          tooltip: {
            trigger: 'axis'
          },
          xAxis: {
            type: 'category',
            data: ['周一', '周二', '周三', '周四', '周五'],
            axisLine: {
              lineStyle: { color: '#666' }
            }
          },
          yAxis: {
            type: 'value',
            max: 10,
            axisLine: {
              lineStyle: { color: '#666' }
            }
          },
          series: [{
            data: [8, 9, 8.5, 9, 8],
            type: 'line',
            smooth: true,
            itemStyle: { color: '#CA4499' },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0,
                  color: 'rgba(202, 68, 153, 0.3)'
                }, {
                  offset: 1,
                  color: 'rgba(202, 68, 153, 0.1)'
                }]
              }
            }
          }]
        })
      },
      showDetail(type) {
        this.modalTitle = type === 'time' ? '实习时长详情' :
                         type === 'reports' ? '实习报告详情' : '投递申请详情'
        this.modalContent = '详细内容...'
        this.showModal = true
      },
      closeModal() {
        this.showModal = false
      }
    }
  }
  </script>
  
  <style scoped>
  .main-content {
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
    margin-bottom: 30px;
  }
  
  .welcome {
    color: #666;
  }
  
  .dashboard-container {
    display: flex;
    gap: 25px;
    margin-top: 25px;
  }
  
  .left-column {
    flex: 15;
    display: flex;
    flex-direction: column;
  }
  
  .right-column {
    flex: 5;
  }
  
  .chart-card {
    background: white;
    border-radius: 20px;
    padding: 25px;
    box-shadow: 0 10px 20px rgba(202, 68, 153, 0.05);
    border: 1px solid rgba(202, 68, 153, 0.1);
    margin-bottom: 25px;
    height: 400px;
  }
  
  .pie-chart, .trend-chart {
    height: 320px;
    width: 100%;
  }
  
  .stat-summary {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .stat-item {
    display: flex;
    align-items: center;
    padding: 15px;
    border-radius: 8px;
    background: white;
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .stat-item:hover {
    background: rgba(202, 68, 153, 0.1);
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: #CA4499;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
  }
  
  .stat-content {
    flex: 1;
  }
  
  .stat-content h4 {
    margin: 0;
    color: #333;
  }
  
  .stat-number {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .number {
    font-size: 24px;
    font-weight: bold;
  }
  
  .trend {
    font-size: 12px;
    color: #666;
  }
  
  .trend.up {
    color: #4CAF50;
  }
  
  .trend.down {
    color: #F44336;
  }
  
  .rating-chart {
    background: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }
  
  .rating-chart-container {
    height: 200px;
  }
  
  .background-image {
    position: absolute;
    bottom: 20px;
    right: 20px;
    width: 400px;
    height: 400px;
    background-image: url('@/assets/xx.png');
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;
    opacity: 0.1;
    pointer-events: none;
    z-index: 2;
  }
  
  .chart-card h3 i {
    color: #CA4499;
    margin-right: 10px;
  }
  
  .rating-chart h4 {
    display: flex;
    align-items: center;
  }
  
  .rating-chart h4::before {
    content: '\f080';
    font-family: 'Font Awesome 5 Free';
    font-weight: 900;
    margin-right: 10px;
    color: #CA4499;
  }
  
  /* 弹窗样式 */
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
    border-radius: 16px;
    padding: 25px;
    width: 90%;
    max-width: 500px;
    position: relative;
  }
  
  .modal-close {
    position: absolute;
    right: 20px;
    top: 20px;
    background: none;
    border: none;
    color: #666;
    cursor: pointer;
  }
  </style>