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
### 1. 获取配置参数

翻译功能主要是由LLM来提供，目前支持 `阿里去百炼` 和 字节跳动`扣子(coze) `，可根据不同服务商查看获取相关配置参数：

- [阿里云百炼](./docs/bailian.md) 获取 `APP_ID` 和 `APP_KEY`
- [扣子 coze](./docs/coze.md) 获取 `BotID` 和 `token`

### 2. 在IDE里配置 AI Translate

在IDE里配置 AI Translate，点击菜单栏 `File` -> `Settings` -> `Tools` -> `AI Translate`，填写前面获取的应用信息。
![img.png](static/image/setting.png)

### 3. 测试
选择文档中的注释段落，按 `Ctrl+Alt+T` 即可翻译。也可以右键选择 "AI 翻译" 菜单进行翻译。

> 如果按快捷键没有响映的话，请检查快捷键是否存在冲突，也可以在IDE里自定义快捷键。



## 说明

本插件实现的功能很简单，就是将用户选中的文本发送至 大模型 的一个API 接口，然后再将模型返回的数据在IDE客户端输出。

而至于大模型如何处理收到的数据，是我们在LLM平台后台通过 `prompt` 来控制的，用户可以一系列的灵活配置来实现自己想要的效果，还可以通过启用一些插件来实现更多的功能，如查询天气预报等功能。

如果上面的`prompt` 没有达到您想要的效果，还可以进行自由调整以达到最终想实现的效果。



Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
