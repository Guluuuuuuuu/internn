import { themeFactory } from '@milkdown/core';

// 创建一个基于Typora maize主题的Milkdown主题
export const maizeTheme = themeFactory({
  name: 'maize',
  // 定义主题变量
  variables: {
    fontFamily: '"Fira Code", "微软雅黑", "PingFang SC", "Microsoft YaHei", sans-serif',
    fontSize: '16px',
    lineHeight: '26px',
    
    // 颜色
    primaryColor: '#E49123',            // 主色调，用于强调和交互元素
    backgroundColor: '#fafafa',         // 背景色
    textColor: '#333333',               // 主要文字颜色
    secondaryTextColor: '#6a737d',      // 次要文字颜色
    codeColor: 'rgb(235, 76, 55)',      // 代码颜色
    codeBackgroundColor: '#f0f0f0',     // 代码背景色
    selectionColor: '#b981d1',          // 选中文本颜色
    blockquoteBorderColor: '#ffb11b',   // 引用边框颜色
    blockquoteBackgroundColor: '#fff9f9', // 引用背景色
    linkColor: '#E49123',               // 链接颜色
    
    // 间距
    padding: '0.5em',
    margin: '0.72em 0',
    borderRadius: '5px'
  },
  // 风格映射
  style: ({ typography, color, palette, font }) => {
    // 编辑器整体样式
    typography.root = {
      backgroundColor: palette.backgroundColor,
      color: palette.textColor,
      fontFamily: font.fontFamily,
      fontSize: font.fontSize,
      lineHeight: font.lineHeight,
      maxWidth: '914px',
      letterSpacing: '0px'
    };
    
    // 段落样式
    typography.paragraph = {
      margin: '0',
      padding: '8px 0',
      fontSize: font.fontSize,
      lineHeight: font.lineHeight,
      color: 'black'
    };
    
    // 标题样式
    typography.h1 = {
      fontSize: '2rem',
      margin: '1.2em 0',
      color: 'black',
      fontWeight: 'bold'
    };
    
    typography.h2 = {
      fontSize: '1.7rem',
      margin: '1em 0',
      color: 'black',
      fontWeight: 'bold',
      '&::before': {
        content: '"✔"',
        fontWeight: 'bold',
        color: '#ffb11b',
        marginRight: '4px'
      }
    };
    
    typography.h3 = {
      fontSize: '1.5rem',
      margin: '0.72em 0',
      color: 'black',
      fontWeight: 'bold'
    };
    
    typography.h4 = {
      fontSize: '1.25rem',
      margin: '0.72em 0',
      color: 'black',
      fontWeight: 'bold'
    };
    
    typography.h5 = {
      fontSize: '1rem',
      margin: '0.72em 0',
      color: 'black',
      fontWeight: 'bold'
    };
    
    typography.h6 = {
      fontSize: '1rem',
      margin: '0.72em 0',
      color: 'black',
      fontWeight: 'bold'
    };
    
    // 列表样式
    typography.list = {
      marginTop: '8px',
      marginBottom: '8px',
      paddingLeft: '40px',
      color: 'black',
      '& ul': {
        listStyleType: 'square'
      }
    };
    
    typography.orderedList = {
      listStyleType: 'decimal'
    };
    
    typography.bulletList = {
      listStyleType: 'disc'
    };
    
    // 加粗样式
    typography.strong = {
      color: palette.primaryColor,
      fontWeight: 'bold'
    };
    
    // 引用样式
    typography.blockquote = {
      marginBottom: '16px',
      marginTop: '16px',
      padding: '10px 10px 10px 20px',
      fontSize: '0.9em',
      background: palette.blockquoteBackgroundColor,
      borderLeft: `3px solid ${palette.blockquoteBorderColor}`,
      color: palette.secondaryTextColor,
      overflow: 'auto'
    };
    
    // 链接样式
    typography.link = {
      textDecoration: 'none',
      fontWeight: 'bold',
      color: palette.linkColor,
      borderBottom: `1px solid ${palette.linkColor}`
    };
    
    // 分割线样式
    typography.hr = {
      height: '1px',
      padding: '0',
      border: 'none',
      backgroundImage: 'linear-gradient(to right, rgba(231, 93, 109, 0.3), rgba(255, 159, 150, 0.75), rgba(255, 216, 181, 0.3))'
    };
    
    // 代码样式
    typography.code = {
      fontSize: '14px',
      color: palette.codeColor,
      backgroundColor: palette.codeBackgroundColor,
      padding: '2px 4px',
      borderRadius: '4px',
      margin: '0 2px',
      fontFamily: font.fontFamily
    };
    
    // 代码块样式
    typography.codeBlock = {
      margin: '0 0 20px',
      backgroundColor: '#292b35',
      color: 'rgb(236, 236, 236)',
      padding: '0.3em 1em',
      paddingTop: '0.4em',
      boxShadow: '0px 4px 9px grey',
      borderRadius: '5px',
      fontFamily: font.fontFamily
    };
    
    // 表格样式
    typography.table = {
      borderCollapse: 'collapse',
      margin: '16px 0',
      '& tr': {
        border: '0',
        borderTop: '1px solid #ccc',
        backgroundColor: 'white',
        '&:nth-child(2n)': {
          backgroundColor: '#F8F8F8'
        }
      },
      '& th, & td': {
        fontSize: '16px',
        lineHeight: '1.5',
        border: '1px solid #ccc',
        padding: '5px 10px',
        textAlign: 'left'
      },
      '& th': {
        fontWeight: 'bold',
        backgroundColor: '#f0f0f0'
      }
    };
    
    // 图片样式
    typography.image = {
      display: 'block',
      margin: '0 auto',
      width: '85%',
      maxWidth: '100%',
      borderRadius: '5px',
      boxShadow: '0px 4px 12px #84A1A8',
      border: '0px'
    };
    
    // 选中文本样式
    color.selection = {
      backgroundColor: palette.selectionColor,
      color: 'white'
    };
    
    return {};
  }
}); 