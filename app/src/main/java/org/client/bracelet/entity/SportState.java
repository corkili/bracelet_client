package org.client.bracelet.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 运动状态
 */
public class SportState extends State{

    /**
     * 运动状态的类型（步行、跑步）
     */
    private String sportType;

    /**
     * 步数
     */
    private Long steps;

    /**
     * 里程（公里）
     */
    private Double kilometre;

    public SportState() {

    }

    public SportState(String jsonString, User user) {
        super(jsonString, user);
        try {
            JSONObject json = new JSONObject(jsonString);
            this.sportType = json.getString("sportType");
            this.steps = json.getLong("steps");
            this.kilometre = json.getDouble("kilometre");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public Long getSteps() {
        return steps;
    }

    public void setSteps(Long steps) {
        this.steps = steps;
    }

    public Double getKilometre() {
        return kilometre;
    }

    public void setKilometre(Double kilometre) {
        this.kilometre = kilometre;
    }

    @Override
    public String toString() {
        JSONObject json;
        try {
            json = new JSONObject(super.toString());
            json.put("sportType", sportType)
                    .put("steps", steps)
                    .put("kilometre", kilometre);
        } catch (JSONException e) {
            json = new JSONObject();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SportState)) return false;
        if (!super.equals(o)) return false;

        SportState that = (SportState) o;

        if (sportType != null ? !sportType.equals(that.sportType) : that.sportType != null) return false;
        if (steps != null ? !steps.equals(that.steps) : that.steps != null) return false;
        if (kilometre != null ? !kilometre.equals(that.kilometre) : that.kilometre != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sportType != null ? sportType.hashCode() : 0);
        result = 31 * result + (steps != null ? steps.hashCode() : 0);
        result = 31 * result + (kilometre != null ? kilometre.hashCode() : 0);
        return result;
    }
}
