package org.client.bracelet.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.beardedhen.androidbootstrap.BootstrapButton;

import org.client.bracelet.R;
import org.client.bracelet.utils.ViewFindUtils;


public class LoginActivity extends AppCompatActivity {
    private BootstrapButton toRegisterBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        View v = getWindow().getDecorView();
        toRegisterBtn = ViewFindUtils.find(v, R.id.btn_register);
        toRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });
    }



}
