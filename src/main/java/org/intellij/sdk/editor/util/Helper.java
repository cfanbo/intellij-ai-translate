package org.intellij.sdk.editor.util;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.*;
import org.intellij.sdk.editor.components.TranslateStatusBarWidget;

public class Helper {
    private static Project project; // 单例 Project 对象
    private static ToolWindow toolWindow;
    private static TranslateStatusBarWidget translateStatusBarWidget;

    public static void setProject(Project project) {
        Helper.project = project;
        Helper.toolWindow = ToolWindowManager.getInstance(project).getToolWindow("AiTranslate");

        // statusBar
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar != null) {
            StatusBarWidget widget = statusBar.getWidget("TranslateStatusBarWidget");
            if (widget instanceof TranslateStatusBarWidget) {
                Helper.translateStatusBarWidget = (TranslateStatusBarWidget) widget;
            }
        }
    }

    public static void printToConsole(String str) {
        if (toolWindow != null) {
            toolWindow.activate(() -> {
                // 访问工具窗口内容
                ConsoleView consoleView = getConsoleViewFromToolWindow(toolWindow);
                if (consoleView != null) {
                    consoleView.print(str, ConsoleViewContentType.NORMAL_OUTPUT);
                    consoleView.requestScrollingToEnd();

                    // loading status
                    statusBarLoadingStatus();
                }
            });
        }
    }

    public static void printFinished() {
        if (toolWindow != null) {
            toolWindow.activate(() -> {
                // 访问工具窗口内容
                ConsoleView consoleView = getConsoleViewFromToolWindow(toolWindow);
                if (consoleView != null) {
                    consoleView.print("\r\n\r\n", ConsoleViewContentType.NORMAL_OUTPUT);
                    consoleView.requestScrollingToEnd();
                }

                // loading status
                statusBarDefaultStatus();
            });
        }
    }

    public static void clearConsole() {
        if (toolWindow != null) {
            toolWindow.activate(() -> {
                ConsoleView consoleView = getConsoleViewFromToolWindow(toolWindow);
                consoleView.clear();
            });
        }
    }

    public static void promptUserToConfigure(String message) {
        // 弹出提示框，询问用户是否要立即配置
        int result = Messages.showYesNoDialog(
                project,
                message + "\n Would you like to configure AiTranslate?",
                "Configure AiTranslate",
                Messages.getQuestionIcon()
        );

        if (result == Messages.YES) {
            // 打开插件的设置页面
            ShowSettingsUtil.getInstance().showSettingsDialog(project, "AI Translate");
        }
    }

    public static ConsoleView getConsoleViewFromToolWindow(ToolWindow toolWindow) {
        // 获取工具窗口内容并检查其类型
        if (toolWindow.getContentManager().getContentCount() > 0) {
            Object component = toolWindow.getContentManager().getContent(0).getComponent();
            if (component instanceof ConsoleView) {
                return (ConsoleView) component;
            }
        }
        return null;
    }

    public static void statusBarLoadingStatus() {
        // 获取当前项目的 StatusBar
        if (translateStatusBarWidget != null) {
                translateStatusBarWidget.startLoading();
        }
    }

    private static void statusBarDefaultStatus() {
        if (translateStatusBarWidget != null) {
            translateStatusBarWidget.stopLoading();
        }
    }
}
