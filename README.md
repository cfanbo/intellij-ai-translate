# ai-translate

<div style="text-align: center;">
    <img src="https://plugins.jetbrains.com/files/25313/602650/icon/pluginIcon.svg" alt="AI Translate" />
</div>

![Build](https://github.com/cfanbo/intellij-ai-translate/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/25313.svg)](https://plugins.jetbrains.com/plugin/25313)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/25313.svg)](https://plugins.jetbrains.com/plugin/25313)

## 项目简介

<!-- Plugin description -->

这是一款基于LLM的编程开发者翻译助手。旨在帮助开发人员快速将英文翻译成中文，且保持软件开发专业术语语义。

选择英文注释段落，按 Ctrl+Alt+T 即可翻译，也可以选择要翻译的段落，右键选择“AI 翻译“ 菜单翻译。

使用插件前，需要配置Alibaba 大模型 `通义千问` 应用 AppId 和 AppKey。访问 https://bailian.console.aliyun.com/， 点击左侧栏菜单 ”我的应用“即可看到应用信息。

访问项目主页：https://github.com/cfanbo/intellij-ai-translate<br/>
<!-- Plugin description end -->

## 安装插件 

> 如果您使用的是 VSCode 开发工具，请移步 https://github.com/cfanbo/vscode-ai-translate/ 安装 `vscode-ai-translate`  对应的插件。

- 在 IDE 中搜索安装

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "ai-translate"</kbd> >
  <kbd>Install</kbd>

- 使用 JetBrains 应用市场安装

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/25313) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/25313/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- 手动安装

  Download the [latest release](https://github.com/cfanbo/intellij-ai-translate/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## 使用教程
### 1. 创建应用

首先您需要创建一个通义千问应 https://bailian.console.aliyun.com/#/app-center ，然后配置 `prompt` 告诉 AI服务器只做翻译的功能。如
```
# 角色
你是一位精通多语言的软件开发技术文档专家，专长于处理和翻译软件开发者的编码注释，能够将散落在代码中的英文注释内容整合并翻译为中文，提升可读性和理解度。
输出内容只能包含翻译后的内容，不能包括任何除翻译内容无关的文字。


## 技能
### 技能1：注释整理与去噪
- **识别与提取**：自动识别并移除以 `//` 或 `#` 开头的英文注释前缀或以‘/*'开始和 ‘*/'结尾的注释，整理成连贯的段落形式。
- **结构优化**：保持代码注释的原有逻辑结构，确保技术信息的层次清晰、易于阅读。

### 技能2：精准翻译
- **翻译技术文档**：将整理好的英文注释内容精确翻译成中文，确保技术术语的准确性与行业规范相符。
- **语境适应**：根据注释内容所涉及的技术领域，调整翻译风格以适应不同的读者群体，无论是初级开发者还是高级工程师。
- **专业术语**：针对行业专业术语或一些众所周知的单词，不需要进行翻译，如"LLM"、“TTS"等。
- **通用词组**：针对当前知名企业、产品、知识产权等，不需要进行翻译，如 "OpenAI"、"GitHub"、"Microsoft"等。
- **注意事项**: 如果单词或词组全部为大写字母，则表示有特殊意义，不进行任何翻译，保持原样。

## 限制
- 仅处理以 `//` 和 `#` 作为开始标记或以 '/*' 开始且以 ‘*/'结束的标记的英文注释。
- 翻译过程中需保持对原意的高度忠实，避免因翻译造成的技术信息偏差。 
- 不涉及对代码本身的修改或执行，专注于提升文档的可读性和信息传达效率。
# 知识库
请记住以下材料，他们可能对回答问题有帮助。
${documents}
````
其它配置项，根据需求做设置，如图所示

![img.png](static/image/tongyi-app.png)
### 2. 获取应用AppId 和 AppKey

![img.png](static/image/img.png)

### 3. 在IDE里配置 AI Translate

在IDE里配置 AI Translate，点击菜单栏 `File` -> `Settings` -> `Tools` -> `AI Translate`，填写上面获取的应用 AppId 和 AppKey。
![img.png](static/image/tongyi-appid-appkey.png)

### 4. 测试
选择文档中的注释段落，按 `Ctrl+Alt+T` 即可翻译。也可以右键选择 "AI 翻译" 菜单进行翻译。

> 如果按快捷键没有响映的话，请检查快捷键是否存在冲突，也可以在IDE里自定义快捷键。



## 说明

本插件实现的功能很简单，就是将用户选中的文本发送至 大模型 的一个API 接口，然后再将模型返回的数据在IDE客户端输出。

而至于大模型如何处理收到的数据，是我们在LLM平台后台通过 `prompt` 来控制的，用户可以一系列的灵活配置来实现自己想要的效果，还可以通过启用一些插件来实现更多的功能，如查询天气预报等功能。

如果上面的`prompt` 没有达到您想要的效果，还可以进行自由调整以达到最终想实现的效果。



Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
