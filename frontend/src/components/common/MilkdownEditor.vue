<template>
  <div class="milkdown-editor">
    <!-- 工具栏部分 -->
    <div class="editor-toolbar">
      <div class="toolbar-group">
        <button @click="undoOperation()" title="撤销 (Ctrl+Z)"><i class="fas fa-undo"></i></button>
        <button @click="redoOperation()" title="重做 (Ctrl+Y)"><i class="fas fa-redo"></i></button>
      </div>
      <div class="toolbar-group">
        <button @click="insertHeading(1)" title="标题1 (Ctrl+1)">H1</button>
        <button @click="insertHeading(2)" title="标题2 (Ctrl+2)">H2</button>
        <button @click="insertHeading(3)" title="标题3 (Ctrl+3)">H3</button>
      </div>
      <div class="toolbar-group">
        <button @click="toggleBold()" title="加粗 (Ctrl+B)"><i class="fas fa-bold"></i></button>
        <button @click="toggleItalic()" title="斜体 (Ctrl+I)"><i class="fas fa-italic"></i></button>
        <button @click="toggleStrikethrough()" title="删除线"><i class="fas fa-strikethrough"></i></button>
      </div>
      <div class="toolbar-group">
        <button @click="insertList('bullet')" title="无序列表"><i class="fas fa-list-ul"></i></button>
        <button @click="insertList('ordered')" title="有序列表"><i class="fas fa-list-ol"></i></button>
        <button @click="insertTaskList()" title="任务列表"><i class="fas fa-tasks"></i></button>
      </div>
      <div class="toolbar-group">
        <button @click="insertBlockquote()" title="引用"><i class="fas fa-quote-right"></i></button>
        <button @click="insertCodeBlock()" title="代码块"><i class="fas fa-code"></i></button>
        <button @click="insertHorizontalRule()" title="分割线"><i class="fas fa-minus"></i></button>
      </div>
      <div class="toolbar-group">
        <button @click="insertTable()" title="表格"><i class="fas fa-table"></i></button>
        <button @click="insertLink()" title="链接"><i class="fas fa-link"></i></button>
        <button @click="insertImage()" title="图片"><i class="fas fa-image"></i></button>
      </div>
    </div>

    <!-- 编辑器容器 -->
    <div class="editor-container" ref="editorContainer"></div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onBeforeUnmount, watch } from 'vue';
import * as Vue from 'vue';
import { defaultValueCtx, Editor, editorViewCtx, rootCtx } from '@milkdown/core';
import { commonmark } from '@milkdown/preset-commonmark';
import { gfm } from '@milkdown/preset-gfm';
import { listener, listenerCtx } from '@milkdown/plugin-listener';
// import { nord } from '@milkdown/theme-nord'; // 注释掉nord主题
import { history, historyKeymap } from '@milkdown/plugin-history';
import { clipboard } from '@milkdown/plugin-clipboard';
// import { maizeTheme } from './MilkdownTheme'; // 不再需要单独导入主题

// 导入我们的自定义样式
import '@/assets/css/custom-milkdown.css';

export default defineComponent({
  name: 'MilkdownEditor',
  props: {
    value: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: '请输入内容...'
    }
  },
  emits: ['update:value', 'save'],
  setup(props, { emit }) {
    const editorContainer = ref(null);
    let editor = null;
    let editorReady = false; // 标记编辑器是否已经准备就绪
    
    // 撤销和重做功能
    const undoOperation = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          // 检查是否有操作可以撤销（不再使用undoDepth）
          const view = ctx.get(editorViewCtx);
          if (view) {
            // 直接尝试执行撤销命令
            historyKeymap.Undo(view.state, view.dispatch, view);
          }
        } catch (error) {
          console.error('撤销操作失败:', error);
        }
      });
    };
    
    const redoOperation = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          // 检查是否有操作可以重做（不再使用redoDepth）
          const view = ctx.get(editorViewCtx);
          if (view) {
            // 直接尝试执行重做命令
            historyKeymap.Redo(view.state, view.dispatch, view);
          }
        } catch (error) {
          console.error('重做操作失败:', error);
        }
      });
    };
    
    // 检查视图状态是否有效
    const isViewValid = (view) => {
      return view && view.state && view.state.selection;
    };
    
    // 工具栏操作方法
    const insertHeading = (level) => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from, to } = selection;
          
          let text = '#'.repeat(level) + ' ';
          if (from !== to) {
            text += state.doc.textBetween(from, to);
          }
          
          dispatch(state.tr.insertText(text, from, to));
        } catch (error) {
          console.error('插入标题失败:', error);
        }
      });
    };

    const toggleBold = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from, to } = selection;
          
          if (from === to) return; // 需要选中文本
          
          const text = state.doc.textBetween(from, to);
          dispatch(state.tr.insertText(`**${text}**`, from, to));
        } catch (error) {
          console.error('加粗文本失败:', error);
        }
      });
    };

    const toggleItalic = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from, to } = selection;
          
          if (from === to) return; // 需要选中文本
          
          const text = state.doc.textBetween(from, to);
          dispatch(state.tr.insertText(`*${text}*`, from, to));
        } catch (error) {
          console.error('斜体文本失败:', error);
        }
      });
    };

    const toggleStrikethrough = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from, to } = selection;
          
          if (from === to) return; // 需要选中文本
          
          const text = state.doc.textBetween(from, to);
          dispatch(state.tr.insertText(`~~${text}~~`, from, to));
        } catch (error) {
          console.error('删除线文本失败:', error);
        }
      });
    };

    const insertList = (type) => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from } = selection;
          
          const prefix = type === 'bullet' ? '- ' : '1. ';
          dispatch(state.tr.insertText(prefix, from, from));
        } catch (error) {
          console.error('插入列表失败:', error);
        }
      });
    };

    const insertBlockquote = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from } = selection;
          
          dispatch(state.tr.insertText('> ', from, from));
        } catch (error) {
          console.error('插入引用失败:', error);
        }
      });
    };

    const insertCodeBlock = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from } = selection;
          
          dispatch(state.tr.insertText('```js\n\n```', from, from));
        } catch (error) {
          console.error('插入代码块失败:', error);
        }
      });
    };

    const insertHorizontalRule = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from } = selection;
          
          dispatch(state.tr.insertText('\n---\n', from, from));
        } catch (error) {
          console.error('插入分割线失败:', error);
        }
      });
    };

    const insertTaskList = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from } = selection;
          
          dispatch(state.tr.insertText('- [ ] ', from, from));
        } catch (error) {
          console.error('插入任务列表失败:', error);
        }
      });
    };

    const insertTable = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from } = selection;
          
          dispatch(state.tr.insertText('| Column 1 | Column 2 | Column 3 |\n| -------- | -------- | -------- |\n| Cell 1 | Cell 2 | Cell 3 |\n| Cell 4 | Cell 5 | Cell 6 |', from, from));
        } catch (error) {
          console.error('插入表格失败:', error);
        }
      });
    };

    const insertLink = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from } = selection;
          
          dispatch(state.tr.insertText('[Text](https://example.com)', from, from));
        } catch (error) {
          console.error('插入链接失败:', error);
        }
      });
    };

    const insertImage = () => {
      if (!editor || !editorReady) return;
      
      editor.action((ctx) => {
        try {
          const view = ctx.get(editorViewCtx);
          if (!isViewValid(view)) return;
          
          const { state, dispatch } = view;
          const { selection } = state;
          const { from } = selection;
          
          dispatch(state.tr.insertText('![Alt Text](https://example.com/image.jpg)', from, from));
        } catch (error) {
          console.error('插入图片失败:', error);
        }
      });
    };

    // 监听props.value的变化
    watch(() => props.value, (newValue) => {
      if (editor && editorReady) {
        editor.action((ctx) => {
          try {
            ctx.set(defaultValueCtx, newValue);
          } catch (error) {
            console.error('更新编辑器内容失败:', error);
          }
        });
      }
    });

    // 保存方法
    const handleSave = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        emit('save');
        return true;
      }
      return false;
    };

    // 初始化编辑器
    const initEditor = () => {
      if (!editorContainer.value) {
        console.log('错误: 编辑器容器为空');
        return;
      }
      
      console.log('开始初始化编辑器...');
      
      try {
        // 创建编辑器实例 - 极度简化版本
        console.log('创建极简编辑器实例');
        editor = Editor.make()
          .config((ctx) => {
            ctx.set(rootCtx, editorContainer.value);
            ctx.set(defaultValueCtx, props.value || '');
            
            // 添加自定义粘贴处理逻辑
            // clipboard插件会自动处理基本的HTML到Markdown转换
            // 这里我们添加额外的日志记录，便于调试和了解粘贴过程
            const handlePaste = (view, event) => {
              if (!event.clipboardData) return false;
              
              const html = event.clipboardData.getData('text/html');
              const text = event.clipboardData.getData('text/plain');
              
              console.log('粘贴内容:', {
                hasHtml: !!html,
                textLength: text?.length,
                htmlPreview: html ? html.substring(0, 100) + '...' : ''
              });
              
              // 返回false让clipboard插件继续处理
              return false;
            };
            
            // 当编辑器创建完成后，添加paste事件处理
            setTimeout(() => {
              if (editor) {
                editor.action((ctx) => {
                  try {
                    const view = ctx.get(editorViewCtx);
                    if (view) {
                      const dom = view.dom;
                      dom.addEventListener('paste', (e) => {
                        // 如果自定义处理返回true，则阻止默认粘贴行为
                        if (handlePaste(view, e)) {
                          e.preventDefault();
                        }
                      });
                    }
                  } catch (error) {
                    console.error('设置粘贴处理器失败:', error);
                  }
                });
              }
            }, 500);
          })
          // .use(nord) // 注释掉nord主题
          .use(listener)
          .use(clipboard) // 添加clipboard插件
          .use(history);
        
        // 首先添加commonmark核心插件，这是最重要的，定义了基本的文档结构
        console.log('添加commonmark基础插件...');
        if (Array.isArray(commonmark)) {
          for (const plugin of commonmark) {
            try {
              editor.use(plugin);
            } catch (e) {
              console.error('添加commonmark插件失败，继续尝试:', e);
            }
          }
        } else {
          console.error('commonmark不是数组，跳过');
        }
        
        // 配置事件监听
        console.log('配置基本事件监听');
        editor.config((ctx) => {
          try {
            const editorListener = ctx.get(listenerCtx);
            editorListener.markdownUpdated((_, value) => {
              emit('update:value', value);
            });
          } catch (error) {
            console.error('配置监听器失败:', error);
          }
        });
        
        // 创建编辑器
        console.log('正在创建编辑器...');
        editor.create()
          .then(() => {
            console.log('编辑器初始化成功，现在添加其他插件...');
            editorReady = true;
            
            // 添加GFM插件
            try {
              console.log('添加GFM插件...');
              if (Array.isArray(gfm)) {
                for (const plugin of gfm) {
                  try {
                    editor.action(ctx => {
                      ctx.get(editorViewCtx); // 检查编辑器是否已准备好
                    });
                    editor.use(plugin);
                  } catch (e) {
                    console.error('添加GFM插件失败，继续尝试:', e);
                  }
                }
              }
            } catch (e) {
              console.error('添加GFM插件过程失败:', e);
            }
            
            // 设置焦点和编辑状态
            setTimeout(() => {
              if (editor) {
                editor.action((ctx) => {
                  try {
                    const view = ctx.get(editorViewCtx);
                    if (view) {
                      // 确保编辑器可输入
                      view.setProps({ 
                        editable: () => true
                      });
                      
                      // 强制聚焦并刷新视图
                      view.focus();
                      view.dispatch(view.state.tr);
                      console.log('编辑器设置完成');
                    } else {
                      console.warn('无法获取编辑器视图');
                    }
                  } catch (error) {
                    console.error('设置焦点失败:', error);
                  }
                });
              }
            }, 500);
          })
          .catch(error => {
            console.error('编辑器初始化失败:', error);
            console.log('尝试使用最小配置初始化...');
            
            // 尝试备用最小配置
            try {
              if (editor) {
                editor.destroy();
              }
              
              // 最小化配置 - 只使用最核心的插件
              console.log('使用最小配置初始化');
              editor = Editor.make()
                .config((ctx) => {
                  ctx.set(rootCtx, editorContainer.value);
                  ctx.set(defaultValueCtx, props.value || '');
                })
                // .use(maizeTheme) // 不再需要使用maizeTheme
                .use(listener)
                .use(clipboard) // 在简化版中也添加clipboard插件
                .use(history);
              
              // 一次只添加一个commonmark插件，找到包含doc节点的那个
              if (Array.isArray(commonmark) && commonmark.length > 0) {
                console.log('尝试逐个添加commonmark插件...');
                for (let i = 0; i < commonmark.length; i++) {
                  try {
                    editor.use(commonmark[i]);
                    console.log(`成功添加commonmark[${i}]`);
                  } catch (e) {
                    console.error(`添加commonmark[${i}]失败:`, e);
                  }
                }
              }
              
              // 添加基础history插件
              try {
                editor.use(history);
              } catch (e) {
                console.error('添加history插件失败:', e);
              }
              
              // 配置基本监听器
              editor.config((ctx) => {
                try {
                  const editorListener = ctx.get(listenerCtx);
                  editorListener.markdownUpdated((_, value) => {
                    emit('update:value', value);
                  });
                } catch (e) {
                  console.error('设置监听器失败:', e);
                }
              });
              
              // 创建编辑器
              console.log('使用最小配置创建编辑器...');
              editor.create()
                .then(() => {
                  console.log('编辑器使用最小配置初始化成功');
                  editorReady = true;
                })
                .catch(minimalError => {
                  console.error('最小配置初始化也失败:', minimalError);
                });
            } catch (fallbackError) {
              console.error('编辑器降级初始化失败:', fallbackError);
            }
          });
        
        // 添加快捷键事件监听
        document.addEventListener('keydown', handleSave);
      } catch (error) {
        console.error('初始化编辑器失败:', error);
      }
    };

    // 组件挂载时初始化编辑器
    onMounted(() => {
      console.log('组件挂载，准备初始化Milkdown...');
      
      // 先进行环境检测
      const checkEnvironment = () => {
        console.log('检查编辑器环境...');
        // 检查Vue版本
        console.log('Vue版本:', Vue?.version || '未知');
        
        console.log('编辑器环境检查:', {
          Editor: typeof Editor,
          commonmark: typeof commonmark,
          gfm: typeof gfm,
          listener: typeof listener,
          history: typeof history
        });
        
        // 检查是否有可能导致加载失败的错误
        const knownErrors = [];
        if (typeof Editor !== 'function') {
          knownErrors.push('Milkdown Editor不是一个函数');
        }
        
        if (!Array.isArray(commonmark)) {
          knownErrors.push('Commonmark插件不是一个数组');
        }
        
        if (!Array.isArray(gfm)) {
          knownErrors.push('GFM插件不是一个数组');
        }
        
        // 如果有错误，记录日志但仍尝试初始化
        if (knownErrors.length > 0) {
          console.warn('发现潜在问题:', knownErrors);
        }
        
        return knownErrors.length === 0;
      };
      
      // 延迟初始化，确保DOM已完全渲染
      setTimeout(() => {
        const envOk = checkEnvironment();
        if (envOk) {
          console.log('环境检查通过，使用正常初始化');
          initEditor();
        } else {
          console.log('环境检查有问题，尝试简化方式初始化');
          // 尝试最简化的初始化方式
          try {
            if (!editorContainer.value) {
              console.log('编辑器容器为空，无法初始化');
              return;
            }
            
            // 核心最小配置
            editor = Editor.make()
              .config((ctx) => {
                ctx.set(rootCtx, editorContainer.value);
                ctx.set(defaultValueCtx, props.value || '');
              })
              // .use(maizeTheme) // 不再需要使用maizeTheme
              .use(listener)
              .use(clipboard) // 在最简化版中也添加clipboard插件
              .use(history);
            
            // 逐个添加commonmark插件来确保有doc节点
            if (Array.isArray(commonmark) && commonmark.length > 0) {
              console.log('单独添加commonmark插件...');
              for (let i = 0; i < Math.min(5, commonmark.length); i++) {
                try {
                  editor.use(commonmark[i]);
                } catch (e) {
                  console.error(`添加commonmark[${i}]失败:`, e);
                }
              }
            }
            
            // 添加基础history插件
            editor.use(history);
            
            // 配置基本监听器
            editor.config((ctx) => {
              try {
                const editorListener = ctx.get(listenerCtx);
                editorListener.markdownUpdated((_, value) => {
                  emit('update:value', value);
                });
              } catch (e) {
                console.error('设置监听器失败:', e);
              }
            });
            
            // 创建编辑器
            editor.create()
              .then(() => {
                console.log('简化初始化成功');
                editorReady = true;
              })
              .catch(error => {
                console.error('简化初始化失败:', error);
              });
          } catch (error) {
            console.error('尝试简化初始化失败:', error);
          }
        }
      }, 100);
    });

    // 组件销毁前清理编辑器
    onBeforeUnmount(() => {
      if (editor) {
        editor.destroy();
        editor = null;
      }
      editorReady = false;
      
      // 移除快捷键事件监听
      document.removeEventListener('keydown', handleSave);
    });

    return {
      editorContainer,
      undoOperation,
      redoOperation,
      insertHeading,
      toggleBold,
      toggleItalic,
      toggleStrikethrough,
      insertList,
      insertBlockquote,
      insertCodeBlock,
      insertHorizontalRule,
      insertTaskList,
      insertTable,
      insertLink,
      insertImage
    };
  }
});
</script>

<style scoped>
.milkdown-editor {
  display: flex;
  flex-direction: column;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

/* 工具栏样式 */
.editor-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  padding: 8px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #e0e0e0;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}

.toolbar-group {
  display: flex;
  gap: 5px;
  margin-right: 10px;
}

.toolbar-group:not(:last-child)::after {
  content: '';
  height: 20px;
  width: 1px;
  background-color: #ddd;
  margin-left: 10px;
}

.toolbar-group button {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 4px;
  color: #555;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  padding: 0 8px;
}

.toolbar-group button:hover {
  background-color: #e8e8e8;
  color: #333;
}

.toolbar-group button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.editor-container {
  flex: 1;
  overflow: auto;
  position: relative;
  border: 1px solid #e0e0e0;
  border-top: none;
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
  background-color: #fff;
}

/* 编辑器基础样式 */
.editor-container {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  min-height: 400px;
}

/* 使用:deep()选择器来确保样式能够穿透组件 */
:deep(.milkdown) {
  width: 100%;
  height: 100%;
}

:deep(.ProseMirror) {
  outline: none;
  padding: 16px;
  height: 100%;
  box-sizing: border-box;
}

/* Typora 风格编辑体验增强 */
:deep(.ProseMirror) {
  counter-reset: typora-h1 typora-h2 typora-h3 typora-h4 typora-h5 typora-h6;
}

/* 光标所在行高亮 */
:deep(.ProseMirror-focused .ProseMirror-gapcursor:after),
:deep(.ProseMirror-focused *[data-pm-node-active="true"]) {
  background-color: rgba(202, 68, 153, 0.05);
}

/* 标题编号 */
:deep(.ProseMirror h1:not([data-pm-node-active="true"])) {
  counter-increment: typora-h1;
  counter-reset: typora-h2 typora-h3 typora-h4 typora-h5 typora-h6;
}
:deep(.ProseMirror h2:not([data-pm-node-active="true"])) {
  counter-increment: typora-h2;
  counter-reset: typora-h3 typora-h4 typora-h5 typora-h6;
}
:deep(.ProseMirror h3:not([data-pm-node-active="true"])) {
  counter-increment: typora-h3;
  counter-reset: typora-h4 typora-h5 typora-h6;
}

/* 工具提示和菜单样式 */
:deep(.milkdown-menu) {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  z-index: 10;
}

:deep(.milkdown-menu-item) {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

:deep(.milkdown-menu-item:hover) {
  background-color: rgba(202, 68, 153, 0.1);
}

:deep(.milkdown-tooltip) {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.12);
  padding: 8px;
  z-index: 10;
}

/* 块操作插件样式 */
:deep(.milkdown-block-selector) {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  z-index: 10;
}

:deep(.milkdown-block-item) {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

:deep(.milkdown-block-item:hover),
:deep(.milkdown-block-item.active) {
  background-color: rgba(202, 68, 153, 0.1);
}

:deep(.milkdown-block-icon) {
  margin-right: 10px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #CA4499;
}

/* 斜杠命令样式 */
:deep(.milkdown-slash-search) {
  max-height: 300px;
  overflow-y: auto;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.12);
}

:deep(.milkdown-slash-item) {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

:deep(.milkdown-slash-item.active),
:deep(.milkdown-slash-item:hover) {
  background-color: rgba(202, 68, 153, 0.1);
}

/* 焦点元素样式 */
:deep(*:focus) {
  outline: none;
}

/* 标记源码模式下的元素 */
:deep([data-sourcepos]) {
  position: relative;
}

/* 链接预览 */
:deep(a:hover::after) {
  content: attr(href);
  position: absolute;
  bottom: 100%;
  left: 0;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 10;
}
</style> 