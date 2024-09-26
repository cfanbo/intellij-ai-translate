// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.editor.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.event.HyperlinkEvent;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent extends JPanel {

    private final JPanel myMainPanel;

    // bailian
    private final JBTextField appId = new JBTextField();
    private final JBTextField appKey = new JBTextField();
//    private final JBCheckBox myIdeaUserStatus = new JBCheckBox("IntelliJ IDEA user");

    // coze
    private final JBTextField cozeBotID = new JBTextField();
    private final JBTextField cozeToken = new JBTextField();

    private String selectedProvider = "";
    private JComboBox<ComboItem> providerComboBox;

    private FormBuilder fb = FormBuilder.createFormBuilder();

    public class ComboItem {
        private final String displayName;
        private final String value;

        public ComboItem(String displayName, String value) {
            this.displayName = displayName;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return displayName;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ComboItem)) return false;
            ComboItem other = (ComboItem) obj;
            return displayName.equals(other.displayName) && value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(displayName, value);
        }
    }


    public AppSettingsComponent() {
        myMainPanel = new JPanel(new BorderLayout());

        JPanel providerFormContent = this.providerPanel();
        this.fb.addComponent(providerFormContent);


        // 添加组件到 combinedPanel
        JPanel bailianFormContent = this.bailianPanel();
        this.fb.addComponent(bailianFormContent);

        JPanel cozeFormContent = this.cozePanel();
        this.fb.addComponent(cozeFormContent);

        // 添加到主面板的中心
        myMainPanel.add(this.fb.getPanel(), BorderLayout.NORTH);
    }

    private JPanel providerPanel() {
        // 初始化下拉框组件，添加 ComboItem 对象
        providerComboBox = new ComboBox<>(new ComboItem[]{
                new ComboItem("阿里云百炼", "bailian"),
                new ComboItem("扣子", "coze"),
        });

        selectedProvider = getProvider();

        // 设置默认选择的实际值，检查用户上次的选择
        String previousProvider = selectedProvider;
        for (int i = 0; i < providerComboBox.getItemCount(); i++) {
            ComboItem item = providerComboBox.getItemAt(i);
            if (item.getValue().equals(previousProvider)) {
                providerComboBox.setSelectedItem(item);
                break;
            }
        }

        // 监听下拉框的选择事件
//        themeComboBox.addActionListener(e -> {
//            ComboItem selectedItem = (ComboItem) themeComboBox.getSelectedItem();
//            if (selectedItem != null) {
//                selectedProvider = selectedItem.getValue();
//                System.out.println("Selected Theme: " + selectedItem + ", Actual Value: " + selectedProvider);
//            }
//        });

        TitledSeparator titledSeparator = new TitledSeparator("服务提供商");

        JPanel providerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        providerPanel.add(providerComboBox);

        JPanel formContent = FormBuilder.createFormBuilder()
                .addComponent(titledSeparator)
                .addComponent(providerPanel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        return formContent;
    }

    private JEditorPane createHyperlinkPane(String text) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setText(text);

        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        return editorPane;
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return appId;
    }

    public void setProvider(String p) {
        for (int i = 0; i < providerComboBox.getItemCount(); i++) {
            ComboItem item = providerComboBox.getItemAt(i);
            if (item.getValue().equals(p)) {
                providerComboBox.setSelectedItem(item);
                break;
            }
        }
    }
    public String getProvider() {
        return ((ComboItem) providerComboBox.getSelectedItem()).getValue();
    }

    // ======== bailian settings
    public JPanel bailianPanel() {
        TitledSeparator titledSeparator = new TitledSeparator("阿里云百炼");
        JPanel formContent = FormBuilder.createFormBuilder()
                .addComponent(titledSeparator)
                .addLabeledComponent(new JBLabel("App ID:"), appId, 1, false)
                .addLabeledComponent(new JBLabel("App Key:"), appKey, 2, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        return formContent;
    }

    @NotNull
    public String getAppId() {
        return appId.getText().trim();
    }

    public void setAppId(@NotNull String newText) {
        appId.setText(newText.trim());
    }

    @NotNull
    public String getAppKey() {
        return appKey.getText().trim();
    }

    public void setAppKey(@NotNull String newText) {
        appKey.setText(newText.trim());
    }

    // =========== coze settings
    private JPanel cozePanel() {
        // coze
        TitledSeparator titledSeparator = new TitledSeparator("扣子(字节跳动)");
        String text = "<html>设置教程参考 <a href='https://github.com/cfanbo/intellij-ai-translate/'>https://github.com/cfanbo/intellij-ai-translate/</a></html>";
        JEditorPane linkPane = createHyperlinkPane(text);

        JPanel formContent = FormBuilder.createFormBuilder()
                .addComponent(titledSeparator)
                .addLabeledComponent(new JBLabel("BotID:"), cozeBotID, 1, false)
                .addLabeledComponent(new JBLabel("Token:"), cozeToken, 2, false)
                .addComponent(linkPane, 3)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        return formContent;
    }
    public void setCozeBotID(@NotNull String newBotID) {
        cozeBotID.setText(newBotID.trim());
    }
    public void setCozeToken(@NotNull String newToken) {
        cozeToken.setText(newToken.trim());
    }
    public String getCozeBotID() {
        return cozeBotID.getText().trim();
    }
    public String getCozeToken() {
        return cozeToken.getText().trim();
    }
}