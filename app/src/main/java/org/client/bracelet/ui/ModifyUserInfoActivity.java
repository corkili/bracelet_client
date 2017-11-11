package org.client.bracelet.ui;

import android.app.Activity;
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
import org.client.bracelet.entity.User;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/9.
 */

public class ModifyUserInfoActivity extends AppCompatActivity {

    private BootstrapEditText usernameET, nameET, weightET, heightET;
    private BootstrapButton okBtn, cancelBtn, birthdayBtn, maleBtn, femaleBtn;
    private Date date;
    private SimpleDateFormat dateFormat;
    private CustomDatePicker customDatePicker;
    private SweetAlertDialog pDialog;
    private JSONObject result;
    private ApplicationManager manager;
    private boolean isChanged, isRecipeReasonChanged;
    private User modifiedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_user_info_layout);
        View v = getWindow().getDecorView();
        usernameET = ViewFindUtils.find(v, R.id.edit_username);
        nameET = ViewFindUtils.find(v, R.id.edit_name);
        weightET = ViewFindUtils.find(v, R.id.edit_weight);
        heightET = ViewFindUtils.find(v, R.id.edit_height);
        okBtn = ViewFindUtils.find(v, R.id.btn_ok);
        cancelBtn = ViewFindUtils.find(v, R.id.btn_cancel);
        birthdayBtn = ViewFindUtils.find(v, R.id.edit_birthday);
        maleBtn = ViewFindUtils.find(v, R.id.edit_sex_male);
        femaleBtn = ViewFindUtils.find(v, R.id.edit_sex_female);
        initDatePicker();
        setListener();
        date = null;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        manager = ApplicationManager.getInstance();
        User user = manager.getUser();
        usernameET.setText(user.getUsername());
        nameET.setText(user.getName());
        heightET.setText(String.valueOf(user.getHeight()));
        weightET.setText(String.valueOf(user.getWeight()));
        if ("男".equals(user.getSex())) {
            maleBtn.setSelected(true);
            femaleBtn.setSelected(false);
        } else {
            maleBtn.setSelected(false);
            femaleBtn.setSelected(true);
        }
        date = user.getBirthday();
        birthdayBtn.setText(dateFormat.format(date));
        isChanged = false;
    }

    private void setListener() {
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyUserInfo();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ModifyUserInfoActivity.this, MainActivity.class);
                startActivity(intent);
                ModifyUserInfoActivity.this.finish();
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

    private void modifyUserInfo() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText("错误信息");

        String username = usernameET.getText().toString().trim();
        if (username.isEmpty()) {
            dialog.setContentText("用户名不能为空").show();
            return;
        }

        String name = nameET.getText().toString().trim();
        if (name.isEmpty()) {
            dialog.setContentText("姓名不能为空").show();
            return;
        }

        if (date == null) {
            dialog.setContentText("请选择出生日期").show();
            return;
        }

        Double height = 0.0;

        try{
            height = Double.parseDouble(heightET.getText().toString().trim());
            if (height < 30 || height > 300) {
                dialog.setContentText("身高范围应在30cm至300cm之间").show();
                return;
            }
        } catch (NumberFormatException e) {
            dialog.setContentText("身高范围应在30cm至300cm之间").show();
            return;
        }

        Double weight = 0.0;

        try{
            weight = Double.parseDouble(weightET.getText().toString().trim());
            if (weight < 5 || height > 400) {
                dialog.setContentText("体重范围应在5公斤至400公斤之间").show();
                return;
            }
        } catch (NumberFormatException e) {
            dialog.setContentText("体重范围应在5公斤至400公斤之间").show();
            return;
        }

        String sex = maleBtn.isSelected() ? "男" : "女";

        User user = manager.getUser();
        modifiedUser = new User(user.toString());

        if (!username.equals(user.getUsername())) {
            modifiedUser.setUsername(username);
            isChanged = true;
        }
        if (!name.equals(user.getName())) {
            modifiedUser.setName(name);
            isChanged = true;
        }
        if (!sex.equals(user.getSex())) {
            modifiedUser.setSex(sex);
            isChanged = true;
            isRecipeReasonChanged = true;
        }
        if (!dateFormat.format(date).equals(dateFormat.format(user.getBirthday()))) {
            modifiedUser.setBirthday(new java.sql.Date(date.getTime()));
            isChanged = true;
            isRecipeReasonChanged = true;
        }
        if (!height.equals(user.getHeight())) {
            modifiedUser.setHeight(height);
            isChanged = true;
            isRecipeReasonChanged = true;
        }
        if (!weight.equals(user.getWeight())) {
            modifiedUser.setWeight(weight);
            isChanged = true;
            isRecipeReasonChanged = true;
        }

        if (isChanged) {
            pDialog = new SweetAlertDialog(ModifyUserInfoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("正在请求修改信息");
            pDialog.setCancelable(false);
            pDialog.show();
            new Thread(modifyUserInfoRequest).start();
        } else {
            Toast.makeText(getApplicationContext(), "没有修改任何信息", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(ModifyUserInfoActivity.this, MainActivity.class);
            startActivity(intent);
            ModifyUserInfoActivity.this.finish();
        }
    }

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
                        if (isRecipeReasonChanged) {
                            manager.recipeReasonHasModified(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("recipeReasonHasModified", true);
                            editor.apply();
                        }
                        Intent intent = new Intent();
                        intent.setClass(ModifyUserInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        ModifyUserInfoActivity.this.finish();
                    } catch (JSONException e) {
                        pDialog = new SweetAlertDialog(ModifyUserInfoActivity.this, SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("错误提示");
                        pDialog.setContentText("客户端异常");
                        break;
                    }
                    break;
                }
                case MessageCode.MSG_REQUEST_ERROR: {
                    pDialog = new SweetAlertDialog(ModifyUserInfoActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                    pDialog = new SweetAlertDialog(ModifyUserInfoActivity.this, SweetAlertDialog.ERROR_TYPE);
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
