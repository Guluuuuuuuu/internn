<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%; /* 确保页面元素占满整个屏幕 */
}

#app {
  display: flex;
  flex-direction: column; /* 垂直布局 */
  min-height: 100vh; /* 确保根容器占满视口 */
}

.main-content {
  flex: 1; /* 让 main-content 填满剩余空间 */
  padding: 20px;
}

#header, #footer {
  /* 确保 header 和 footer 高度固定 */
  background-color: #f5f5f5; 
}
</style>
