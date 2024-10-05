package org.intellij.sdk.editor.util;

import java.util.List;
import java.util.Objects;


public class ProviderComboxItem {
    private final String displayName;
    private final Item value;

    class Item {
        String name;
        String provider;
        String baseUrl;
        String apiKey;
        List<String> model;
        String description;
        String type;
    }

    public ProviderComboxItem(String displayName, Item value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getValue() {
        return value.name;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof org.intellij.sdk.editor.util.ComboxItem)) return false;
        org.intellij.sdk.editor.util.ProviderComboxItem other = (org.intellij.sdk.editor.util.ProviderComboxItem) obj;
        return displayName.equals(other.displayName) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, value);
    }
}
