package com.chat.zipchat.Model.Chat;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class ChatPojo {

    @Id
    public Long id;

    @Index(unique = true)
    private String messageId;

    private String friendId;
    private int isMessageSend;

    private String fromId;
    private String text;
    private String timestamp;
    private String toId;
    private String msgType;
    private String isRead;
    private String fileUrl;

    @Override
    public String toString() {
        return "ChatPojo{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                ", friendId='" + friendId + '\'' +
                ", isMessageSend=" + isMessageSend +
                ", fromId='" + fromId + '\'' +
                ", text='" + text + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", toId='" + toId + '\'' +
                ", msgType='" + msgType + '\'' +
                ", isRead='" + isRead + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }

    @Generated(hash = 1371595879)
    public ChatPojo(Long id, String messageId, String friendId, int isMessageSend,
                    String fromId, String text, String timestamp, String toId,
                    String msgType, String isRead, String fileUrl) {
        this.id = id;
        this.messageId = messageId;
        this.friendId = friendId;
        this.isMessageSend = isMessageSend;
        this.fromId = fromId;
        this.text = text;
        this.timestamp = timestamp;
        this.toId = toId;
        this.msgType = msgType;
        this.isRead = isRead;
        this.fileUrl = fileUrl;
    }

    @Generated(hash = 1418529328)
    public ChatPojo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFriendId() {
        return this.friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getIsMessageSend() {
        return this.isMessageSend;
    }

    public void setIsMessageSend(int isMessageSend) {
        this.isMessageSend = isMessageSend;
    }

    public String getFromId() {
        return this.fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
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

    public String getMsgType() {
        return this.msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getIsRead() {
        return this.isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}
