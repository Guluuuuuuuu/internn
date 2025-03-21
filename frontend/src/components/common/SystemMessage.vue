<!-- SystemMessage.vue -->
<template>
  <Transition name="message-fade">
    <div v-if="visible" class="message" :class="type">
      {{ message }}
    </div>
  </Transition>
</template>

<script>
export default {
  name: 'SystemMessage',
  data() {
    return {
      visible: false,
      message: '',
      type: 'info',
      duration: 3000,
      timer: null
    }
  },
  methods: {
    show({ message, type = 'info', duration = 3000 }) {
      // 清除之前的定时器
      if (this.timer) {
        clearTimeout(this.timer);
      }
      
      // 设置新的消息
      this.message = message;
      this.type = type;
      this.duration = duration;
      this.visible = true;
      
      // 设置新的定时器
      this.timer = setTimeout(() => {
        this.visible = false;
      }, this.duration);
    }
  },
  beforeUnmount() {
    // 组件销毁前清除定时器
    if (this.timer) {
      clearTimeout(this.timer);
    }
  }
}
</script>

<style scoped>
.message {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 10px 20px;
  border-radius: 4px;
  z-index: 9999;
  font-size: 14px;
  transition: all 0.3s;
}

.success {
  background: #f0f9eb;
  color: #67c23a;
  border: 1px solid #c2e7b0;
}

.error {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

.info {
  background: #f4f4f5;
  color: #909399;
  border: 1px solid #e9e9eb;
}

.message-fade-enter-active,
.message-fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.message-fade-enter-from,
.message-fade-leave-to {
  opacity: 0;
  transform: translate(-50%, -100%);
}
</style> 