package org.client.bracelet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.User;
import org.client.bracelet.utils.ViewFindUtils;


public class LoginActivity extends AppCompatActivity {
    private BootstrapButton toRegisterBtn, forgetPasswordBtn, loginBtn;

    private BootstrapEditText phoneET, passwordET;

    private BtnOnClick btnOnClick;

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
                ApplicationManager.getInstance().setUser(new User());
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
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
}
