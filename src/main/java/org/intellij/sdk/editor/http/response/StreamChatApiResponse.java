package org.intellij.sdk.editor.http.response;

import com.alibaba.fastjson.annotation.JSONField;

public class StreamChatApiResponse {

    @JSONField(name = "id")
    private String id;

    private String role;

    private String type;

    private String content;

    private String contentType;

    private String chatId;

    private String conversationId;

    private String botId;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    // toString 方法用于方便查看对象的内容
    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", role='" + role + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", chatId='" + chatId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", botId='" + botId + '\'' +
                '}';
    }
}