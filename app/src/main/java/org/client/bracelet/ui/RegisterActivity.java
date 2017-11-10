package org.client.bracelet.ui;

import android.content.DialogInterface;
import android.content.Intent;
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
import org.client.bracelet.entity.Message;
import org.client.bracelet.entity.MessageCode;
import org.client.bracelet.entity.ResponseCode;
import org.client.bracelet.entity.User;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class RegisterActivity extends AppCompatActivity {

    private BootstrapEditText phoneET, passwordET, confirmPasswordET, usernameET, nameET, weightET, heightET;
    private BootstrapButton registerBtn, cancelBtn, birthdayBtn, maleBtn, femaleBtn;
    private Date date;
    private SimpleDateFormat dateFormat;
    private CustomDatePicker customDatePicker;
    private User user;
    private JSONObject result;
    private SweetAlertDialog pDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        View v = getWindow().getDecorView();
        phoneET = ViewFindUtils.find(v, R.id.edit_phone);
        passwordET = ViewFindUtils.find(v, R.id.edit_password);
        confirmPasswordET = ViewFindUtils.find(v, R.id.edit_confirm_password);
        usernameET = ViewFindUtils.find(v, R.id.edit_username);
        nameET = ViewFindUtils.find(v, R.id.edit_name);
        weightET = ViewFindUtils.find(v, R.id.edit_weight);
        heightET = ViewFindUtils.find(v, R.id.edit_height);
        registerBtn = ViewFindUtils.find(v, R.id.btn_register);
        cancelBtn = ViewFindUtils.find(v, R.id.btn_cancel);
        birthdayBtn = ViewFindUtils.find(v, R.id.edit_birthday);
        maleBtn = ViewFindUtils.find(v, R.id.edit_sex_male);
        femaleBtn = ViewFindUtils.find(v, R.id.edit_sex_female);
        initDatePicker();
        setListener();
        date = null;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    private void setListener() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInformation()) {
                    pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("正在注册");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    new Thread(registerRequest).start();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
        });

        birthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date == null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                    String now = sdf.format(new Date());
                    birthdayBtn.setText(now.split(" ")[0]);
                }
                customDatePicker.show(birthdayBtn.getText().toString());
            }
        });
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());

        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                birthdayBtn.setText(time.split(" ")[0]);
                try {
                    date = dateFormat.parse(birthdayBtn.getText().toString());
                } catch (ParseException e) {
                    birthdayBtn.setText(null);
                    date = null;
                }
            }
        }, "1900-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行

        customDatePicker.showSpecificTime(false); // 不显示时和分
        customDatePicker.setIsLoop(false); // 不允许循环滚动

    }

    private boolean checkInformation() {
        String phone = phoneET.getText().toString().trim();
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText("错误信息");
        if (phone.isEmpty() || phone.length() != 11) {
            dialog.setContentText("手机号不能为空，且长度应为11位").show();
            return false;
        }

        String password = passwordET.getText().toString().trim();
        String confirmPassword = confirmPasswordET.getText().toString().trim();
        if (password.isEmpty() || confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            dialog.setContentText("密码不能为空，且两次输入密码必须一致").show();
            return false;
        }

        String username = usernameET.getText().toString().trim();
        if (username.isEmpty()) {
            dialog.setContentText("用户名不能为空").show();
            return false;
        }

        String name = nameET.getText().toString().trim();
        if (name.isEmpty()) {
            dialog.setContentText("姓名不能为空").show();
            return false;
        }

        if (date == null) {
            dialog.setContentText("请选择出生日期").show();
            return false;
        }

        Double height = 0.0;

        try{
            height = Double.parseDouble(heightET.getText().toString().trim());
            if (height < 30 || height > 300) {
                dialog.setContentText("身高范围应在30cm至300cm之间").show();
                return false;
            }
        } catch (NumberFormatException e) {
            dialog.setContentText("身高范围应在30cm至300cm之间").show();
            return false;
        }

        Double weight = 0.0;

        try{
            weight = Double.parseDouble(weightET.getText().toString().trim());
            if (weight < 5 || height > 400) {
                dialog.setContentText("体重范围应在5公斤至400公斤之间").show();
                return false;
            }
        } catch (NumberFormatException e) {
            dialog.setContentText("体重范围应在5公斤至400公斤之间").show();
            return false;
        }

        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setSex(maleBtn.isSelected() ? "男" : "女");
        user.setBirthday(new java.sql.Date(date.getTime()));
        user.setWeight(weight);
        user.setHeight(height);
        user.setPhone(phone);
        user.setRegisterTime(new Date());
        user.setLastLoginTime(new Date());
        return true;
    }

    Runnable registerRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.register(user);
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
                    pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setTitleText("温馨提示");
                    pDialog.setContentText("注册成功");
                    pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent intent = new Intent();
                            intent.setClass(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            RegisterActivity.this.finish();
                        }
                    });
                    break;
                }
                case MessageCode.MSG_REQUEST_ERROR: {
                    pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                    pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("异常提示");
                    pDialog.setContentText(resMsg);
                    break;
                }
            }
            pDialog.show();
        }
    };
}
