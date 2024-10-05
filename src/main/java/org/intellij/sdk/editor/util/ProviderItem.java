package org.intellij.sdk.editor.util;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class ProviderItem {
    public String provider;

    @JSONField(name = "baseurl")
    public String baseUrl;   // 与 JSON 中的 "baseurl" 一致

    @JSONField(name = "apikey")
    public String apiKey;    // 与 JSON 中的 "apikey" 一致

    @JSONField(name = "models")
    public List<String> models;  // 与 JSON 中的 "models" 一致
    public String description;
    public String type;
}
