package org.intellij.sdk.editor.http.response;

import com.alibaba.fastjson.annotation.JSONField;

public class RetrieveApiResponse {

    private int code;
    private Data data;
    private String msg;

    // Getter and Setter methods

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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
        @JSONField(name = "conversation_id")
        private String conversationId;
        @JSONField(name = "created_at")
        private long createdAt;
        private String id;
        private String status;

        // Getter and Setter methods

        public String getBotId() {
            return botId;
        }

        public void setBotId(String botId) {
            this.botId = botId;
        }

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @Override
    public String toString() {
        return "RetrieveApiResponse{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }
}

