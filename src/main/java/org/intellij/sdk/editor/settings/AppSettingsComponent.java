// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.intellij.sdk.editor.settings;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.FormBuilder;
import org.intellij.sdk.editor.config.LlmConfig;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent extends JPanel {
    String jsonContent = """
            {
                    bailian: {
                             provider: "BaiLian",
                             baseurl: 'https://api.openai.com/v1',
                             apikey: 'OpenAI',
                             models: [],
                             description: "alibaba",
                             type: "aiagent"
                     },
                     coze: {
                             provider: "Coze",
                             baseurl: 'https://api.openai.com/v1',
                             apikey: 'OpenAI',
                             models: [],
                             description: "coze",
                             type: "aiagent"
                     },
                    openai: {
                     		name: "OpenAI",
                     		baseurl: 'https://api.openai.com/v1',
                     		apikey: 'OpenAI',
                     		models: ["gpt-3.5-turbo", "gpt-3.5-turbo-0125", "gpt-3.5-turbo-1106", "gpt-3.5-turbo-0613", "gpt-4-1106-preview", "gpt-4-0125-preview", "gpt-4o", "gpt-4o-mini", "gpt-4-turbo", "gpt-4"],
                     		description: "openai"
                     	},
                     anthropic: {
                             provider: "Anthropic",
                             baseurl: 'https://api.anthropic.com/',
                             apikey: 'Anthropic',
                             models: ['claude-3-5-sonnet-20240620', 'claude-3-haiku-20240307', 'claude-3-opus-20240229', 'claude-3-sonnet-20240229'],
                             description: "https://docs.anthropic.com/en/docs/welcome"
                     },
                     deepl: {
                             provider: "DeepL",
                             baseurl: 'https://api-free.deepl.com',
                             apikey: '',
                             models: [],
                             description: "https://www.deepl.com/"
                     },
                     glm: {
                             provider: "GLM",
                             baseurl: "https://open.bigmodel.cn/api/paas/v4",
                             apikey: 'GLM',
                             models: ['codegeex-4', 'glm-4-flash', 'glm-4v-plus', 'glm-4-0520', 'glm-4-long', 'glm-4v', 'glm-4-air', 'glm-4', 'glm-4-9b', 'glm-4-flashx'],
                             description: ""
                     },
                     doubao: {
                             provider: "Doubao",
                             baseurl: 'https://ark.cn-beijing.volces.com/api/v3',
                             apikey: 'Doubao',
                             models: [],
                             description: ""
                     },
                     deepseek: {
                             provider: "Deepseek",
                             baseurl: 'https://api.deepseek.com',
                             apikey: 'Deepseek',
                             models: ["deepseek-chat", "deepseek-coder"],
                             description: "https://platform.deepseek.com/api_keys"
                     },
                     dashscope: {
                             provider: "Dashscope",
                             baseurl: 'https://dashscope.aliyuncs.com/compatible-mode/v1',
                             apikey: 'Dashscope',
                             models: ['qwen-max', 'qwen-plus', 'qwen-turbo', 'qwen-long'],
                             description: ""
                     },
                     github: {
                             provider: "GitHub",
                             baseurl: 'https://models.inference.ai.azure.com',
                             apikey: 'github',
                             models: ['gpt-4o', 'gpt-4o-mini', "Meta-Llama-3.1-405B-Instruct", "Meta-Llama-3.1-70B-Instruct", "Meta-Llama-3.1-8B-Instruct", "Meta-Llama-3-70B-Instruct", "Meta-Llama-3-8B-Instruct", "Mistral-Nemo", "Mistral-large", "Mistral-large-2407", "Mistral-small"],
                             description: "https://gh.io/models"
                     },
                     gemini: {
                             provider: "Gemini",
                             baseurl: '',
                             apikey: '',
                             models: ['gemini-1.0-pro-latest', 'gemini-1.0-pro-002', "gemini-1.0-pro-001", "gemini-1.5-pro-latest", "gemini-1.5-flash"],
                             description: ""
                     },
            }
            """;

    String langList = """
                         "Simplified Chinese",
                                     "Traditional Chinese",
            //                         "English",
                                     "Japanese",
                                     "Korean",
                                     "Spanish",
                                     "German",
                                     "French",
                                     "Portuguese",
                                     "Portuguese (Brazil)",
                                     "Russian",
                                     "Arabic",
                                     "Italian",
                                     "Malay",
                                     "Indonesian",
                                     "Vietnamese",
                                     "Afrikaans",
                                     "Thai",
                                     "Urdu",
                                     "Cantonese (Traditional)",
                                     "Northeastern Chinese",
                                     "Tibetan",
                                     "Classical Chinese",
                                     "Amharic",
                                     "Azerbaijani",
                                     "Belarusian",
                                     "Bulgarian",
                                     "Bengali",
                                     "Bosnian",
                                     "Catalan",
                                     "Cebuano",
                                     "Corsican",
                                     "Czech",
                                     "Welsh",
                                     "Danish",
                                     "Greek",
                                     "Esperanto",
                                     "Estonian",
                                     "Basque",
                                     "Persian",
                                     "Finnish",
                                     "Filipino",
                                     "Fijian",
                                     "Frisian",
                                     "Irish",
                                     "Scottish Gaelic",
                                     "Galician",
                                     "Gujarati",
                                     "Hausa",
                                     "Hawaiian",
                                     "Hebrew",
                                     "Hindi",
                                     "Hmong",
                                     "Croatian",
                                     "Haitian Creole",
                                     "Hungarian",
                                     "Armenian",
                                     "Igbo",
                                     "Icelandic",
                                     "Javanese",
                                     "Georgian",
                                     "Kazakh",
                                     "Khmer",
                                     "Kannada",
                                     "Kurdish",
                                     "Kyrgyz",
                                     "Latin",
                                     "Luxembourgish",
                                     "Lao",
                                     "Lithuanian",
                                     "Latvian",
                                     "Malagasy",
                                     "Maori",
                                     "Macedonian",
                                     "Malayalam",
                                     "Mongolian",
                                     "Marathi",
                                     "Maltese",
                                     "Burmese",
                                     "Dutch",
                                     "Punjabi",
                                     "Polish",
                                     "Pashto",
                                     "Romanian",
                                     "Sanskrit",
                                     "Sinhala",
                                     "Slovak",
                                     "Slovenian",
                                     "Samoan",
                                     "Shona",
                                     "Somali",
                                     "Albanian",
                                     "Serbian",
                                     "Serbian (Cyrillic)",
                                     "Serbian (Latin)",
                                     "Sesotho",
                                     "Sundanese",
                                     "Swedish",
                                     "Swahili",
                                     "Tamil",
                                     "Telugu",
                                     "Tajik",
                                     "Turkish",
                                     "Uyghur",
                                     "Ukrainian",
                                     "Uzbek",
                                     "Xhosa",
                                     "Yiddish",
                                     "Yoruba",
                                     "Zulu",
                                     "Roman Urdu"
            """;

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
    private ComboBox<String> serviceComboBox;
    private ComboBox<String> configModelComboBox = new ComboBox<>();

    private JBTextField configBaseUrl = new JBTextField();
    private JBTextField configApiKey = new JBTextField();
    //    private JBTextField configModel = new JBTextField();
    //    private JBTextField configPrompt = new JBTextField();
    ExpandableTextField configPrompt = new ExpandableTextField();
    private final JBTextField configTemperature = new JBTextField();
    private final JBTextField configMaxTokens = new JBTextField();


    // panel
    private JPanel bailianPanel;
    private JPanel cozePanel;
    private JPanel llmPanel;

    private Map<String, JSONObject> serviceModelsMap = new HashMap<>();

    private FormBuilder fb = FormBuilder.createFormBuilder();

    public class ComboxItem {
        private final String displayName;
        private final String value;

        public ComboxItem(String displayName, String value) {
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
            if (!(obj instanceof ComboxItem)) return false;
            ComboxItem other = (ComboxItem) obj;
            return displayName.equals(other.displayName) && value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(displayName, value);
        }
    }


    private void initTargetLang() {
        String[] lines = langList.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            line = line.replaceAll("\"", "").replaceAll(",", "").trim(); // 去掉单引号和逗号
            ComboxItem item = new ComboxItem(line, line);
            targetLangComboBox.addItem(item);
        }
    }

    public AppSettingsComponent() {
        try {
            loadServiceModels();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.initTargetLang();

        configModelComboBox.setEditable(true);

        serviceComboBox = new ComboBox<>(serviceModelsMap.keySet().toArray(new String[0]));
        serviceComboBox.addActionListener(e -> {
            String selectedService = (String) serviceComboBox.getSelectedItem();
            JSONObject serviceData = serviceModelsMap.get(selectedService);

            this.cozePanel.setVisible(false);
            this.bailianPanel.setVisible(false);
            this.llmPanel.setVisible(false);

            String selectedProvider = serviceData.getString("provider");
            if (serviceData.containsKey("type") && "aiagent".equals(serviceData.getString("type"))) {
                // 智能体
                this.llmPanel.setVisible(false);

                if ("Coze".equals(selectedProvider)) {
                    this.cozePanel.setVisible(true);
                } else {
                    this.bailianPanel.setVisible(true);
                }
            } else {
                this.llmPanel.setVisible(true);
                configBaseUrl.setText(serviceData.getString("baseurl"));

                if (serviceData != null && serviceData.containsKey("models")) {
                    List<String> modelList = serviceData.getJSONArray("models").toJavaList(String.class);
                    configModelComboBox.setModel(new DefaultComboBoxModel<>(modelList.toArray(new String[0])));
                } else {
                    configModelComboBox.setModel(new DefaultComboBoxModel<>(new String[]{}));
                }

                configModelComboBox.setVisible(true);
            }
        });

        fb.addLabeledComponent(new JBLabel("target Language:"), targetLangComboBox).addComponent(streamStatus);

        TitledSeparator titledSeparator = new TitledSeparator("");
        fb.addComponent(titledSeparator);

        fb.addLabeledComponent(new JBLabel("Service Provider:"), serviceComboBox);

        String selectedProvider = serviceComboBox.getSelectedItem().toString();

        // bailian
        this.bailianPanel = this.bailianPanel();
        fb.addComponent(this.bailianPanel);
        if ("bailian".equals(selectedProvider)) {
            this.bailianPanel.setVisible(false);
        }

        // coze
        this.cozePanel = this.cozePanel();
        fb.addComponent(this.cozePanel);
        if ("coze".equals(selectedProvider)) {
            this.cozePanel.setVisible(false);
        }

        // llm
        this.llmPanel = this.llmPanel();
        fb.addComponent(this.llmPanel);

        // 添加到主面板的中心
        myMainPanel = new JPanel(new BorderLayout());
        myMainPanel.add(fb.getPanel(), BorderLayout.NORTH);
    }

    private JPanel llmPanel() {
        FormBuilder fb = FormBuilder.createFormBuilder();

        JPanel modelPanel = new JPanel(new FlowLayout());
        modelPanel.setLayout(new BoxLayout(modelPanel, BoxLayout.X_AXIS));
        modelPanel.add(configModelComboBox);
//        modelPanel.add(configModel);

        fb.addLabeledComponent(new JLabel("Model:"), modelPanel);

        configMaxTokens.setColumns(6);
        configTemperature.setColumns(6);

        // 限制输入内容为数字 TODO
//        JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        jp.add(new JBLabel("Max Tokens:"));
//        jp.add(configMaxTokens);
//        jp.add(new JBLabel("Temperature:"));
//        jp.add(configTemperature);

//        // prompt component
//        configPrompt.setLineWrap(true);  // 启用自动换行
//        configPrompt.setWrapStyleWord(true);  // 按单词换行

        configPrompt.setPreferredSize(new Dimension(400, 100));
        configPrompt.setVisible(false);

        fb.addLabeledComponent(new JBLabel("API Key:"), configApiKey)
                .addLabeledComponent(new JLabel("Endpoint:"), configBaseUrl);
//                .addLabeledComponent(new JLabel("Prompt:"), scrollPane)
//                .addLabeledComponent(new JBLabel("Prompt:"), configPrompt)
//                .addComponent(jp);

        return fb.getPanel();
    }

    private void loadServiceModels() throws IOException {
//        try {
//            String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/config/config.json").toURI())), StandardCharsets.UTF_8);

        JSONObject configJson = JSONObject.parseObject(jsonContent);
        for (String key : configJson.keySet()) {
            serviceModelsMap.put(key, configJson.getJSONObject(key));
        }
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
    }

    private JPanel providerPanel() {
//        // 初始化下拉框组件，添加 ComboItem 对象
//        providerComboBox = new ComboBox<>(new ComboxItem[]{
//                new ComboxItem("阿里云百炼", "bailian"),
//                new ComboxItem("扣子", "coze"),
//        });
//
//        selectedProvider = getProvider();
//
//        // 设置默认选择的实际值，检查用户上次的选择
//        String previousProvider = selectedProvider;
//        for (int i = 0; i < providerComboBox.getItemCount(); i++) {
//            ComboxItem item = providerComboBox.getItemAt(i);
//            if (item.getValue().equals(previousProvider)) {
//                providerComboBox.setSelectedItem(item);
//                break;
//            }
//        }
//
//        TitledSeparator titledSeparator = new TitledSeparator("服务提供商");
//
//        JPanel providerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        providerPanel.add(providerComboBox);

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
//        return ((ComboxItem) providerComboBox.getSelectedItem()).getValue();
        return "";
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

    public String getTargetLang() {
        return targetLangComboBox.getSelectedItem().toString();
    }

    public void setTargetLang(String lang) {
        for (int i = 0; i < targetLangComboBox.getItemCount(); i++) {
            ComboxItem item = targetLangComboBox.getItemAt(i);
            if (item.getValue().equals(lang)) {
                targetLangComboBox.setSelectedItem(item);
                break;
            }
        }
    }

    public LlmConfig getLlmConfig() {
        return new LlmConfig(
                serviceComboBox.getSelectedItem().toString(),
                configBaseUrl.getText().trim(),
                configApiKey.getText().trim(),
                configModelComboBox.getSelectedItem().toString(),
                configPrompt.getText().trim(),
                Integer.parseInt(configMaxTokens.getText().trim()),
                Double.parseDouble(configTemperature.getText().trim())
        );
    }

    public void setLlmConfig(LlmConfig config) {
        for (int i = 0; i < serviceComboBox.getItemCount(); i++) {
            String item = serviceComboBox.getItemAt(i);
            if (item.equals(config.provider)) {
                serviceComboBox.setSelectedItem(config.provider);
                break;
            }
        }

        for (int i = 0; i < configModelComboBox.getItemCount(); i++) {
            String item = configModelComboBox.getItemAt(i);
            if (item.equals(config.model)) {
                configModelComboBox.setSelectedItem(config.model);
                break;
            }
        }

        configBaseUrl.setText(config.baseUrl);
        configApiKey.setText(config.apiKey);
        configPrompt.setText(config.prompt);
        configMaxTokens.setText(String.valueOf(config.maxTokens));
        configTemperature.setText(String.valueOf(config.temperature));
    }

}