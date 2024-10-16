// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.editor.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.FormBuilder;
import org.intellij.sdk.editor.config.LlmConfig;
import org.intellij.sdk.editor.config.Store;
import org.intellij.sdk.editor.config.StoreRecord;
import org.intellij.sdk.editor.provider.Ollama;
import org.intellij.sdk.editor.util.ComboxItem;
import org.intellij.sdk.editor.util.Lang;
import org.intellij.sdk.editor.util.Provider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent extends JPanel {
    private final JPanel myMainPanel;

    // bailian
    private final JBTextField appId = new JBTextField();
    private final JBTextField appKey = new JBTextField();
    private final JBCheckBox streamStatus = new JBCheckBox("Enable Stream Output");

    // coze
    private final JBTextField cozeBotID = new JBTextField();
    private final JBTextField cozeToken = new JBTextField();

    private String selectedProvider = "";
    private JComboBox<ComboxItem> providerComboBox;

    // v1
    private JComboBox<ComboxItem> targetLangComboBox = new JComboBox<>();
    private ComboBox<ComboxItem> serviceComboBox = new ComboBox<>();
    private ComboBox<String> configModelComboBox = new ComboBox<>();

    private JBTextField configBaseUrl = new JBTextField();
    private JBPasswordField configApiKey = new JBPasswordField();
    private JEditorPane configDescriptionText = new JEditorPane();

    private final JBTextField configTemperature = new JBTextField();
    private final JBTextField configMaxTokens = new JBTextField();
    ExpandableTextField configPrompt = new ExpandableTextField();

    // modelPanel
    private JLabel modelPanelLable = new JLabel("Model:");
    private JPanel modelPanel = new JPanel(new FlowLayout());

    // panel
    private JPanel bailianPanel;
    private JPanel cozePanel;
    private JPanel llmPanel;

    private FormBuilder fb = FormBuilder.createFormBuilder();

    private Lang lang;
    private Provider provider;

    public AppSettingsComponent() {
        this.lang = new Lang(this.targetLangComboBox);
        this.provider = new Provider(this.serviceComboBox);

        {
            configDescriptionText.setContentType("text/html");
            configDescriptionText.setEditable(false);
            configDescriptionText.setOpaque(false);
            configDescriptionText.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
//            configDescriptionText.setText(text);

            configDescriptionText.addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        configModelComboBox.setEditable(true);
        serviceComboBox.addActionListener(e -> {
            String selectedValue = this.provider.getSelectedItem();

            StoreRecord item = this.provider.getModelRecord(selectedValue);

            this.cozePanel.setVisible(false);
            this.bailianPanel.setVisible(false);
            this.llmPanel.setVisible(false);

            if (item.isAgent()) {
                this.llmPanel.setVisible(false);

                if ("Coze".equals(selectedValue)) {
                    this.cozePanel.setVisible(true);
                } else {
                    this.bailianPanel.setVisible(true);
                }
            } else {
                this.llmPanel.setVisible(true);

                configBaseUrl.setText(item.baseUrl);
                configApiKey.setText(item.apiKey);
//                configApiKey.getEmptyText().setText(item.apiKeyPlaceholder);
                if (item.description.isEmpty()) {
                    configDescriptionText.setVisible(false);
                } else {
                    configDescriptionText.setVisible(true);
                    configDescriptionText.setText(item.description);
                }

                if (item.provider.equals("Ollama")) {
                    // fetch current support models list
                    item.models = Ollama.getModelList(configBaseUrl.getText().trim());
                }

                if (item.models != null && !item.models.isEmpty()) {
                    configModelComboBox.setModel(new DefaultComboBoxModel<>(item.models.toArray(new String[0])));
                } else {
                    configModelComboBox.setModel(new DefaultComboBoxModel<>(new String[]{}));
                }

                if ("DeepL".equals(item.provider)) {
                    this.setModelPanelVisible(false);
                } else {
                    this.setModelPanelVisible(true);
                }
            }
        });

        fb.addLabeledComponent(new JBLabel("Target Language:"), targetLangComboBox).addComponent(streamStatus);

        TitledSeparator titledSeparator = new TitledSeparator("");
        fb.addComponent(titledSeparator);

        fb.addLabeledComponent(new JBLabel("Service Provider:"), serviceComboBox);

        String selectedProvider = this.provider.getSelectedItem().toString();

        // bailian
        this.bailianPanel = this.bailianPanel();
        fb.addComponent(this.bailianPanel);
        if ("BaiLian".equals(selectedProvider)) {
            this.bailianPanel.setVisible(true);
        } else {
            this.bailianPanel.setVisible(false);
        }

        // coze
        this.cozePanel = this.cozePanel();
        fb.addComponent(this.cozePanel);
        if ("Coze".equals(selectedProvider)) {
            this.cozePanel.setVisible(true);
        } else {
            this.cozePanel.setVisible(false);
        }

        // llm
        this.llmPanel = this.llmPanel();
        if (!selectedProvider.equals("BaiLian") && !selectedProvider.equals("Coze")) {
            if ("DeepL".equals(selectedProvider)) {
                this.setModelPanelVisible(false);
            } else {
                this.setModelPanelVisible(true);
            }

            this.llmPanel.setVisible(true);
        } else {
            this.llmPanel.setVisible(false);
        }
        fb.addComponent(this.llmPanel);

        // 添加到主面板的中心
        myMainPanel = new JPanel(new BorderLayout());
        myMainPanel.add(fb.getPanel(), BorderLayout.NORTH);
    }

    private void setModelPanelVisible(boolean flag) {
        this.modelPanelLable.setVisible(flag);
        this.modelPanel.setVisible(flag);
    }

    private JPanel getModelPanel() {
        this.modelPanel.setLayout(new BoxLayout(this.modelPanel, BoxLayout.X_AXIS));
        this.modelPanel.add(configModelComboBox);

        return this.modelPanel;
    }

    private JPanel llmPanel() {
        FormBuilder fb = FormBuilder.createFormBuilder();

        fb.addLabeledComponent(this.modelPanelLable, this.getModelPanel());

        configMaxTokens.setColumns(6);
        configTemperature.setColumns(6);

        // 限制输入内容为数字 TODO
        JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jp.add(new JBLabel("Max Tokens:"));
        jp.add(configMaxTokens);
        jp.add(new JBLabel("Temperature:"));
        jp.add(configTemperature);

        jp.setVisible(false);

        configPrompt.setPreferredSize(new Dimension(400, 100));
        configPrompt.setVisible(false);

        fb.addLabeledComponent(new JBLabel("API Key:"), configApiKey)
                .addLabeledComponent(new JLabel("Endpoint:"), configBaseUrl)
                .addComponent(configDescriptionText)
                .addComponent(jp);

        return fb.getPanel();
    }

    private JPanel providerPanel() {
        JPanel formContent = FormBuilder.createFormBuilder()
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
//        for (int i = 0; i < providerComboBox.getItemCount(); i++) {
//            ComboxItem item = providerComboBox.getItemAt(i);
//            if (item.getValue().equals(p)) {
//                providerComboBox.setSelectedItem(item);
//                break;
//            }
//        }
    }

    public String getProvider() {
        LlmConfig llmConfig = this.getLlmConfig();

        return llmConfig.provider;
    }

    // stream
    public boolean getStreamStatus() {
        return streamStatus.isSelected();
    }

    public void setStreamStatus(boolean enable) {
        streamStatus.setSelected(enable);
    }

    // ======== bailian settings
    public JPanel bailianPanel() {
        TitledSeparator titledSeparator = new TitledSeparator("阿里云百炼");

        String text = "<html>设置教程参考 <a href='https://github.com/cfanbo/intellij-ai-translate/'>https://github.com/cfanbo/intellij-ai-translate/</a></html>";
        JEditorPane linkPane = createHyperlinkPane(text);

        JPanel formContent = FormBuilder.createFormBuilder()
                .addComponent(titledSeparator)
                .addLabeledComponent(new JBLabel("App ID:"), appId, 1, false)
                .addLabeledComponent(new JBLabel("App Key:"), appKey, 2, false)
                .addComponent(linkPane, 3)
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
        StoreRecord record = Store.getInstance().getRecord("BaiLian");
        record.setModel(newText.trim());
    }

    @NotNull
    public String getAppKey() {
        return appKey.getText().trim();
    }

    public void setAppKey(@NotNull String newText) {
        appKey.setText(newText.trim());

        StoreRecord record = Store.getInstance().getRecord("BaiLian");
        record.setApiKey(newText.trim());
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

        StoreRecord record = Store.getInstance().getRecord("Coze");
        record.setModel(newBotID.trim());
    }

    public void setCozeToken(@NotNull String newToken) {
        cozeToken.setText(newToken.trim());

        StoreRecord record = Store.getInstance().getRecord("Coze");
        record.setApiKey(newToken.trim());
    }

    public String getCozeBotID() {
        return cozeBotID.getText().trim();
    }

    public String getCozeToken() {
        return cozeToken.getText().trim();
    }

    public String getTargetLang() {
        return this.lang.getSelectedItem();
    }

    public void setTargetLang(String value) {
        this.lang.setSelectedItem(value);
    }

    public LlmConfig getLlmConfig() {
        int maxTokens = 1024;
        if (!configMaxTokens.getText().trim().isEmpty()) {
            maxTokens = Integer.parseInt(configMaxTokens.getText().trim());
        }

        double temperature = 0;
        if (!configTemperature.getText().trim().isEmpty()) {
            temperature = Double.parseDouble(configTemperature.getText().trim());
        }

        String modelValue = "";
        if (configModelComboBox.getSelectedItem() != null) {
            modelValue = configModelComboBox.getSelectedItem().toString();
        }

        return new LlmConfig(
                this.provider.getSelectedItem(),
                configBaseUrl.getText().trim(),
                String.copyValueOf(configApiKey.getPassword()),
                modelValue,
                configPrompt.getText().trim(),
                maxTokens,
                temperature
        );
    }

    public void setLlmConfig(LlmConfig config) {
        this.provider.setSelectedItem(config.provider);
        if (configModelComboBox.getItem() != null) {
            configModelComboBox.setSelectedItem(config.model);
        }

        configBaseUrl.setText(config.baseUrl);
        configApiKey.setText(config.apiKey);
        configPrompt.setText(config.prompt);
        configMaxTokens.setText(String.valueOf(config.maxTokens));
        configTemperature.setText(String.valueOf(config.temperature));
    }

    public String getStoreString() {
        Store store = Store.getInstance();

        LlmConfig llmConfig = this.getLlmConfig();
        StoreRecord record = store.getRecord(llmConfig.provider);
        if (record.isAgent()) {
            if (record.provider.equals("Coze")) {
                record.setApiKey(this.getCozeToken())
                        .setModel(this.getCozeBotID());
            } else {
                record.setApiKey(this.getAppKey())
                        .setModel(this.getAppId());
            }

        } else {
            record.setApiKey(llmConfig.apiKey)
                    .setBaseUrl(llmConfig.baseUrl)
                    .setProvider(llmConfig.provider)
                    .setModel(llmConfig.model);
        }
        String storeString = store.toJSONString();

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue("store", storeString);
        return storeString;
    }

}