package org.client.bracelet.entity;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * 心率状态
 */
public class HeartState extends State {

    /**
     * 心跳频率
     */
    private Integer times;

    public HeartState() {

    }

    public HeartState(String jsonString, User user) {
        super(jsonString, user);
        try {
            JSONObject json = new JSONObject(jsonString);
            this.times = json.getInt("times");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    @Override
    public String toString() {
        JSONObject json;
        try {
            json = new JSONObject(super.toString());
            json.put("times", times);
        } catch (JSONException e) {
            json = new JSONObject();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeartState)) return false;
        if (!super.equals(o)) return false;

        HeartState that = (HeartState) o;

        if (times != null ? !times.equals(that.times) : that.times != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (times != null ? times.hashCode() : 0);
        return result;
    }
}
