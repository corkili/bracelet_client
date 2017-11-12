package org.client.bracelet.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 状态
 */
public class State {
    /**
     * 状态ID
     */
    private Long id;

    /**
     * 状态起始时间
     */
    private Date startTime;

    /**
     * 状态终止时间
     */
    private Date endTime;

    /**
     * 状态类型（sport、heart、sleep）
     */
    private String status;

    /**
     * 状态所属用户ID
     */
    private User user;

    public State() {

    }

    public State(String jsonString, User user) {
        try {
            JSONObject json = new JSONObject(jsonString);
            if (json.has("id")) {
                this.id = json.getLong("id");
            }
            this.startTime = new Date(json.getLong("startTime"));
            this.endTime = new Date(json.getLong("endTime"));
            this.status = json.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("startTime", startTime.getTime())
                    .put("endTime", endTime.getTime())
                    .put("status", status);
            if (id != null) {
                json.put("id", id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;

        State state = (State) o;

        if (id != null ? !id.equals(state.id) : state.id != null) return false;
        if (startTime != null ? !startTime.equals(state.startTime) : state.startTime != null) return false;
        if (endTime != null ? !endTime.equals(state.endTime) : state.endTime != null) return false;
        if (status != null ? !status.equals(state.status) : state.status != null) return false;
        if (user != null ? !user.equals(state.user) : state.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
