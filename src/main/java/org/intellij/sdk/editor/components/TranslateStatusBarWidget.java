package org.intellij.sdk.editor.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.Alarm;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.event.MouseEvent;

public class TranslateStatusBarWidget implements StatusBarWidget {
    private static final Icon[] ICONS = new Icon[]{
        AllIcons.Process.Step_1,
        AllIcons.Process.Step_2,
        AllIcons.Process.Step_3,
        AllIcons.Process.Step_4,
        AllIcons.Process.Step_5,
        AllIcons.Process.Step_6,
        AllIcons.Process.Step_7,
        AllIcons.Process.Step_8,
    };

    private final Project project;
    private boolean isLoading = false;
    private Icon defaultIcon;
    private int currentIconIndex = 0;
    private Alarm alarm;

    public TranslateStatusBarWidget(Project project) {
        defaultIcon = IconLoader.getIcon("/icons/icon_light.svg", TranslateStatusBarWidget.class);

        this.project = project;
        this.alarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, project);
    }

    @NotNull
    @Override
    public String ID() {
        return "TranslateStatusBarWidget";
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation() {
        return new LoadingPresentation();
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        // 可以在这里进行一些初始化操作
    }

    @Override
    public void dispose() {
        stopLoading();
        if (alarm != null) {
            alarm.dispose();
            alarm = null;
        }
    }

    public void startLoading() {
        if (!isLoading) {
            isLoading = true;
            currentIconIndex = 0;
            scheduleIconUpdate();
            updateWidget();
        }
    }

    public void stopLoading() {
        if (isLoading) {
            isLoading = false;
            if (alarm != null) {
                alarm.cancelAllRequests();
            }
            updateWidget();
        }
    }

    private void scheduleIconUpdate() {
        if (alarm != null && !alarm.isDisposed()) {
            alarm.addRequest(() -> {
                currentIconIndex = (currentIconIndex + 1) % ICONS.length;
                updateWidget();
                if (isLoading) {
                    scheduleIconUpdate();
                }
            }, 250);
        }
    }

    private void updateWidget() {
        if (project != null && !project.isDisposed()) {
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            if (statusBar != null) {
                statusBar.updateWidget(ID());
            }
        }
    }

    private class LoadingPresentation implements StatusBarWidget.IconPresentation {
        @NotNull
        @Override
        public String getTooltipText() {
            return isLoading ? "Loading..." : "AI Translate";
        }

        @Nullable
        @Override
        public Icon getIcon() {
            return isLoading ? ICONS[currentIconIndex] : defaultIcon;
        }

        @Nullable
        @Override
        public Consumer<MouseEvent> getClickConsumer() {
            return (MouseEvent e) -> {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, "AI Translate");
//                JOptionPane.showMessageDialog(null, "Status Bar Icon Clicked!");
            };
        }
    }
}

