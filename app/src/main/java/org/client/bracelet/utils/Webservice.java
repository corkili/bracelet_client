package org.client.bracelet.utils;


import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.State;
import org.client.bracelet.entity.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Webservice {

    private static final String BASE_URL = "http://220.167.47.123:8080";

    private static final String registerUrl = BASE_URL + "/user/register";

    private static final String loginUrl = BASE_URL + "/user/login";

    private static final String logoutUrl = BASE_URL + "/user/logout";

    private static final String resetPasswordUrl = BASE_URL + "/user/resetPassword";

    private static final String modifyUserInformationUrl = BASE_URL + "/user/modifyUserInformation";

    private static final String sendMessagesUrl = BASE_URL + "/user/sendMessages";

    private static final String addFriendUrl = BASE_URL + "/user/addFriend";

    private static final String refreshMessages = BASE_URL + "/user/refreshMessages";

    private static final String getAllFoodTypeUrl = BASE_URL + "/food/getAllFoodType";

    private static final String refreshRecipeUrl = BASE_URL + "/food/makeRecipe";

    private static final String getStatesUrl = BASE_URL + "/state/getStates";

    private static final String addStatesUrl = BASE_URL + "/state/addStates";

    public static JSONObject register(User user) {
        String result = HttpUtils.postRequest(registerUrl, user.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject login(String phone, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put("phone", phone);
            params.put("password", password);
        } catch (JSONException e) {
            return null;
        }
        String result = HttpUtils.postRequest(loginUrl, params.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject modifyUserInformation(User user) {
        String result = HttpUtils.postRequest(modifyUserInformationUrl, user.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }


    public static JSONObject getAllFoodType() {
        String result = HttpUtils.postRequest(getAllFoodTypeUrl, "");
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject logout() {
        JSONObject params = new JSONObject();
        try {
            params.put("userId", ApplicationManager.getInstance().getUser().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.postRequest(logoutUrl, params.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject resetPassword(String phone, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put("phone", phone);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.postRequest(resetPasswordUrl, params.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject refreshMessages() {
        String result = HttpUtils.postRequest(refreshMessages, "");
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject refreshRecipe() {
        String result = HttpUtils.postRequest(refreshRecipeUrl, "");
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject addFriend(String userPhone) {
        JSONObject params = new JSONObject();
        try {
            params.put("phone", userPhone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.postRequest(addFriendUrl, params.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject sendMessages(String content, long fromUserId) {
        JSONObject params = new JSONObject();
        try {
            params.put("phone", content);
            params.put("fromUserId", fromUserId);
            params.put("time", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.postRequest(sendMessagesUrl, params.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject addStates(String... stateJsonStrings) {
        JSONArray params = new JSONArray();
        try {
            for (int i = 0; i <stateJsonStrings.length; i++) {
                params.put(new JSONObject(stateJsonStrings[i]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.postRequest(addStatesUrl, params.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getStates(String status, long startTime, long endTime) {
        JSONObject params = new JSONObject();
        try {
            params.put("status", status);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.postRequest(getStatesUrl, params.toString());
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

}
