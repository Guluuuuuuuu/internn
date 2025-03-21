<!-- DocumentOutline.vue -->
<template>
  <div class="document-outline" :class="{ 'collapsed': isCollapsed }">
    <div class="outline-header">
      <h3>目录</h3>
      <button class="toggle-btn" @click="toggleCollapse">
        <i :class="isCollapsed ? 'fas fa-chevron-right' : 'fas fa-chevron-left'"></i>
      </button>
    </div>
    
    <div class="outline-content" v-if="!isCollapsed">
      <div v-if="headings.length === 0" class="empty-outline">
        <div class="lottie-container">
          <Vue3Lottie
            :animationData="emptyAnimationData"
            :height="120"
            :width="120"
          />
        </div>
        <p>添加标题后会自动生成目录</p>
      </div>
      
      <ul v-else class="outline-list">
        <li 
          v-for="(heading, index) in headings" 
          :key="index" 
          class="outline-item"
          :class="{ 
            'active': activeHeadingIndex === index,
            [`level-${heading.level}`]: true 
          }"
          @click="selectHeading(index, heading.id)"
        >
          <span class="heading-text">{{ heading.text }}</span>
        </li>
      </ul>
    </div>
    
    <div class="collapse-button" v-if="isCollapsed" @click="toggleCollapse">
      <div class="icon-wrapper">
        <i class="fas fa-list-ul"></i>
      </div>
    </div>
  </div>
</template>

<script>
import { Vue3Lottie } from 'vue3-lottie';
import emptyAnimation from '../../assets/animations/empty-box.json';

export default {
  name: 'DocumentOutline',
  components: {
    Vue3Lottie
  },
  props: {
    markdown: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      isCollapsed: false,
      headings: [],
      activeHeadingIndex: null,
      emptyAnimationData: emptyAnimation
    };
  },
  watch: {
    markdown: {
      handler(newMarkdown) {
        this.parseHeadings(newMarkdown);
      },
      immediate: true
    }
  },
  methods: {
    toggleCollapse() {
      this.isCollapsed = !this.isCollapsed;
      this.$emit('toggle-collapse', this.isCollapsed);
    },
    parseHeadings(markdown) {
      if (!markdown) {
        this.headings = [];
        return;
      }
      
      const headingRegex = /^(#{1,6})\s+(.+)$/gm;
      const headings = [];
      let match;
      
      while ((match = headingRegex.exec(markdown)) !== null) {
        const level = match[1].length;
        const text = match[2].trim();
        const id = this.generateId(text);
        
        headings.push({
          level,
          text,
          id,
          position: match.index
        });
      }
      
      this.headings = headings;
    },
    generateId(text) {
      return text
        .toLowerCase()
        .replace(/[^\w\s-]/g, '')
        .replace(/\s+/g, '-');
    },
    selectHeading(index, id) {
      this.activeHeadingIndex = index;
      this.$emit('select-heading', id);
    }
  }
};
</script>

<style scoped>
.document-outline {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 250px;
  background: #f1f1f1;
  border-right: 1px solid #ddd;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.document-outline.collapsed {
  width: 45px;
}

.outline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ddd;
}

.outline-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.toggle-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.toggle-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}

.outline-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
}

.outline-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.outline-item {
  padding: 6px 15px 6px 20px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s ease;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.outline-item:hover {
  background: rgba(0, 0, 0, 0.05);
}

.outline-item.active {
  background: rgba(202, 68, 153, 0.1);
}

.outline-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px;
  background: #CA4499;
}

.outline-item.level-1 {
  font-weight: 600;
}

.outline-item.level-2 {
  padding-left: 30px;
}

.outline-item.level-3 {
  padding-left: 40px;
  font-size: 13px;
}

.outline-item.level-4, .outline-item.level-5, .outline-item.level-6 {
  padding-left: 50px;
  font-size: 12px;
  color: #666;
}

.collapse-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  cursor: pointer;
  transition: all 0.2s ease;
}

.icon-wrapper {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(202, 68, 153, 0.1);
  color: #CA4499;
  transition: all 0.2s ease;
}

.icon-wrapper:hover {
  background: rgba(202, 68, 153, 0.2);
  transform: scale(1.05);
}

.empty-outline {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #999;
  text-align: center;
}

.lottie-container {
  margin-bottom: 10px;
}
</style> 