<template>
  <div class="resume-container">
    <div class="resume-management">
      <div class="resume-header">
        <h1>个人简历</h1>
        <div class="actions">
          <button v-if="!isEditing" class="edit-btn" @click="startEditing">
            <i class="fas fa-edit"></i> 编辑简历
          </button>
          <template v-else>
            <button class="save-btn" @click="saveResume">
              <i class="fas fa-save"></i> 保存
            </button>
            <button class="cancel-btn" @click="cancelEditing">
              <i class="fas fa-times"></i> 取消
            </button>
          </template>
          <button class="export-btn" @click="exportResume">
            <i class="fas fa-download"></i> 导出
          </button>
        </div>
      </div>

      <!-- 查看模式 -->
      <div v-if="!isEditing" class="resume-view">
        <div class="resume-basic-info">
          <div class="info-left">
            <h2>{{ resumeData.name || '姓名' }} <span>{{ resumeData.age || '' }} {{ resumeData.education || '' }}</span></h2>
            <div class="certificates">
              <p v-if="resumeData.certificates"><strong>资格证书:</strong> {{ resumeData.certificates }}</p>
              <p v-if="resumeData.english_level"><strong>英语能力:</strong> {{ resumeData.english_level }}</p>
            </div>
          </div>
          <div class="info-right">
            <div class="photo">
              <img v-if="resumeData.photo" :src="resumeData.photo" alt="个人照片">
              <div v-else class="photo-placeholder">
                <i class="fas fa-user"></i>
              </div>
            </div>
          </div>
        </div>

        <div v-for="(section, index) in sections" :key="index" class="resume-section">
          <div class="section-header">
            <h3><i :class="section.icon"></i> {{ section.title }}</h3>
          </div>
          <div class="section-content markdown-content" v-html="renderMarkdown(resumeData[section.key] || '')"></div>
        </div>
      </div>

      <!-- 编辑模式 -->
      <div v-else class="resume-edit">
        <div class="edit-section">
          <h3>基本信息</h3>
          <div class="resume-basic-info">
            <div class="info-left">
              <div class="form-row">
                <div class="form-group">
                  <label>姓名</label>
                  <input type="text" v-model="editData.name" placeholder="请输入您的姓名">
                </div>
                <div class="form-group">
                  <label>年龄</label>
                  <input type="number" v-model="editData.age" placeholder="请输入年龄">
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label>学历</label>
                  <input type="text" v-model="editData.education" placeholder="如：本科/硕士">
                </div>
                <div class="form-group">
                  <label>英语能力</label>
                  <input type="text" v-model="editData.english_level" placeholder="如：CET-6/雅思6.5">
                </div>
              </div>
              <div class="form-group full-width">
                <label>资格证书</label>
                <input type="text" v-model="editData.certificates" placeholder="请输入您的资格证书，用逗号分隔">
              </div>
            </div>
            <div class="info-right">
              <div class="photo-container">
                <div class="photo-frame">
                  <div class="photo-preview">
                    <img v-if="editData.photo" :src="editData.photo" alt="照片预览">
                    <div v-else class="photo-placeholder">
                      <i class="fas fa-user"></i>
                      <span>证件照</span>
                    </div>
                  </div>
                </div>
                <p class="photo-tip">建议上传25×35mm证件照</p>
                <button class="upload-btn" @click="uploadPhoto">
                  <i class="fas fa-upload"></i> 上传照片
                </button>
                <input type="file" ref="photoInput" style="display: none" @change="handlePhotoUpload" accept="image/*">
              </div>
            </div>
          </div>
        </div>

        <div v-for="(section, index) in sections" :key="index" class="edit-section">
          <h3>{{ section.title }}</h3>
          <div class="markdown-editor">
            <div class="editor-header">
              <span>Markdown编辑器</span>
              <div class="editor-tools">
                <button @click="formatSelectedText(section.key, '**', '**')" title="粗体">
                  <i class="fas fa-bold"></i>
                </button>
                <button @click="formatSelectedText(section.key, '#### ', '')" title="标题">
                  <i class="fas fa-heading"></i>
                </button>
                <button @click="formatSelectedText(section.key, '- ', '')" title="列表项">
                  <i class="fas fa-list-ul"></i>
                </button>
                <button @click="formatRightAlign(section.key)" title="右对齐">
                  <i class="fas fa-align-right"></i>
                </button>
              </div>
            </div>
            <textarea 
              :value="editData[section.key]"
              @input="updateContent(section.key, $event)"
              @keydown="handleKeydown($event)"
              :placeholder="`使用Markdown格式编辑${section.title}...`"
              rows="10"
              :ref="el => { if (el) markdownEditors[section.key] = el }"
            ></textarea>
            <div v-if="isPreview" class="markdown-preview">
              <div class="preview-header">预览</div>
              <div class="preview-content" v-html="renderMarkdown(editData[section.key] || '')"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { marked } from 'marked';
import DOMPurify from 'dompurify';

export default {
  name: 'ResumeManagement',
  data() {
    return {
      isEditing: false,
      isPreview: true,
      resumeData: {
        name: '',
        age: '',
        education: '',
        certificates: '',
        english_level: '',
        photo: '',
        introduction: '',
        education_background: '',
        competition_experience: '',
        internship_experience: '',
        research_experience: '',
        skills: ''
      },
      editData: {
        name: '',
        age: '',
        education: '',
        certificates: '',
        english_level: '',
        photo: '',
        introduction: '',
        education_background: '',
        competition_experience: '',
        internship_experience: '',
        research_experience: '',
        skills: ''
      },
      sections: [
        { title: '个人简介', key: 'introduction', icon: 'fas fa-user' },
        { title: '教育背景', key: 'education_background', icon: 'fas fa-graduation-cap' },
        { title: '竞赛经历', key: 'competition_experience', icon: 'fas fa-trophy' },
        { title: '实习实践经历', key: 'internship_experience', icon: 'fas fa-briefcase' },
        { title: '科研经历', key: 'research_experience', icon: 'fas fa-flask' },
        { title: '文体特长', key: 'skills', icon: 'fas fa-star' }
      ],
      markdownEditors: {}
    };
  },
  mounted() {
    this.fetchResumeData();
  },
  methods: {
    async fetchResumeData() {
      try {
        const studentId = localStorage.getItem('userId');
        if (!studentId) {
          console.error('未找到学生ID');
          return;
        }
        
        const response = await axios.get(`http://localhost:8888/api/resume/${studentId}`);
        this.resumeData = response.data;
        this.editData = JSON.parse(JSON.stringify(this.resumeData));
      } catch (error) {
        console.error('获取简历数据失败:', error);
      }
    },
    
    async saveResume() {
      try {
        const studentId = localStorage.getItem('userId');
        if (!studentId) {
          console.error('未找到学生ID');
          return;
        }
        
        await axios.post(`http://localhost:8888/api/resume/${studentId}`, this.editData);
        this.resumeData = JSON.parse(JSON.stringify(this.editData));
        this.isEditing = false;
        alert('简历保存成功！');
      } catch (error) {
        console.error('保存简历失败:', error);
        alert('保存失败，请稍后重试');
      }
    },
    
    startEditing() {
      this.editData = JSON.parse(JSON.stringify(this.resumeData));
      this.isEditing = true;
    },
    
    cancelEditing() {
      this.isEditing = false;
    },
    
    renderMarkdown(text) {
      if (!text) return '';
      const rawHtml = marked(text);
      return DOMPurify.sanitize(rawHtml);
    },
    
    formatSelectedText(key, prefix, suffix = '') {
      const textarea = this.markdownEditors[key];
      if (!textarea) return;
      
      const text = this.editData[key] || '';
      const selectionStart = textarea.selectionStart;
      const selectionEnd = textarea.selectionEnd;
      
      if (selectionStart !== selectionEnd) {
        const selectedText = text.substring(selectionStart, selectionEnd);
        const formattedText = prefix + selectedText + suffix;
        
        this.editData[key] = text.substring(0, selectionStart) + formattedText + text.substring(selectionEnd);
        
        this.$nextTick(() => {
          textarea.focus();
          textarea.setSelectionRange(
            selectionStart + prefix.length,
            selectionEnd + prefix.length
          );
        });
      } else if (selectionStart >= 0) {
        const defaultText = prefix === '- ' ? '列表项' : 
                           prefix === '#### ' ? '标题' : 
                           prefix === '**' ? '粗体文本' : '文本';
        
        const formattedText = prefix + defaultText + suffix;
        
        this.editData[key] = text.substring(0, selectionStart) + formattedText + text.substring(selectionStart);
        
        this.$nextTick(() => {
          textarea.focus();
          const newSelectionStart = selectionStart + prefix.length;
          textarea.setSelectionRange(
            newSelectionStart,
            newSelectionStart + defaultText.length
          );
        });
      } else {
        const defaultText = prefix === '- ' ? '列表项' : 
                           prefix === '#### ' ? '标题' : 
                           prefix === '**' ? '粗体文本' : '文本';
        
        const newLine = text.length > 0 && !text.endsWith('\n') ? '\n' : '';
        const formattedText = newLine + prefix + defaultText + suffix;
        
        this.editData[key] = text + formattedText;
        
        this.$nextTick(() => {
          textarea.focus();
          const newSelectionStart = text.length + newLine.length + prefix.length;
          textarea.setSelectionRange(
            newSelectionStart,
            newSelectionStart + defaultText.length
          );
        });
      }
    },
    
    formatRightAlign(key) {
      const textarea = this.markdownEditors[key];
      if (!textarea) return;
      
      const text = this.editData[key] || '';
      const selectionStart = textarea.selectionStart;
      const selectionEnd = textarea.selectionEnd;
      
      const prefix = '<span style="float:right;">';
      const suffix = '</span>';
      
      if (selectionStart !== selectionEnd) {
        const selectedText = text.substring(selectionStart, selectionEnd);
        const formattedText = prefix + selectedText + suffix;
        
        this.editData[key] = text.substring(0, selectionStart) + formattedText + text.substring(selectionEnd);
        
        this.$nextTick(() => {
          textarea.focus();
          textarea.setSelectionRange(
            selectionStart + prefix.length,
            selectionEnd + prefix.length
          );
        });
      } else if (selectionStart >= 0) {
        const defaultText = '右对齐文本';
        const formattedText = prefix + defaultText + suffix;
        
        this.editData[key] = text.substring(0, selectionStart) + formattedText + text.substring(selectionStart);
        
        this.$nextTick(() => {
          textarea.focus();
          const newSelectionStart = selectionStart + prefix.length;
          textarea.setSelectionRange(
            newSelectionStart,
            newSelectionStart + defaultText.length
          );
        });
      } else {
        const defaultText = '右对齐文本';
        const newLine = text.length > 0 && !text.endsWith('\n') ? '\n' : '';
        const formattedText = newLine + prefix + defaultText + suffix;
        
        this.editData[key] = text + formattedText;
        
        this.$nextTick(() => {
          textarea.focus();
          const newSelectionStart = text.length + newLine.length + prefix.length;
          textarea.setSelectionRange(
            newSelectionStart,
            newSelectionStart + defaultText.length
          );
        });
      }
    },
    
    handlePhotoUpload(event) {
      const file = event.target.files[0];
      if (!file) return;
      
      const reader = new FileReader();
      reader.onload = (e) => {
        this.editData.photo = e.target.result;
      };
      reader.readAsDataURL(file);
    },
    
    exportResume() {
      try {
        // 如果当前是编辑模式，先切换到查看模式
        const wasEditing = this.isEditing;
        if (wasEditing) {
          this.isEditing = false;
        }
        
        // 给DOM一点时间渲染查看模式
        setTimeout(() => {
          // 获取查看模式下的简历内容
          const resumeContent = document.querySelector('.resume-view');
          if (!resumeContent) {
            console.error('找不到简历内容元素');
            return;
          }
          
          // 创建一个临时的样式元素，用于PDF导出时调整样式
          const tempStyle = document.createElement('style');
          tempStyle.textContent = `
            .resume-view {
              width: 190mm !important; /* 减小宽度，留出更多边距 */
              padding: 10mm !important;
              font-size: 10px !important; /* 减小字体 */
              line-height: 1.4 !important;
            }
            .resume-view h2 {
              font-size: 14px !important;
              margin-top: 10px !important;
              margin-bottom: 0px !important; /* 减少标题与下方内容的距离 */
              padding-bottom: 0px !important; /* 减少标题下边框的距离 */
              border-bottom-width: 0px !important;
            }
            .resume-view h3 {
              font-size: 12px !important;
              margin-top: 8px !important;
              margin-bottom: 0px !important; /* 减少标题与下方内容的距离 */
            }
            .resume-view p {
              margin: 5px 0 !important;
            }
            .resume-view .resume-basic-info {
              margin-bottom: 15px !important;
            }
            .resume-view .info-left h2 {
              font-size: 16px !important; /* 姓名字体更大 */
              margin-bottom: 2px !important;
            }
            .resume-view .info-left h2 span {
              font-size: 12px !important; /* 学历字体更小 */
            }
            .resume-view .info-right {
              width: 25mm !important; /* 减小照片容器宽度 */
            }
            .resume-view .photo {
              width: 25mm !important; /* 减小照片宽度 */
              height: 35mm !important; /* 减小照片高度 */
            }
            .resume-view .resume-section {
              margin-bottom: 10px !important;
            }
            .resume-view .resume-section h2 {
              padding-bottom: 1px !important;
              margin-bottom: 3px !important;
            }
            .resume-view ul, .resume-view ol {
              padding-left: 15px !important;
              margin: 5px 0 !important;
            }
          `;
          document.head.appendChild(tempStyle);
          
          // 使用html2pdf库将内容转换为PDF
          import('html2pdf.js').then(html2pdf => {
            const opt = {
              margin: [15, 15, 15, 15], // 增加边距
              filename: `${this.resumeData.name || '我的'}简历.pdf`,
              image: { type: 'jpeg', quality: 0.98 },
              html2canvas: { 
                scale: 2, 
                useCORS: true,
                allowTaint: true,
                logging: true,
                letterRendering: true,
                width: 800, // 指定宽度
                height: 1131, // A4比例高度
                windowWidth: 800 // 确保内容适配
              },
              jsPDF: { 
                unit: 'mm', 
                format: 'a4', 
                orientation: 'portrait',
                compress: true
              }
            };
            
            html2pdf.default(resumeContent, opt).then(() => {
              // 导出完成后移除临时样式
              document.head.removeChild(tempStyle);
              
              // 如果之前是编辑模式，导出后恢复
              if (wasEditing) {
                this.isEditing = true;
              }
            });
          }).catch(err => {
            console.error('加载html2pdf库失败:', err);
            alert('导出PDF失败，请稍后重试');
            // 移除临时样式
            if (document.head.contains(tempStyle)) {
              document.head.removeChild(tempStyle);
            }
            // 恢复编辑模式
            if (wasEditing) {
              this.isEditing = true;
            }
          });
        }, 800); // 增加等待时间确保完全渲染
      } catch (error) {
        console.error('导出简历失败:', error);
        alert('导出简历失败，请稍后重试');
      }
    },
    
    updateContent(key, event) {
      this.editData[key] = event.target.value;
    },
    
    handleKeydown(event) {
      if ((event.metaKey || event.ctrlKey) && (event.key === 'z' || event.key === 'y')) {
        return;
      }
    },
    
    uploadPhoto() {
      this.$refs.photoInput.click();
    }
  }
};
</script>

<style scoped>
.resume-container {
  flex: 1;
  padding: 30px;
  position: relative;
  z-index: 1;
  background: #f8f9fa;
  border-radius: 30px 0 0 30px;
  margin-left: -20px;
}

.resume-management {
  max-width: 1200px;
  margin: 0 auto;
}

.resume-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.resume-header h1 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.actions {
  display: flex;
  gap: 12px;
}

.actions button {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  transition: all 0.3s;
}

.edit-btn {
  background-color: #CA4499;
  color: white;
}

.save-btn {
  background-color: rgb(197, 20, 197);
  color: white;
}

.cancel-btn {
  background-color: #7855E5;
  color: white;
}

.export-btn {
  background-color: #4b14b3;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.export-btn:hover {
  background-color: #40a9ff;
}

.edit-btn:hover, .save-btn:hover, .cancel-btn:hover, .export-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/* 查看模式样式 */
.resume-view {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 32px;
}

.resume-basic-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  gap: 24px;
  background-color: transparent;
  padding: 0;
  box-shadow: none;
  border-radius: 0;
}

.info-left {
  flex: 1;
  background-color: #f3f0f0;
  padding: 15px 20px;
  border-radius: 6px;
}

.info-left h2 {
  margin-top: 0;
  margin-bottom: 12px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(82, 80, 80, 0.1);
}

.info-left h2 span {
  font-size: 16px;
  color: #666;
  margin-left: 12px;
  font-weight: normal;
}

.certificates p {
  margin: 8px 0;
  color: #666;
}

.info-right {
  width: 32.5mm;
}

.photo {
  width: 32.5mm;
  height: 45.5mm;
  overflow: hidden;
  border-radius: 4px;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ddd;
  background-color: #f9f9f9;
}

.photo-placeholder i {
  font-size: 50px;
  margin-bottom: 10px;
}

.photo-placeholder span {
  font-size: 14px;
  color: #aaa;
}

.photo-tip {
  margin: 10px 0;
  font-size: 12px;
  color: #888;
}

.upload-btn {
  padding: 10px 20px;
  background-color: #CA4499;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  width: 100%;
  margin-top: 5px;
}

.upload-btn:hover {
  background-color: #b93c89;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(202, 68, 153, 0.2);
}

.upload-btn:active {
  transform: translateY(0);
}

.upload-btn i {
  margin-right: 5px;
}

.resume-section {
  margin-bottom: 32px;
}

.section-header {
  margin-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.section-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header h3 i {
  color: #CA4499;
}

.markdown-content {
  line-height: 1.6;
  color: #333;
}

.markdown-content ul {
  padding-left: 20px;
}

/* 编辑模式样式 */
.resume-edit {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 32px;
}

.edit-section {
  margin-bottom: 32px;
}

.edit-section h3 {
  margin: 0 0 16px;
  color: #333;
  font-size: 18px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.markdown-editor {
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background-color: #f7f7f7;
  border-bottom: 1px solid #e8e8e8;
}

.editor-header span {
  font-weight: 500;
  color: #555;
}

.editor-tools {
  display: flex;
  gap: 8px;
}

.editor-tools button {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
}

.editor-tools button:hover {
  background-color: #f0f0f0;
  border-color: #bbb;
}

.editor-tools button:active {
  background-color: #e8e8e8;
}

.markdown-editor textarea {
  border: none;
  border-radius: 0;
  padding: 16px;
  width: 100%;
  min-height: 200px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  resize: vertical;
}

.markdown-editor textarea:focus {
  outline: none;
  background-color: #fefefe;
}

.markdown-preview {
  padding: 16px;
  border-top: 1px solid #e8e8e8;
  background-color: #fafafa;
}

.preview-header {
  margin-bottom: 12px;
  color: #888;
  font-size: 13px;
  font-weight: 500;
}

.preview-content {
  padding: 0 8px;
}

/* 编辑模式下的基本信息样式 */
.resume-edit .resume-basic-info {
  display: flex;
  margin-bottom: 30px;
  background-color: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.resume-edit .info-left {
  flex: 1;
  padding-right: 40px;
  background-color: transparent;
  border-radius: 0;
}

.resume-edit .form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.resume-edit .form-group {
  flex: 1;
  margin-bottom: 15px;
}

.resume-edit .form-group.full-width {
  width: 100%;
}

.resume-edit .form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 15px;
}

.resume-edit .form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
  background-color: #fcfcfc;
}

.resume-edit .form-group input:focus {
  border-color: #CA4499;
  box-shadow: 0 0 0 2px rgba(202, 68, 153, 0.1);
  outline: none;
  background-color: white;
}

.resume-edit .form-group input::placeholder {
  color: #aaa;
}

.resume-edit .info-right {
  width: 220px;
}

.resume-edit .photo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.resume-edit .photo-frame {
  padding: 8px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.resume-edit .photo-preview {
  width: 175px;
  height: 245px; /* 25×35mm 比例 */
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: white;
}

.resume-edit .photo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.resume-edit .photo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ddd;
  background-color: white;
  border: 1px dashed #e0e0e0;
}

.resume-edit .photo-placeholder i {
  font-size: 50px;
  margin-bottom: 10px;
}

.resume-edit .photo-placeholder span {
  font-size: 14px;
  color: #aaa;
}

.resume-edit .photo-tip {
  margin: 10px 0;
  font-size: 12px;
  color: #888;
}

.resume-edit .upload-btn {
  padding: 10px 20px;
  background-color: #CA4499;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  width: 100%;
  margin-top: 5px;
}

.resume-edit .upload-btn:hover {
  background-color: #b93c89;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(202, 68, 153, 0.2);
}

.resume-edit .upload-btn:active {
  transform: translateY(0);
}

.resume-edit .upload-btn i {
  margin-right: 5px;
}
</style> 