package org.client.bracelet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import org.client.bracelet.R;
import org.client.bracelet.utils.ViewFindUtils;

/**
 * Created by 李浩然
 * on 2017/11/9.
 */

public class ResetPasswordActivity extends AppCompatActivity {

    private BootstrapButton getVerifyCodeBtn, okBtn, cancelBtn;
    private BootstrapEditText phoneET, verifyCodeET, passwordET, confirmPasswordET;
    private BtnOnClick btnOnClick;

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
    }

    private class BtnOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == getVerifyCodeBtn.getId()) {
                Toast.makeText(getApplicationContext(), "获取验证码", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == okBtn.getId()) {
                Intent intent = new Intent();
                intent.setClass(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                ResetPasswordActivity.this.finish();
            } else if (v.getId() == cancelBtn.getId()) {
                ResetPasswordActivity.this.finish();
            }
        }
    }
}
