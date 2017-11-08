package org.client.bracelet.entity;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * 食物类型
 */
public class FoodType {

    /**
     * 食物类型ID
     */
    private Long id;

    /**
     * 食物类型名称
     */
    private String name;

    public FoodType() {

    }

    public FoodType(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            this.id = json.getLong("id");
            this.name = json.getString("name");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id).put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodType)) return false;

        FoodType foodType = (FoodType) o;

        if (id != null ? !id.equals(foodType.id) : foodType.id != null) return false;
        if (name != null ? !name.equals(foodType.name) : foodType.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
