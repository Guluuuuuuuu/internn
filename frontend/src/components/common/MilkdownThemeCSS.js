// 创建一个简单的CSS注入函数，用于应用Typora主题到Milkdown编辑器
export const injectMaizeTheme = (editorRoot) => {
  if (!editorRoot) return;
  
  // 为编辑器容器添加maize主题类名
  editorRoot.classList.add('maize-theme');
  
  // 向head中插入Typora的maize样式
  const styleElement = document.createElement('style');
  styleElement.setAttribute('type', 'text/css');
  styleElement.setAttribute('id', 'milkdown-maize-theme');
  styleElement.textContent = `
    /* Maize主题 - 基于Typora maize.css */
    .milkdown.maize-theme .editor {
      background-color: #fafafa;
      color: #333333;
      font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
      font-size: 16px;
      line-height: 26px;
    }

    /* 选中文本样式 */
    .milkdown.maize-theme .editor ::selection {
      background: #b981d1;
      color: white;
    }

    /* 段落样式 */
    .milkdown.maize-theme .editor p {
      padding-top: 8px;
      padding-bottom: 8px;
      margin: 0;
      line-height: 26px;
      color: black;
    }

    /* 标题样式 */
    .milkdown.maize-theme .editor h1,
    .milkdown.maize-theme .editor h2,
    .milkdown.maize-theme .editor h3,
    .milkdown.maize-theme .editor h4,
    .milkdown.maize-theme .editor h5,
    .milkdown.maize-theme .editor h6 {
      margin: 0.72em 0;
      padding: 0px;
      font-weight: bold;
      color: black;
    }

    .milkdown.maize-theme .editor h1 {
      margin: 1.2em 0;
      font-size: 2rem;
    }

    .milkdown.maize-theme .editor h2::before {
      content: "✔";
      font-weight: bold;
      color: #ffb11b;
      margin-right: 4px;
    }

    .milkdown.maize-theme .editor h2 {
      font-size: 1.7rem;
      margin: 1em 0;
    }

    .milkdown.maize-theme .editor h3 {
      font-size: 1.5rem;
    }

    .milkdown.maize-theme .editor h4 {
      font-size: 1.25rem;
    }

    /* 列表样式 */
    .milkdown.maize-theme .editor ul,
    .milkdown.maize-theme .editor ol {
      margin-top: 8px;
      margin-bottom: 8px;
      padding-left: 40px;
      color: black;
    }

    .milkdown.maize-theme .editor ul {
      list-style-type: disc;
    }

    .milkdown.maize-theme .editor ul ul {
      list-style-type: square;
    }

    .milkdown.maize-theme .editor ol {
      list-style-type: decimal;
    }

    /* 加粗样式 */
    .milkdown.maize-theme .editor strong {
      color: #E49123;
      font-weight: bold;
    }

    /* 引用样式 */
    .milkdown.maize-theme .editor blockquote {
      margin-bottom: 16px;
      margin-top: 16px;
      padding: 10px 10px 10px 20px;
      font-size: 0.9em;
      background: #fff9f9;
      border-left: 3px solid #ffb11b;
      color: #6a737d;
      overflow: auto;
    }

    /* 链接样式 */
    .milkdown.maize-theme .editor a {
      text-decoration: none;
      font-weight: bold;
      color: #E49123;
      border-bottom: 1px solid #E49123;
    }

    /* 分割线样式 */
    .milkdown.maize-theme .editor hr {
      height: 1px;
      padding: 0;
      border: none;
      background-image: linear-gradient(to right, rgba(231, 93, 109, 0.3), rgba(255, 159, 150, 0.75), rgba(255, 216, 181, 0.3));
    }

    /* 行内代码样式 */
    .milkdown.maize-theme .editor code {
      font-size: 14px;
      color: rgb(235, 76, 55);
      background-color: #f0f0f0;
      padding: 2px 4px;
      border-radius: 4px;
      margin: 0 2px;
    }

    /* 代码块样式 */
    .milkdown.maize-theme .editor pre {
      margin: 0 0 20px;
      background-color: #292b35;
      color: rgb(236, 236, 236);
      padding: 0.3em 1em;
      padding-top: 0.4em;
      box-shadow: 0px 4px 9px grey;
      border-radius: 5px;
    }

    /* 表格样式 */
    .milkdown.maize-theme .editor table {
      border-collapse: collapse;
      margin: 16px 0;
    }

    .milkdown.maize-theme .editor table tr {
      border: 0;
      border-top: 1px solid #ccc;
      background-color: white;
    }

    .milkdown.maize-theme .editor table tr:nth-child(2n) {
      background-color: #F8F8F8;
    }

    .milkdown.maize-theme .editor table th,
    .milkdown.maize-theme .editor table td {
      font-size: 16px;
      line-height: 1.5;
      border: 1px solid #ccc;
      padding: 5px 10px;
      text-align: left;
    }

    .milkdown.maize-theme .editor table th {
      font-weight: bold;
      background-color: #f0f0f0;
    }

    /* 图片样式 */
    .milkdown.maize-theme .editor img {
      display: block;
      margin: 0 auto;
      width: 85%;
      max-width: 100%;
      border-radius: 5px;
      box-shadow: 0px 4px 12px #84A1A8;
      border: 0px;
    }
  `;
  
  // 如果已存在相同ID的样式元素，则先移除
  const existingStyle = document.getElementById('milkdown-maize-theme');
  if (existingStyle) {
    existingStyle.parentNode.removeChild(existingStyle);
  }
  
  // 添加新样式到head
  document.head.appendChild(styleElement);
  
  return () => {
    // 返回清理函数
    const styleToRemove = document.getElementById('milkdown-maize-theme');
    if (styleToRemove) {
      styleToRemove.parentNode.removeChild(styleToRemove);
    }
    editorRoot.classList.remove('maize-theme');
  };
}; 