const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  indexPath: 'index.html',
  outputDir: 'dist',
  publicPath: '/',
  devServer: {
    port: 8080,
    historyApiFallback: {
      rewrites: [
        // 静态页面保持原样
        { from: /^\/$/, to: '/home.html' },
        { from: /^\/home/, to: '/home.html' },
        { from: /^\/login/, to: '/login.html' },
        { from: /^\/register/, to: '/register.html' },
        { from: /^\/recommend/, to: '/recommend.html' },
        { from: /./, to: '/index.html' }    
      ]
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        ws: true
      }
    }
  },
  pages: {
    index: {
      entry: 'src/main.js',
      template: 'src/index.html',
      filename: 'index.html',
      title: '实习管理系统'
    }
  }
})
