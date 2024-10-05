package org.intellij.sdk.editor.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Lang {
    Map<String, String> data = new LinkedHashMap<>();
    JComboBox<ComboxItem> targetLangComboBox;

    public Lang(JComboBox<ComboxItem> targetComboBox) {
        this.targetLangComboBox = targetComboBox;

        String jsonContent = Func.readResourceFileContents("lang.json");
        this.data = JSON.parseObject(jsonContent, new TypeReference<LinkedHashMap<String, String>>() {
        });

        this.FillData(this.targetLangComboBox);
    }

    public void FillData(JComboBox<ComboxItem> targetLangComboBox) {
        targetLangComboBox.removeAllItems();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            ComboxItem item = new ComboxItem(entry.getKey(), entry.getValue());
            targetLangComboBox.addItem(item);
        }
    }

    public String getSelectedItem() {
        ComboxItem item = (ComboxItem) targetLangComboBox.getSelectedItem();
        return item.getValue();
    }

    public void setSelectedItem(String value) {
        for (int i = 0; i < targetLangComboBox.getItemCount(); i++) {
            ComboxItem item = targetLangComboBox.getItemAt(i);
            if (item.getValue().equals(value)) {
                targetLangComboBox.setSelectedItem(item);
                break;
            }
        }
    }

}
