package org.intellij.sdk.editor.util;

import java.util.Objects;

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
