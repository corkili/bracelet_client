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

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
    }

    private void setListener() {
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date != null) {
                    Toast.makeText(getApplicationContext(), maleBtn.isSelected() + " " + femaleBtn.isSelected() + " " + dateFormat.format(date), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(ModifyUserInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                    ModifyUserInfoActivity.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "请选择生日", Toast.LENGTH_SHORT).show();
                }
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
}
