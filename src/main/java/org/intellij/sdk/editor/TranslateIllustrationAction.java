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
import org.intellij.sdk.editor.util.Helper;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;

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

        final Document document = editor.getDocument();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
//    int start = primaryCaret.getSelectionStart();
//    int end = primaryCaret.getSelectionEnd();
        String input = primaryCaret.getSelectedText();

        Helper.setProject(project);

        try {
            LLmService llm = LLmFactory.getInstance(config);
            llm.callAgentApp(input);
        } catch (ConfigurationException configErr) {
            System.out.println(configErr.getMessage());
            Helper.promptUserToConfigure(configErr.getMessage());
        } catch (Exception ex) {
            Helper.printToConsole("Error: " + ex.getMessage());
            return;
        }
//        Helper.printToConsole(resultStr);
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
        // 动态设置不同主题下的图标
        Presentation presentation = e.getPresentation();
        if (JBColor.isBright()) {
            // 如果是亮色主题
            presentation.setIcon(IconLoader.getIcon("/icons/icon_light.svg", getClass()));
        } else {
            // 如果是暗色主题
            presentation.setIcon(IconLoader.getIcon("/icons/icon_dark.svg", getClass()));
        }

        // Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        // Set visibility and enable only in case of existing project and editor and if a selection exists
        e.getPresentation().setEnabledAndVisible(
                project != null && editor != null && editor.getSelectionModel().hasSelection()
        );
    }

}
