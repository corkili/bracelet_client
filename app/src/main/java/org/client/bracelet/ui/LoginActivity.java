package org.client.bracelet.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.MessageCode;
import org.client.bracelet.entity.ResponseCode;
import org.client.bracelet.entity.User;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends AppCompatActivity {

    private BootstrapButton toRegisterBtn, forgetPasswordBtn, loginBtn;
    private BootstrapEditText phoneET, passwordET;
    private BtnOnClick btnOnClick;
    private SweetAlertDialog pDialog;
    private JSONObject result;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String phone, password;
    private ApplicationManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        btnOnClick = new BtnOnClick();
        View v = getWindow().getDecorView();
        toRegisterBtn = ViewFindUtils.find(v, R.id.btn_register);
        forgetPasswordBtn = ViewFindUtils.find(v, R.id.btn_reset_password);
        loginBtn = ViewFindUtils.find(v, R.id.btn_login);
        phoneET = ViewFindUtils.find(v, R.id.edit_phone);
        passwordET = ViewFindUtils.find(v, R.id.edit_password);

        toRegisterBtn.setOnClickListener(btnOnClick);
        forgetPasswordBtn.setOnClickListener(btnOnClick);
        loginBtn.setOnClickListener(btnOnClick);

        manager = ApplicationManager.getInstance();
        sharedPreferences = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        Boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
        if (isLogin) {
            phoneET.setText(sharedPreferences.getString("phone", ""));
            passwordET.setText(sharedPreferences.getString("password", ""));
        }
    }

    private class BtnOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == toRegisterBtn.getId()) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            } else if (v.getId() == forgetPasswordBtn.getId()) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            } else if (v.getId() == loginBtn.getId()) {
                login();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private void login() {
        phone = phoneET.getText().toString();
        password = passwordET.getText().toString();
        if (phone.isEmpty() || password.isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误信息").setContentText("手机号或密码不能为空")
                    .show();
        } else {
            pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("正在登录");
            pDialog.setCancelable(false);
            pDialog.show();
            new Thread(loginRequest).start();
        }
    }

    Runnable loginRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.login(phone, password);
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
            handler.sendMessage(msg);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg){
            super.handleMessage(msg);
            pDialog.dismissWithAnimation();
            switch (msg.what) {
                case MessageCode.MSG_REQUEST_SUCCESSFUL: {
                    try {
                        manager.setUser(new User(result.getJSONObject("user").toString()));
                        manager.isLogin(true);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("isLogin", true);
                        editor.putString("phone", phone);
                        editor.putString("password", password);
                        editor.apply();
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } catch (JSONException e) {
                        manager.isLogin(false);
                        pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("错误提示");
                        pDialog.setContentText("客户端异常");
                        break;
                    }
                    break;
                }
                case MessageCode.MSG_REQUEST_ERROR: {
                    pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                    pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
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
