package org.intellij.sdk.editor.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.intellij.ide.util.PropertiesComponent;
import org.intellij.sdk.editor.util.Func;

import java.util.*;

public class Store {
    public List<StoreRecord> records;
    private String recordsFile = "store.json";

    private Store() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        String jsonContent = propertiesComponent.getValue("store");

        if (jsonContent == null || jsonContent.isEmpty()) {
            jsonContent = Func.readResourceFileContents(recordsFile);
            this.records = JSON.parseObject(jsonContent, new TypeReference<List<StoreRecord>>() {});
        } else {
            // 合并数据，后期可对store.json文件进行修改，包含新项添加和删除
            List<StoreRecord> userRecords = JSON.parseObject(jsonContent, new TypeReference<List<StoreRecord>>() {});
            List<StoreRecord> templateRecords = JSON.parseObject(Func.readResourceFileContents(recordsFile), new TypeReference<List<StoreRecord>>() {});

            for (int i = 0; i < templateRecords.size(); i++) {
                StoreRecord templateRecord = templateRecords.get(i);
                StoreRecord userRecord = Store.readProviderItem(userRecords, templateRecord.name);
                if (userRecord == null) {
                    continue;
                }

                if (userRecord.provider.equals(templateRecord.provider)) {
                    templateRecord.setApiKey(userRecord.apiKey);
                    templateRecord.setBaseUrl(userRecord.baseUrl);
                    templateRecord.setType(userRecord.type);

                    // 模型处理
                    Set<String> mergedSet = new HashSet<>(templateRecord.models);
                    mergedSet.addAll(userRecord.models);
                    templateRecord.setModels(new ArrayList<>(mergedSet));
                }
            }

            this.records = templateRecords;
        }
    }

    private static StoreRecord readProviderItem(List<StoreRecord> userRecords, String name) {
        for (StoreRecord record : userRecords) {
            if (record.name.equals(name)) {
                return record;
            }
        }
        return null;
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
