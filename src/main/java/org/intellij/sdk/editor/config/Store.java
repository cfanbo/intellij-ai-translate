package org.intellij.sdk.editor.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.intellij.ide.util.PropertiesComponent;
import org.intellij.sdk.editor.util.Func;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Store {
    public List<StoreRecord> records;
    private String recordsFile = "store.json";

    private Store() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        String jsonContent = propertiesComponent.getValue("store");

        if (jsonContent == null || "".equals(jsonContent)) {
            jsonContent = Func.readResourceFileContents(recordsFile);
        }

        this.records = JSON.parseObject(jsonContent, new TypeReference<List<StoreRecord>>() {
        });
    }

    // 静态内部类
    private static class SingletonHolder {
        private static final Store INSTANCE = new Store();
    }

    // 获取实例
    public static Store getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public List<StoreRecord> getRecords() {
        return records;
    }

    public Map<String, String> getProviderList() {
        Map<String, String> providerList = new LinkedHashMap<>();
        if (records == null) {
            return providerList;
        }
        for (StoreRecord record : records) {
            providerList.put(record.name, record.provider);
        }
        return providerList;
    }

    public List<String> getModelList(String name) {
        StoreRecord record = getRecord(name);
        return record.getModelList();
    }

    public StoreRecord getRecord(String name) {
        for (StoreRecord record : records) {
            if (record.provider.equals(name)) {
                return record;
            }
        }
        return null;
    }

    public String toJSONString() {
        return JSON.toJSONString(this.records, true);
    }
}
