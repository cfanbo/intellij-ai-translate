package org.intellij.sdk.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;

public class AiTranslateConsoleToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // 创建 ConsoleView
        ConsoleView consoleView = new ConsoleViewImpl(project, false);

        // 将 ConsoleView 嵌入到工具窗口中
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(consoleView.getComponent(), "Console", false);
        contentManager.addContent(content);

        // 输出一些文本到 ConsoleView
        // consoleView.print("Hello from My Plugin!\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }
}
