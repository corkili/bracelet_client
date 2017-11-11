package org.client.bracelet.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.FoodType;
import org.client.bracelet.entity.MessageCode;
import org.client.bracelet.entity.ResponseCode;
import org.client.bracelet.entity.User;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class SettingFragment extends Fragment {
    private static SettingFragment singleton;

    private AwesomeTextView usernameTV, nameTV, sexTV, birthdayTV, heightTV, weightTV, likeFoodsTV;
    private RelativeLayout likeFoodsLayout;
    private BootstrapButton editUserInfoBtn, editPasswordBtn, logoutBtn;
    private BtnOnClick btnOnClick;
    private SimpleDateFormat dateFormat;
    private SweetAlertDialog pDialog;
    private ApplicationManager manager;
    private JSONObject result;
    private User modifiedUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static SettingFragment getInstance() {
        if (singleton == null) {
            singleton = new SettingFragment();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        btnOnClick = new BtnOnClick();
        manager = ApplicationManager.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        List<FoodType> foodTypes = new ArrayList<>();
        Set<String> foodTypeJsons = sharedPreferences.getStringSet("foodTypes", new HashSet<String>());
        manager.recipeReasonHasModified(sharedPreferences.getBoolean("recipeReasonHasModified", false));
        for (String foodTypeJson : foodTypeJsons) {
            foodTypes.add(new FoodType(foodTypeJson));
        }
        manager.setCacheFoodTypes(foodTypes);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (ApplicationManager.getInstance().isLogin()) {
            v = inflater.inflate(R.layout.fragment_settings, null);
            usernameTV = ViewFindUtils.find(v, R.id.username);
            nameTV = ViewFindUtils.find(v, R.id.name);
            sexTV = ViewFindUtils.find(v, R.id.sex);
            birthdayTV = ViewFindUtils.find(v, R.id.birthday);
            heightTV = ViewFindUtils.find(v, R.id.height);
            weightTV = ViewFindUtils.find(v, R.id.weight);
            likeFoodsTV = ViewFindUtils.find(v, R.id.like_foods);

            likeFoodsLayout = ViewFindUtils.find(v, R.id.like_foods_layout);

            editUserInfoBtn = ViewFindUtils.find(v, R.id.btn_edit_info);
            editPasswordBtn = ViewFindUtils.find(v, R.id.btn_edit_password);
            logoutBtn = ViewFindUtils.find(v, R.id.btn_logout);

            editUserInfoBtn.setOnClickListener(btnOnClick);
            editPasswordBtn.setOnClickListener(btnOnClick);
            logoutBtn.setOnClickListener(btnOnClick);
            likeFoodsLayout.setOnClickListener(btnOnClick);

            User user = manager.getUser();

            usernameTV.setText(user.getUsername());
            nameTV.setText(user.getName());
            sexTV.setText(user.getSex());
            birthdayTV.setText(dateFormat.format(user.getBirthday()));
            heightTV.setText(user.getHeight() + "厘米");
            weightTV.setText(user.getWeight() + "公斤");
            String likeFoodsStr = "";
            for (FoodType foodType : user.getLikeFoods()) {
                likeFoodsStr += " " + foodType.getName();
            }
            if (likeFoodsStr.length() != 0) {
                likeFoodsStr = likeFoodsStr.substring(1);
            } else {
                likeFoodsStr = "无";
            }
            likeFoodsTV.setText(likeFoodsStr);
        } else {
            v = inflater.inflate(R.layout.fragment_nologin, null);
            BootstrapButton toLoginBtn = ViewFindUtils.find(v, R.id.btn_to_login);
            toLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }

        return v;
    }

    private class BtnOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == editPasswordBtn.getId()) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ResetPasswordActivity.class);
                startActivity(intent);
            } else if (v.getId() == editUserInfoBtn.getId()) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ModifyUserInfoActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else if (v.getId() == logoutBtn.getId()) {
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("正在注销");
                pDialog.setCancelable(false);
                pDialog.show();
                new Thread(logoutRequest).start();
            } else if (v.getId() == likeFoodsLayout.getId()) {
                if (!ApplicationManager.getInstance().hasCacheFoodTypes()) {
                    // 从服务器获取列表
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("正在拉取饮食列表");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    new Thread(getAllFoodTypesRequest).start();
                } else {
                    showChooseFoodTypeDialog();
                }
            }
        }
    }

    private void showChooseFoodTypeDialog() {
        List<FoodType> foodTypes = ApplicationManager.getInstance().getCacheFoodTypes();
        final FoodType[] foodTypeArray = new FoodType[foodTypes.size()];
        String[] items = new String[foodTypes.size()];
        boolean[] checkeds = new boolean[foodTypes.size()];
        final List<Integer> chose = new ArrayList<>();
        for (FoodType foodType : foodTypes) {
            items[(int)(foodType.getId() - 1)] = foodType.getName();
            foodTypeArray[(int)(foodType.getId() - 1)] = foodType;
        }
        for (int i = 0; i < checkeds.length; i++) {
            checkeds[i] = false;
        }
        for (FoodType foodType : ApplicationManager.getInstance().getUser().getLikeFoods()) {
            checkeds[(int)(foodType.getId() - 1)] = true;
            chose.add((int)(foodType.getId() - 1));
        }
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle("请勾选饮食偏好")
                .setMultiChoiceItems(items, checkeds, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            chose.add(which);
                        } else {
                            chose.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<FoodType> likeFoods = new ArrayList<>();
                        for (int i : chose) {
                            likeFoods.add(foodTypeArray[i]);
                        }
                        modifiedUser = new User(manager.getUser().toString());
                        if (!manager.getUser().getLikeFoods().equals(likeFoods)) {
                            modifiedUser.setLikeFoods(likeFoods);
                            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("正在请求修改信息");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            new Thread(modifyUserInfoRequest).start();
                        } else {
                            Toast.makeText(getActivity(), "未对饮食偏好进行修改", Toast.LENGTH_LONG).show();
                        }


                    }
                });
        builder.create().show();
    }

    Runnable getAllFoodTypesRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.getAllFoodType();
            int resCode;
            try {
                if (result != null) {
                    resCode = result.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            getAllFoodTypesHandler.sendMessage(msg);
        }
    };

    Handler getAllFoodTypesHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            pDialog.dismissWithAnimation();
            switch (msg.what) {
                case MessageCode.MSG_REQUEST_SUCCESSFUL: {
                    try {
                        JSONArray foodTypeArray = result.getJSONArray("foodTypes");
                        List<FoodType> foodTypes = new ArrayList<>();
                        Set<String> foodTypeJsons = new HashSet<>();
                        for (int i = 0; i < foodTypeArray.length(); i++) {
                            foodTypes.add(new FoodType(foodTypeArray.getJSONObject(i).toString()));
                            foodTypeJsons.add(foodTypeArray.getJSONObject(i).toString());
                        }
                        editor =  sharedPreferences.edit();
                        editor.putStringSet("foodTypes", foodTypeJsons);
                        editor.apply();
                        manager.setCacheFoodTypes(foodTypes);
                        showChooseFoodTypeDialog();
                    } catch (JSONException e) {
                        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("错误提示");
                        pDialog.setContentText("客户端异常");
                        break;
                    }
                    break;
                }
                case MessageCode.MSG_REQUEST_ERROR: {
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("错误提示");
                    pDialog.setContentText("网络请求错误，请检查网络设置");
                    break;
                }
                case MessageCode.MSG_REQUEST_EXCEPTION: {
                    String resMsg;
                    try {
                        resMsg = result.getString("resMsg");
                    } catch (JSONException e) {
                        resMsg = "未知错误，请重试";
                    }
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("异常提示");
                    pDialog.setContentText(resMsg);
                    break;
                }
            }
            pDialog.show();
            result = null;
        }
    };

    Runnable modifyUserInfoRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.modifyUserInformation(modifiedUser);
            int resCode;
            try {
                if (result != null) {
                    resCode = result.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            modifyUserInfoHandler.sendMessage(msg);
        }
    };

    Handler modifyUserInfoHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            pDialog.dismissWithAnimation();
            switch (msg.what) {
                case MessageCode.MSG_REQUEST_SUCCESSFUL: {
                    try {
                        manager.setUser(new User(result.getJSONObject("user").toString()));
                        String likeFoodsStr = "";
                        for (FoodType foodType : manager.getUser().getLikeFoods()) {
                            likeFoodsStr += " " + foodType.getName();
                        }
                        if (likeFoodsStr.length() != 0) {
                            likeFoodsStr = likeFoodsStr.substring(1);
                        } else {
                            likeFoodsStr = "无";
                        }
                        editor = sharedPreferences.edit();
                        manager.recipeReasonHasModified(true);
                        editor.putBoolean("recipeReasonHasModified", true);
                        editor.apply();
                        likeFoodsTV.setText(likeFoodsStr);
                    } catch (JSONException e) {
                        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("错误提示");
                        pDialog.setContentText("客户端异常");
                        break;
                    }
                    break;
                }
                case MessageCode.MSG_REQUEST_ERROR: {
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("错误提示");
                    pDialog.setContentText("网络请求错误，请检查网络设置");
                    break;
                }
                case MessageCode.MSG_REQUEST_EXCEPTION: {
                    String resMsg;
                    try {
                        resMsg = result.getString("resMsg");
                    } catch (JSONException e) {
                        resMsg = "未知错误，请重试";
                    }
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("异常提示");
                    pDialog.setContentText(resMsg);
                    break;
                }
            }
            pDialog.show();
            result = null;
        }
    };

    Runnable logoutRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.logout();
            int resCode;
            try {
                if (result != null) {
                    resCode = result.getInt("resCode");
                } else {
                    resCode = MessageCode.MSG_REQUEST_ERROR;
                }
            } catch (JSONException e) {
                resCode = MessageCode.MSG_REQUEST_ERROR;
            }
            if (resCode == MessageCode.MSG_REQUEST_ERROR) {
                msg.what = MessageCode.MSG_REQUEST_ERROR;
            } else if (resCode == ResponseCode.SUCCESSFUL) {
                msg.what = MessageCode.MSG_REQUEST_SUCCESSFUL;
            } else if (resCode == ResponseCode.NO_LOGIN) {
                msg.what = MessageCode.MSG_NO_LOGIN;
            } else {
                msg.what = MessageCode.MSG_REQUEST_EXCEPTION;
            }
            logoutHandler.sendMessage(msg);
        }
    };

    Handler logoutHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            pDialog.dismissWithAnimation();
            switch (msg.what) {
                case MessageCode.MSG_REQUEST_SUCCESSFUL: {
                    manager.setUser(null);
                    manager.isLogin(false);
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                }
                case MessageCode.MSG_REQUEST_ERROR: {
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("错误提示");
                    pDialog.setContentText("网络请求错误，请检查网络设置");
                    break;
                }
                case MessageCode.MSG_REQUEST_EXCEPTION: {
                    String resMsg;
                    try {
                        resMsg = result.getString("resMsg");
                    } catch (JSONException e) {
                        resMsg = "未知错误，请重试";
                    }
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("异常提示");
                    pDialog.setContentText(resMsg);
                    break;
                }
            }
            pDialog.show();
            result = null;
        }
    };
}
