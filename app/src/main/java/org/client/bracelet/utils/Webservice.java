package org.client.bracelet.utils;


import org.client.bracelet.entity.User;
import org.json.JSONException;
import org.json.JSONObject;

public class Webservice {

    private static final String BASE_URL = "http://118.114.42.222:8080";

    private static final String registerUrl = BASE_URL + "/user/register";

    public static JSONObject register(User user) {
        String result = HttpUtils.postRequest(registerUrl, user.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }
}
