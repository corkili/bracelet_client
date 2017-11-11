package org.client.bracelet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.MessageCode;
import org.client.bracelet.entity.ResponseCode;
import org.client.bracelet.entity.User;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/11.
 */

public class FriendFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<User> mDatas;
    private ApplicationManager manager;
    private SweetAlertDialog pDialog;
    private JSONObject result;
    private BootstrapButton addFriendBtn;
    private BootstrapEditText phoneET;
    private FriendAdapter mAdapter;
    private String friendPhone;

    private static FriendFragment singleton;

    public static FriendFragment getInstance() {
        if (singleton == null) {
            singleton = new FriendFragment();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = ApplicationManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (ApplicationManager.getInstance().isLogin()) {
            v = inflater.inflate(R.layout.fragment_friend, null);
            addFriendBtn = ViewFindUtils.find(v, R.id.btn_add_friend);
            phoneET = ViewFindUtils.find(v, R.id.edit_phone);
            initData();
            mRecyclerView = ViewFindUtils.find(v, R.id.id_recyclerview);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mAdapter = new FriendAdapter());
            addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriend();
                }
            });
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

    private void addFriend() {
        friendPhone = phoneET.getText().toString().trim();
        if (friendPhone.isEmpty()) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("请先输入要添加的好友的手机号")
                    .show();
        } else if (friendPhone.equals(manager.getUser().getPhone())) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("不能添加自己为好友")
                    .show();
        } else if (friendIsExist()) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("不能重复添加好友")
                    .show();
        } else {
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("正在添加好友");
            pDialog.setCancelable(false);
            pDialog.show();
            new Thread(friendRequest).start();
        }
    }

    private boolean friendIsExist() {
        for (User user : manager.getUser().getFriends()) {
            if (user.getPhone().equals(friendPhone)) {
                return true;
            }
        }
        return false;
    }

    private void initData() {
        mDatas = manager.getUser().getFriends();
    }

    class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {

        @Override
        public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FriendHolder holder = new FriendHolder(LayoutInflater.from(getActivity()).inflate(R.layout.friend_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(FriendHolder holder, int position) {
            holder.phone.setText(mDatas.get(position).getPhone());
            holder.name.setText(mDatas.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class FriendHolder extends RecyclerView.ViewHolder {

            AwesomeTextView phone, name;

            public FriendHolder(View itemView) {
                super(itemView);
                phone = ViewFindUtils.find(itemView, R.id.phone);
                name = ViewFindUtils.find(itemView, R.id.name);
            }
        }
    }

    Runnable friendRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.addFriend(friendPhone);
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
                        initData();
                        mAdapter.notifyItemRangeChanged(0, mDatas.size());
                    } catch (JSONException e) {
                        // do nothing
                    }
                    break;
                }
                case MessageCode.MSG_REQUEST_ERROR: {
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
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
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
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
