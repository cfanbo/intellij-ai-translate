package org.intellij.sdk.editor.config;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class StoreRecord {
    public String name; // pk主键
    public String provider;

    @JSONField(name = "baseurl")
    public String baseUrl;   // 与 JSON 中的 "baseurl" 一致

    @JSONField(name = "apikey")
    public String apiKey;    // 与 JSON 中的 "apikey" 一致


    @JSONField(name = "apiKeyPlaceholder")
    public String apiKeyPlaceholder;    // 与 JSON 中的 "apikey" 一致

    @JSONField(name = "models")
    public List<String> models;  // 与 JSON 中的 "models" 一致
    public String description;
    public String type;


    public StoreRecord setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public StoreRecord setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public StoreRecord setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public StoreRecord setModels(List<String> models) {
        this.models = models;
        return this;
    }

    public StoreRecord setDescription(String description) {
        this.description = description;
        return this;
    }

    public StoreRecord setType(String type) {
        this.type = type;
        return this;
    }

    @JSONField(serialize = false)
    public StoreRecord setModel(String name) {
        if (!name.isEmpty() && !this.models.contains(name)) {
            if (this.provider.equals("Coze") || this.provider.equals("BaiLian")) {
                this.models.clear();
            }
            this.models.add(name);
        }
        return this;
    }

    @JSONField(serialize = false)
    public boolean isAgent()
    {
        return this.type != null && this.type.equals("agent");
    }

    @JSONField(serialize = false)
    public List<String> getModelList()
    {
        return this.models;
    }

}
