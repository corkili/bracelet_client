package org.client.bracelet.ui;

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
 * on 2017/11/8.
 */

public class RegisterActivity extends AppCompatActivity {

    private BootstrapButton birthdayBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        View v = getWindow().getDecorView();
        birthdayBtn = ViewFindUtils.find(v, R.id.edit_birthday);
        birthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "选择日期", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
