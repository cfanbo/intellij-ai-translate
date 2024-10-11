package org.intellij.sdk.editor.components;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import org.jetbrains.annotations.NotNull;

public class TranslateStatusBarWidgetFactory implements StatusBarWidgetFactory {

    @Override
    public @NotNull String getId() {
        return "TranslateStatusBarWidget";
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Translate Status Bar getDisplayName";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;  // 控制是否在项目中显示
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new TranslateStatusBarWidget(project);  // 创建状态栏组件
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        widget.dispose();  // 清理资源
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;  // 控制是否允许在状态栏上启用
    }
}

