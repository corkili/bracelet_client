package org.client.bracelet.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.FoodType;
import org.client.bracelet.entity.User;
import org.client.bracelet.utils.ViewFindUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class SettingFragment extends Fragment {
    private static SettingFragment singleton;

    private AwesomeTextView usernameTV, nameTV, sexTV, birthdayTV, heightTV, weightTV, likeFoodsTV;

    private RelativeLayout likeFoodsLayout;

    private BootstrapButton editUserInfoBtn, editPasswordBtn, logoutBtn;

    private BtnOnClick btnOnClick;

    private SimpleDateFormat dateFormat;

    public static SettingFragment getInstance() {
        if (singleton == null) {
            singleton = new SettingFragment();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        btnOnClick = new BtnOnClick();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (ApplicationManager.getInstance().isLogin()) {
            v = inflater.inflate(R.layout.fragment_settings, null);
            usernameTV = ViewFindUtils.find(v, R.id.username);
            nameTV = ViewFindUtils.find(v, R.id.name);
            sexTV = ViewFindUtils.find(v, R.id.sex);
            birthdayTV = ViewFindUtils.find(v, R.id.birthday);
            heightTV = ViewFindUtils.find(v, R.id.height);
            weightTV = ViewFindUtils.find(v, R.id.weight);
            likeFoodsTV = ViewFindUtils.find(v, R.id.like_foods);

            likeFoodsLayout = ViewFindUtils.find(v, R.id.like_foods_layout);

            editUserInfoBtn = ViewFindUtils.find(v, R.id.btn_edit_info);
            editPasswordBtn = ViewFindUtils.find(v, R.id.btn_edit_password);
            logoutBtn = ViewFindUtils.find(v, R.id.btn_logout);

            editUserInfoBtn.setOnClickListener(btnOnClick);
            editPasswordBtn.setOnClickListener(btnOnClick);
            logoutBtn.setOnClickListener(btnOnClick);
            likeFoodsLayout.setOnClickListener(btnOnClick);

            usernameTV.setText("沐少");
            nameTV.setText("李浩然");
            sexTV.setText("男");
            birthdayTV.setText(dateFormat.format(new Date()));
            heightTV.setText("163厘米");
            weightTV.setText("50公斤");
            likeFoodsTV.setText("谷类 酒类 面食");
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
            if (v.getId() == editPasswordBtn.getId()) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ResetPasswordActivity.class);
                startActivity(intent);
            } else if (v.getId() == editUserInfoBtn.getId()) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ModifyUserInfoActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else if (v.getId() == logoutBtn.getId()) {
                ApplicationManager.getInstance().setUser(null);
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else if (v.getId() == likeFoodsLayout.getId()) {
                if (!ApplicationManager.getInstance().hasCacheFoodTypes()) {
                    // 从服务器获取列表
                }
                List<FoodType> foodTypes = ApplicationManager.getInstance().getCacheFoodTypes();
                final FoodType[] foodTypeArray = new FoodType[foodTypes.size()];
                String[] items = new String[foodTypes.size()];
                boolean[] checkeds = new boolean[foodTypes.size()];
                for (FoodType foodType : foodTypes) {
                    items[(int)(foodType.getId() - 1)] = foodType.getName();
                    foodTypeArray[(int)(foodType.getId() - 1)] = foodType;
                }
                for (int i = 0; i < checkeds.length; i++) {
                    checkeds[i] = false;
                }
                for (FoodType foodType : ApplicationManager.getInstance().getUser().getLikeFoods()) {
                    checkeds[(int)(foodType.getId() - 1)] = true;
                }
                final List<Integer> chose = new ArrayList<>();
                AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
                builder.setTitle("请勾选饮食偏好")
                        .setMultiChoiceItems(items, checkeds, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    chose.add(which);
                                } else {
                                    chose.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<FoodType> likeFoods = new ArrayList<>();
                                for (int i : chose) {
                                    likeFoods.add(foodTypeArray[i]);
                                }
                                ApplicationManager.getInstance().getUser().setLikeFoods(likeFoods);
                                // TODO 修改饮食偏好请求
                            }
                        });
                builder.create().show();
            }
        }
    }
}
