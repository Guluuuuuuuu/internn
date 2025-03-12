<template>
  <div class="job-browsing">
    <div class="header">
      <h2>岗位搜索</h2>
      <div class="search-bar">
        <div class="filters">
          <select v-model="cityFilter" class="filter-select">
            <option value="">城市</option>
            <option value="杭州">杭州</option>
            <option value="北京">北京</option>
            <option value="上海">上海</option>
          </select>
          <select v-model="industryFilter" class="filter-select">
            <option value="">行业</option>
            <option value="互联网">互联网</option>
            <option value="金融">金融</option>
            <option value="教育">教育</option>
          </select>
          <select v-model="salaryFilter" class="filter-select">
            <option value="">薪资范围</option>
            <option value="0-200">0-200/天</option>
            <option value="200-300">200-300/天</option>
            <option value="300+">300+/天</option>
          </select>
        </div>
        <div class="search-input">
          <input type="text" v-model="searchKeyword" placeholder="请输入岗位名称搜索">
          <button class="search-btn">搜索</button>
        </div>
      </div>
    </div>

    <div class="content-card">
      <div class="job-list">
        <div v-for="job in jobs" :key="job.id" class="job-card">
          <div class="job-header">
            <h3>{{ job.name }}</h3>
            <span class="salary">{{ job.salary }}</span>
          </div>
          <div class="company-info">
            <img :src="job.companyLogo" alt="公司logo" class="company-logo">
            <span class="company-name">{{ job.company }}</span>
            <span class="location"><i class="fas fa-map-marker-alt"></i> {{ job.location }}</span>
          </div>
          <div class="job-tags">
            <span v-for="tag in job.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="job-description">
            <p>{{ job.description }}</p>
          </div>
          <div class="job-footer">
            <span class="post-time">发布时间：{{ job.postTime }}</span>
            <div class="actions">
              <button class="collect-btn" @click="toggleCollect(job)">
                <i :class="job.isCollected ? 'fas fa-star' : 'far fa-star'"></i>
                {{ job.isCollected ? '已收藏' : '收藏' }}
              </button>
              <button class="apply-btn">申请岗位</button>
            </div>
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
  </div>
</template>

<script>
export default {
  name: 'StudentJobBrowsing',
  data() {
    return {
      cityFilter: '',
      industryFilter: '',
      salaryFilter: '',
      searchKeyword: '',
      jobs: [
        {
          id: 1,
          name: '前端开发实习生',
          salary: '300-400/天',
          company: '阿里巴巴',
          companyLogo: '/company-logos/alibaba.png',
          location: '杭州',
          tags: ['互联网', '弹性工作', '包午餐'],
          description: '负责公司电商平台的前端开发工作，使用 Vue.js 和 React 技术栈...',
          postTime: '2024-03-15',
          isCollected: false
        }
      ]
    }
  },
  methods: {
    toggleCollect(job) {
      job.isCollected = !job.isCollected;
      // TODO: 调用后端API更新收藏状态
    }
  }
}
</script>

<style scoped>
.job-browsing {
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

.filters {
  display: flex;
  gap: 15px;
}

.filter-select {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  min-width: 120px;
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

.job-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.job-card {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 16px;
  transition: all 0.3s;
}

.job-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.job-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.salary {
  color: #f5222d;
  font-weight: bold;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.company-logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.company-name {
  color: #666;
}

.location {
  color: #666;
  font-size: 14px;
}

.location i {
  margin-right: 5px;
  color: #CA4499;
}

.job-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.tag {
  padding: 4px 12px;
  background: rgba(202, 68, 153, 0.1);
  color: #CA4499;
  border-radius: 20px;
  font-size: 12px;
}

.job-description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-time {
  color: #999;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 10px;
}

.collect-btn, .apply-btn {
  padding: 8px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.collect-btn {
  background: rgba(202, 68, 153, 0.1);
  color: #CA4499;
}

.collect-btn i {
  color: #CA4499;
}

.apply-btn {
  background: #CA4499;
  color: white;
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
</style> 