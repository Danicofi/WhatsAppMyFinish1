package com.dan.whatsappmy.models;

import java.util.ArrayList;

public class ChatModel {
    private String id, writing, groupChat, imageGroup;
    private long timestamp;
    private ArrayList<String> ids;
    private  int numberMessages, idNotification;
    private boolean multiChat;

    public ChatModel() {
    }

    public ChatModel(String id, String writing, String groupChat, String imageGroup, long timestamp, ArrayList<String> ids, int numberMessages, int idNotification, boolean multiChat) {
        this.id = id;
        this.writing = writing;
        this.groupChat = groupChat;
        this.imageGroup = imageGroup;
        this.timestamp = timestamp;
        this.ids = ids;
        this.numberMessages = numberMessages;
        this.idNotification = idNotification;
        this.multiChat = multiChat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    public String getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(String groupChat) {
        this.groupChat = groupChat;
    }

    public String getImageGroup() {
        return imageGroup;
    }

    public void setImageGroup(String imageGroup) {
        this.imageGroup = imageGroup;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public int getNumberMessages() {
        return numberMessages;
    }

    public void setNumberMessages(int numberMessages) {
        this.numberMessages = numberMessages;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }

    public boolean isMultiChat() {
        return multiChat;
    }

    public void setMultiChat(boolean multiChat) {
        this.multiChat = multiChat;
    }
}
