package org.intellij.sdk.editor.http.response;

import com.alibaba.fastjson.annotation.JSONField;

public class ChatApiResponse {

    private Data data;
    private int code;
    private String msg;

    // Getter and Setter methods

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    // Nested class for 'data' object
    public static class Data {
        private String id;
        @JSONField(name = "conversation_id")
        private String conversationId;
        @JSONField(name = "bot_id")
        private String botId;
        @JSONField(name = "created_at")
        private long createdAt;
        @JSONField(name = "last_error")
        private LastError lastError;
        private String status;

        // Getter and Setter methods

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public LastError getLastError() {
            return lastError;
        }

        public void setLastError(LastError lastError) {
            this.lastError = lastError;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // Nested class for 'last_error' object
    public static class LastError {
        private int code;
        private String msg;

        // Getter and Setter methods

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
