<template>
  <div class="admin-dashboard">
    <!-- 欢迎信息区域 -->
    <div class="welcome-section">
      <h2>总仪表盘</h2>
      <span class="welcome">欢迎回来，{{ adminInfo.name }}</span>
    </div>

    <!-- 主要内容区域 -->
    <div class="dashboard-container">
      <!-- 左栏 -->
      <div class="left-column">
        <div class="chart-card">
          <h3><i class="fas fa-chart-pie"></i> 实习岗位分布</h3>
          <div ref="pieChart" class="pie-chart"></div>
        </div>
        <div class="chart-card">
          <h3><i class="fas fa-chart-line"></i> 实习申请趋势</h3>
          <div ref="trendChart" class="trend-chart"></div>
        </div>
      </div>

      <!-- 右栏 -->
      <div class="right-column">
        <div class="stat-summary">
          <!-- 数据卡片 -->
          <div class="stat-item" @click="showDetail('student')">
            <div class="stat-icon">
              <i class="fas fa-user-graduate"></i>
            </div>
            <div class="stat-content">
              <h4>学生总数</h4>
              <div class="stat-number">
                <span class="number">150</span>
                <span class="trend up">↑ 12%</span>
              </div>
            </div>
          </div>

          <div class="stat-item" @click="showDetail('company')">
            <div class="stat-icon">
              <i class="fas fa-building"></i>
            </div>
            <div class="stat-content">
              <h4>企业总数</h4>
              <div class="stat-number">
                <span class="number">45</span>
                <span class="trend up">↑ 5%</span>
              </div>
            </div>
          </div>

          <div class="stat-item" @click="showDetail('school')">
            <div class="stat-icon">
              <i class="fas fa-school"></i>
            </div>
            <div class="stat-content">
              <h4>学校总数</h4>
              <div class="stat-number">
                <span class="number">20</span>
                <span class="trend up">↑ 2%</span>
              </div>
            </div>
          </div>

          <!-- 企业评分图表 -->
          <div class="rating-chart">
            <h4>企业评分分布</h4>
            <div ref="ratingChart" class="rating-chart-container"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'AdminDashboardHome',
  data() {
    return {
      adminInfo: JSON.parse(localStorage.getItem('user') || '{}'),
      showModal: false,
      modalTitle: '',
      modalContent: ''
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initPieChart()
      this.initTrendChart()
      this.initRatingChart()
      window.addEventListener('resize', this.resizeCharts)
    })
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resizeCharts)
  },
  methods: {
    resizeCharts() {
      const pieChart = echarts.init(this.$refs.pieChart)
      const trendChart = echarts.init(this.$refs.trendChart)
      const ratingChart = echarts.init(this.$refs.ratingChart)
      
      pieChart.resize()
      trendChart.resize()
      ratingChart.resize()
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
          top: 'center'
        },
        series: [{
          name: '岗位分布',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '16',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: 40, name: '技术开发', itemStyle: { color: '#CA4499' } },
            { value: 25, name: '产品运营', itemStyle: { color: '#9B55D3' } },
            { value: 15, name: '市场营销', itemStyle: { color: '#E391C3' } },
            { value: 12, name: '设计创意', itemStyle: { color: '#F7C0E2' } },
            { value: 8, name: '其他', itemStyle: { color: '#FFE5F3' } }
          ]
        }]
      })
    },

    initTrendChart() {
      const trendChart = echarts.init(this.$refs.trendChart)
      trendChart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['1月', '2月', '3月', '4月', '5月', '6月'],
          axisLine: {
            lineStyle: {
              color: '#666'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              color: '#666'
            }
          },
          splitLine: {
            lineStyle: {
              type: 'dashed',
              color: '#ddd'
            }
          }
        },
        series: [{
          name: '申请数量',
          type: 'line',
          smooth: true,
          data: [120, 132, 101, 134, 90, 230],
          itemStyle: {
            color: '#CA4499'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
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

    initRatingChart() {
      const ratingChart = echarts.init(this.$refs.ratingChart)
      ratingChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['1星', '2星', '3星', '4星', '5星'],
          axisLine: { lineStyle: { color: '#666' } }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: '#666' } },
          splitLine: {
            lineStyle: {
              type: 'dashed',
              color: '#ddd'
            }
          }
        },
        series: [{
          data: [2, 5, 15, 25, 35],
          type: 'bar',
          barWidth: '40%',
          itemStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [{
                offset: 0,
                color: '#CA4499'
              }, {
                offset: 1,
                color: '#E391C3'
              }]
            },
            borderRadius: [4, 4, 0, 0]
          }
        }]
      })
    },

    showDetail(type) {
      this.modalTitle = type === 'student' ? '学生统计详情' :
                       type === 'company' ? '企业统计详情' : '学校统计详情'
      this.modalContent = '这里显示详细的统计信息...'
      this.showModal = true
    },

    closeModal() {
      this.showModal = false
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  flex: 1;
  padding: 25px;
  position: relative;
  z-index: 1;
  background: #f8f9fa;
  border-radius: 30px 0 0 30px;
  margin-left: -20px;
  display: flex;
  flex-direction: column;
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding: 0 10px;
}

.welcome-section h2 {
  font-size: 24px;
  color: #333;
}

.welcome {
  color: #666;
  font-size: 15px;
}

.dashboard-container {
  display: grid;
  grid-template-columns: 13fr 7fr;
  gap: 20px;
  padding: 0 10px;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 16px rgba(202, 68, 153, 0.05);
  border: 1px solid rgba(202, 68, 153, 0.1);
  height: 380px;
}

.pie-chart, .trend-chart {
  height: 300px;
  width: 100%;
}

.stat-summary {
  display: grid;
  grid-template-columns: 1fr;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  background: white;
  padding: 18px;
  border-radius: 16px;
  box-shadow: 0 8px 16px rgba(202, 68, 153, 0.05);
  border: 1px solid rgba(202, 68, 153, 0.1);
  transition: transform 0.3s ease;
  cursor: pointer;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(202, 68, 153, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  background: rgba(202, 68, 153, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 24px;
  color: #CA4499;
}

.stat-content {
  flex: 1;
}

.stat-content h4 {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stat-number {
  display: flex;
  align-items: center;
  margin-top: 5px;
}

.number {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.trend {
  margin-left: 10px;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.trend.up {
  color: #00C853;
  background: rgba(0, 200, 83, 0.1);
}

.rating-chart {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-top: 20px;
  box-shadow: 0 4px 12px rgba(202, 68, 153, 0.05);
}

.rating-chart h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.rating-chart h4::before {
  content: '\f681';
  font-family: 'Font Awesome 5 Free';
  font-weight: 900;
  margin-right: 10px;
  color: #CA4499;
}

.rating-chart-container {
  height: 180px;
  width: 100%;
}

.chart-card h3 i {
  color: #CA4499;
  margin-right: 10px;
}

.pie-chart {
  padding-right: 80px;
  height: 300px;
}

.trend-chart {
  height: 300px;
}
</style> 