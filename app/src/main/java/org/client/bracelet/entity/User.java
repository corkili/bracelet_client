package org.client.bracelet.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 用户实体
 */
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户口令
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 体重
     */
    private Double weight;

    /**
     * 身高
     */
    private Double height;

    /**
     * 手机号
     */
    private String phone;

    private java.util.Date registerTime;

    private java.util.Date lastLoginTime;

    /**
     * 用户饮食偏好
     */
    private List<FoodType> likeFoods;

    private List<User> friends;

    private List<Message> messages;

    public User() {
        this.likeFoods = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.weight = 0.0;
        this.height = 0.0;
        this.age = 0;
        this.sex = "男";
    }

    public User(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            this.id = json.getLong("id");
            this.username = json.getString("username");
            this.name = json.getString("name");
            this.birthday = new Date(json.getLong("birthday"));
            this.age = json.getInt("age");
            this.weight = json.getDouble("weight");
            this.height = json.getDouble("height");
            this.phone = json.getString("phone");
            this.registerTime = new java.util.Date(json.getLong("registerTime"));
            this.lastLoginTime = new java.util.Date(json.getLong("lastLoginTime"));
            this.likeFoods = new ArrayList<>();
            JSONArray foodArray = json.getJSONArray("likeFoods");
            for (int i = 0; i < foodArray.length(); i++) {
                likeFoods.add(new FoodType(foodArray.getJSONObject(i).toString()));
            }
            this.friends = new ArrayList<>();
            JSONArray friendArray = json.getJSONArray("friends");
            for (int i = 0; i < friendArray.length(); i++) {
                JSONObject friend = friendArray.getJSONObject(i);
                User user = new User();
                user.setId(friend.getLong("id"));
                user.setUsername(friend.getString("username"));
                user.setName(friend.getString("name"));
                user.setSex(friend.getString("sex"));
                user.setBirthday(new Date(friend.getLong("birthday")));
                user.setAge(friend.getInt("age"));
                user.setPhone(friend.getString("phone"));
                friends.add(user);
            }
            this.messages = new ArrayList<>();
            JSONArray messageArray = json.getJSONArray("messages");
            for (int i = 0; i < messageArray.length(); i++) {
                messages.add(new Message(messageArray.getJSONObject(i).toString()));
            }
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<FoodType> getLikeFoods() {
        return likeFoods;
    }

    public void setLikeFoods(List<FoodType> likeFoods) {
        this.likeFoods = likeFoods;
    }

    public java.util.Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(java.util.Date registerTime) {
        this.registerTime = registerTime;
    }

    public java.util.Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(java.util.Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id)
                    .put("username", username)
                    .put("name", name)
                    .put("password", password)
                    .put("sex", sex)
                    .put("birthday", birthday.getTime())
                    .put("age", age)
                    .put("weight", weight)
                    .put("height", height)
                    .put("phone", phone)
                    .put("registerTime", registerTime.getTime())
                    .put("lastLoginTime", lastLoginTime.getTime());
            JSONArray foodArray = new JSONArray();
            for (FoodType foodType : likeFoods) {
                foodArray.put(new JSONObject(foodType.toString()));
            }
            json.put("likeFoods", foodArray);

            JSONArray friendArray = new JSONArray();
            for (User user : friends) {
                JSONObject u = new JSONObject();
                u.put("id", user.getId())
                        .put("username", user.getUsername())
                        .put("name", user.getName())
                        .put("sex", user.getSex())
                        .put("birthday", user.getBirthday().getTime())
                        .put("age", user.getAge())
                        .put("phone", user.getPhone());
                friendArray.put(u);
            }
            json.put("friends", friendArray);

            JSONArray messageArray = new JSONArray();
            for (Message message : messages) {
                messageArray.put(new JSONObject(message.toString()));
            }
            json.put("messages", messageArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null)
            return false;
        if (password != null ? !password.equals(user.password) : user.password != null)
            return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (sex != null ? !sex.equals(user.sex) : user.sex != null) return false;
        if (birthday != null ? !birthday.equals(user.birthday) : user.birthday != null)
            return false;
        if (age != null ? !age.equals(user.age) : user.age != null) return false;
        if (weight != null ? !weight.equals(user.weight) : user.weight != null) return false;
        if (height != null ? !height.equals(user.height) : user.height != null) return false;
        if (phone != null ? !phone.equals(user.phone) : user.phone != null) return false;
        if (registerTime != null ? !registerTime.equals(user.registerTime) : user.registerTime != null)
            return false;
        if (lastLoginTime != null ? !lastLoginTime.equals(user.lastLoginTime) : user.lastLoginTime != null)
            return false;
        if (likeFoods != null ? !likeFoods.equals(user.likeFoods) : user.likeFoods != null)
            return false;
        if (friends != null ? !friends.equals(user.friends) : user.friends != null) return false;
        return messages != null ? messages.equals(user.messages) : user.messages == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (registerTime != null ? registerTime.hashCode() : 0);
        result = 31 * result + (lastLoginTime != null ? lastLoginTime.hashCode() : 0);
        result = 31 * result + (likeFoods != null ? likeFoods.hashCode() : 0);
        result = 31 * result + (friends != null ? friends.hashCode() : 0);
        result = 31 * result + (messages != null ? messages.hashCode() : 0);
        return result;
    }
}
