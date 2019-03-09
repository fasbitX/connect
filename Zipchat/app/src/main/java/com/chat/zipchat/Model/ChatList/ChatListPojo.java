package com.chat.zipchat.Model.ChatList;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class ChatListPojo {

    @Id
    public Long id;

    private String text;
    private String timestamp;

    @Index(unique = true)
    private String toId;

    @Generated(hash = 765642557)
    public ChatListPojo(Long id, String text, String timestamp, String toId) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.toId = toId;
    }

    @Generated(hash = 96467942)
    public ChatListPojo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToId() {
        return this.toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }


}
