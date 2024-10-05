package org.intellij.sdk.editor.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.intellij.openapi.ui.ComboBox;
import org.intellij.sdk.editor.config.Store;
import org.intellij.sdk.editor.config.StoreRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Provider {
    private Store store;

    private ComboBox<ComboxItem> providerComboBox;
    private ComboBox<String> modelComboBox = new ComboBox<>();

    public Provider(ComboBox<ComboxItem> targetComboBox) {
        this.store = Store.getInstance();
        this.providerComboBox = targetComboBox;
        this.FillProviderData(this.providerComboBox);
    }

    public void FillProviderData(ComboBox<ComboxItem> targetComboBox) {
        targetComboBox.removeAllItems();

        for (Map.Entry<String, String> entry : store.getProviderList().entrySet()) {
            ComboxItem item = new ComboxItem(entry.getKey(), entry.getValue());
            targetComboBox.addItem(item);
        }
    }

    public String getSelectedItem() {
        ComboxItem item = (ComboxItem) providerComboBox.getSelectedItem();
        return item.getValue();
    }

    public StoreRecord getCurrentItem() {
        String selectedValue = getSelectedItem();
        return this.getModelRecord(selectedValue);
    }

    public StoreRecord getModelRecord(String provider) {
       return this.store.getRecord(provider);
    }

    public void setSelectedItem(String value) {
        for (int i = 0; i < providerComboBox.getItemCount(); i++) {
            ComboxItem item = providerComboBox.getItemAt(i);
            if (item.getValue().equals(value)) {
                providerComboBox.setSelectedItem(item);
                break;
            }
        }
    }

}
