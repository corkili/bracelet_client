package org.client.bracelet.entity;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class Message implements Comparable<Message> {

    private Long id;

    private String content;

    private Long fromUserId;

    private String fromUserName;

    private String fromUserPhone;

    private Date time;

    public Message() {

    }

    public Message(String content, Long fromUserId, String fromUserName, String fromUserPhone, Date time) {
        this.content = content;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.fromUserPhone = fromUserPhone;
        this.time = time;
    }

    public Message(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            this.id = json.getLong("id");
            this.content = json.getString("content");
            this.fromUserId = json.getLong("fromUserId");
            this.fromUserName = json.getString("fromUserName");
            this.fromUserPhone = json.getString("fromUserPhone");
            this.time = new Date(json.getLong("time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserPhone() {
        return fromUserPhone;
    }

    public void setFromUserPhone(String fromUserPhone) {
        this.fromUserPhone = fromUserPhone;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("content", content);
            json.put("fromUserId", fromUserId);
            json.put("fromUserName", fromUserName);
            json.put("fromUserPhone", fromUserPhone);
            json.put("time", time.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message message = (Message) o;

        if (id != null ? !id.equals(message.id) : message.id != null) return false;
        if (content != null ? !content.equals(message.content) : message.content != null) return false;
        if (fromUserId != null ? !fromUserId.equals(message.fromUserId) : message.fromUserId != null) return false;
        if (fromUserName != null ? !fromUserName.equals(message.fromUserName) : message.fromUserName != null)
            return false;
        if (fromUserPhone != null ? !fromUserPhone.equals(message.fromUserPhone) : message.fromUserPhone != null)
            return false;
        return time != null ? time.equals(message.time) : message.time == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (fromUserId != null ? fromUserId.hashCode() : 0);
        result = 31 * result + (fromUserName != null ? fromUserName.hashCode() : 0);
        result = 31 * result + (fromUserPhone != null ? fromUserPhone.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull Message o) {
        if (time.getTime() < o.getTime().getTime()) {
            return -1;
        } else if (time.getTime() > o.getTime().getTime()){
            return 1;
        } else {
            return 0;
        }
    }
}
