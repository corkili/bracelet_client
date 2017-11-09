package org.client.bracelet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.utils.ViewFindUtils;

import java.util.Date;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class SportFragment extends Fragment {

    private AwesomeTextView stepsTV, kilometreTV, sleepTV;
    private BootstrapButton refreshBtn, bandBraceletBtn, heartRateBtn;

    private BtnOnClick btnOnClick;

    private static SportFragment singleton;

    public static SportFragment getInstance() {
        if (singleton == null) {
            singleton = new SportFragment();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnOnClick = new BtnOnClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (ApplicationManager.getInstance().isLogin()) {
            v = inflater.inflate(R.layout.fragment_sport, null);
            stepsTV = ViewFindUtils.find(v, R.id.steps);
            kilometreTV = ViewFindUtils.find(v, R.id.kilometre);
            sleepTV = ViewFindUtils.find(v, R.id.sleep);
            refreshBtn = ViewFindUtils.find(v, R.id.btn_refresh);
            bandBraceletBtn = ViewFindUtils.find(v, R.id.btn_band_bracelet);
            heartRateBtn = ViewFindUtils.find(v, R.id.btn_heart_rate);

            refreshBtn.setOnClickListener(btnOnClick);
            bandBraceletBtn.setOnClickListener(btnOnClick);
            heartRateBtn.setOnClickListener(btnOnClick);

            if (ApplicationManager.getInstance().hasBandBracelet()) {
                bandBraceletBtn.setText("解除绑定");
            } else {
                bandBraceletBtn.setText("绑定设备");
            }

            stepsTV.setText("1000步");
            kilometreTV.setText("3.2公里");
            sleepTV.setText("8.3小时");
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
            if (v.getId() == refreshBtn.getId()) {
                Toast.makeText(getActivity(), "刷新数据", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == bandBraceletBtn.getId()) {
                if (ApplicationManager.getInstance().hasBandBracelet()) {
                    Toast.makeText(getActivity(), "解除绑定", Toast.LENGTH_SHORT).show();
                    ApplicationManager.getInstance().hasBandBracelet(false);
                    bandBraceletBtn.setText("绑定设备");
                } else {
                    Toast.makeText(getActivity(), "绑定手环", Toast.LENGTH_SHORT).show();
                    ApplicationManager.getInstance().hasBandBracelet(true);
                    bandBraceletBtn.setText("解除绑定");
                }
            } else if (v.getId() == heartRateBtn.getId()) {
                Toast.makeText(getActivity(), "测试心率", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
