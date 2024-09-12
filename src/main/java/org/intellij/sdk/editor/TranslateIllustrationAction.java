// Copyright 2000-2023 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.execution.ui.ConsoleView;
import org.intellij.sdk.editor.settings.AppSettings;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.ui.Messages;
/**
 * Menu action to replace a selection of characters with a fixed string.
 */
public class TranslateIllustrationAction extends AnAction {

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

  /**
   * Replaces the run of text selected by the primary caret with a fixed string.
   *
   * @param e Event related to this action
   */
  @Override
  public void actionPerformed(@NotNull final AnActionEvent e) {
    // Get all the required data from data keys
    // Editor and Project were verified in update(), so they are not null.
    final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
    final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

    AppSettings settings = AppSettings.getInstance();
    AppSettings.State config = settings.getState();
    String appId = config.appId;
    String appKey = config.appKey;
    if (appId.isEmpty() || appKey.isEmpty()) {
      promptUserToConfigure(project);
      return;
    }

    final Document document = editor.getDocument();
    // Work off of the primary caret to get the selection info
    Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
//    int start = primaryCaret.getSelectionStart();
//    int end = primaryCaret.getSelectionEnd();

    String resultStr = "";
    LlmService llm = LlmService.getInstance();
    try {
      String input = primaryCaret.getSelectedText();
      resultStr = llm.callAgentApp(input);
    } catch (Exception ex) {
      printToConsole(project, "Error: " + ex.getMessage());
      return;
    }
    printToConsole(project, resultStr);
  }

  public static void promptUserToConfigure(Project project) {
    // 弹出提示框，询问用户是否要立即配置
    int result = Messages.showYesNoDialog(
            project,
            "Would you like to configure AiTranslate?",
            "Configure AiTranslate",
            Messages.getQuestionIcon()
    );

    // 如果用户选择“是”，打开设置页面
    if (result == Messages.YES) {
      // 打开插件的设置页面
      ShowSettingsUtil.getInstance().showSettingsDialog(project, "AiTranslate Setting");
    }
  }

  public void printToConsole(Project project, String str) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("AiTranslate");
    if (toolWindow != null) {
      toolWindow.activate(() -> {
        // 访问工具窗口内容
        ConsoleView consoleView = getConsoleViewFromToolWindow(toolWindow);
        if (consoleView != null) {
          consoleView.clear();
          consoleView.print(str, ConsoleViewContentType.NORMAL_OUTPUT);
        }
      });
    }
  }

  private ConsoleView getConsoleViewFromToolWindow(ToolWindow toolWindow) {
    // 获取工具窗口内容并检查其类型
    if (toolWindow.getContentManager().getContentCount() > 0) {
      Object component = toolWindow.getContentManager().getContent(0).getComponent();
      if (component instanceof ConsoleView) {
        return (ConsoleView) component;
      }
    }
    return null;
  }
  /**
   * Sets visibility and enables this action menu item if:
   * <ul>
   *   <li>a project is open</li>
   *   <li>an editor is active</li>
   *   <li>some characters are selected</li>
   * </ul>
   *
   * @param e Event related to this action
   */
  @Override
  public void update(@NotNull final AnActionEvent e) {
    // Get required data keys
    final Project project = e.getProject();
    final Editor editor = e.getData(CommonDataKeys.EDITOR);
    // Set visibility and enable only in case of existing project and editor and if a selection exists
    e.getPresentation().setEnabledAndVisible(
        project != null && editor != null && editor.getSelectionModel().hasSelection()
    );
  }

}
