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
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.MessageCode;
import org.client.bracelet.entity.ResponseCode;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/9.
 */

public class ResetPasswordActivity extends AppCompatActivity {

    private BootstrapButton getVerifyCodeBtn, okBtn, cancelBtn;
    private BootstrapEditText phoneET, verifyCodeET, passwordET, confirmPasswordET;
    private BtnOnClick btnOnClick;
    private SweetAlertDialog pDialog;
    private ApplicationManager manager;
    private JSONObject result;
    private String phone, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_passsword_layout);
        btnOnClick = new BtnOnClick();
        View v = getWindow().getDecorView();
        getVerifyCodeBtn = ViewFindUtils.find(v, R.id.btn_get_verify_code);
        okBtn = ViewFindUtils.find(v, R.id.btn_ok);
        cancelBtn = ViewFindUtils.find(v, R.id.btn_cancel);
        phoneET = ViewFindUtils.find(v, R.id.edit_phone);
        verifyCodeET = ViewFindUtils.find(v, R.id.edit_verify_code);
        passwordET = ViewFindUtils.find(v, R.id.edit_password);
        confirmPasswordET = ViewFindUtils.find(v, R.id.edit_confirm_password);

        getVerifyCodeBtn.setOnClickListener(btnOnClick);
        okBtn.setOnClickListener(btnOnClick);
        cancelBtn.setOnClickListener(btnOnClick);
        manager = ApplicationManager.getInstance();
    }

    private class BtnOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == getVerifyCodeBtn.getId()) {
                Toast.makeText(getApplicationContext(), "获取验证码", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == okBtn.getId()) {
                resetPassword();
            } else if (v.getId() == cancelBtn.getId()) {
                ResetPasswordActivity.this.finish();
            }
        }
    }

    private void resetPassword() {
        phone = phoneET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        String confirmPassword = confirmPasswordET.getText().toString().trim();
        if (phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误信息")
                    .setContentText("手机、密码不能为空，且两次输入密码需相同")
                    .show();
        } else {
            pDialog = new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("正在重置密码");
            pDialog.setCancelable(false);
            pDialog.show();
            new Thread(resetPasswordRequest).start();
        }
    }

    Runnable resetPasswordRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.resetPassword(phone, password);
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
                    pDialog = new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setTitleText("温馨提示");
                    pDialog.setContentText("密码修改成功");
                    pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ResetPasswordActivity.this.finish();
                        }
                    });
                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", password);
                    editor.apply();
                    break;
                }
                case MessageCode.MSG_REQUEST_ERROR: {
                    pDialog = new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                    pDialog = new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.ERROR_TYPE);
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
