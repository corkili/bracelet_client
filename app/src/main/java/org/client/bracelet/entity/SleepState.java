package org.client.bracelet.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 睡眠状态
 */
public class SleepState extends State {

    /**
     * 睡眠类型（深睡眠或浅睡眠）
     */
    private String sleepType;

    public SleepState() {

    }

    public SleepState(String jsonString, User user) {
        super(jsonString, user);
        try {
            JSONObject json = new JSONObject(jsonString);
            this.sleepType = json.getString("sleepType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSleepType() {
        return sleepType;
    }

    public void setSleepType(String sleepType) {
        this.sleepType = sleepType;
    }

    @Override
    public String toString() {
        JSONObject json;
        try {
            json = new JSONObject(super.toString());
            json.put("sleepType", sleepType);
        } catch (JSONException e) {
            json = new JSONObject();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SleepState)) return false;
        if (!super.equals(o)) return false;

        SleepState that = (SleepState) o;

        if (sleepType != null ? !sleepType.equals(that.sleepType) : that.sleepType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sleepType != null ? sleepType.hashCode() : 0);
        return result;
    }
}
