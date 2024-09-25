package org.intellij.sdk.editor.http.response;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class MessageListApiResponse {

    private int code;
    private List<Data> data;
    private String msg;

    // Getter and Setter methods

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    // Nested class for 'data' object
    public static class Data {
        @JSONField(name = "bot_id")
        private String botId;
        @JSONField(name = "chat_id")
        private String chatId;
        private String content;
        @JSONField(name = "content_type")
        private String contentType;
        @JSONField(name = "conversation_id")
        private String conversationId;
        private String id;
        private String role;
        private String type;

        // Getter and Setter methods

        public String getBotId() {
            return botId;
        }

        public void setBotId(String botId) {
            this.botId = botId;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
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

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }

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
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }
}
