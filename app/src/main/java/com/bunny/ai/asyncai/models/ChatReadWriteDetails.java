package com.bunny.ai.asyncai.models;

public class ChatReadWriteDetails {

    private String members, solved, name, created, lastMessage;
    public ChatReadWriteDetails() {
    }

    public ChatReadWriteDetails(String members, String solved, String name, String created) {
        this.members = members;
        this.solved = solved;
        this.name = name;
        this.created = created;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
