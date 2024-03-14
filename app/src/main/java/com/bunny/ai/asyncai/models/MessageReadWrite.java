package com.bunny.ai.asyncai.models;

public class MessageReadWrite {

    private String messageId, message, senderId, name, isUser, isBot, isExpert, messageType, dataUrl;
    private long timestamp;

    public MessageReadWrite() {
    }

    public MessageReadWrite(String message, String senderId, String name, String isUser, String isBot, String isExpert, String messageType, long timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.name = name;
        this.isUser = isUser;
        this.isBot = isBot;
        this.isExpert = isExpert;
        this.messageType = messageType;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsUser() {
        return isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }

    public String getIsBot() {
        return isBot;
    }

    public void setIsBot(String isBot) {
        this.isBot = isBot;
    }

    public String getIsExpert() {
        return isExpert;
    }

    public void setIsExpert(String isExpert) {
        this.isExpert = isExpert;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
