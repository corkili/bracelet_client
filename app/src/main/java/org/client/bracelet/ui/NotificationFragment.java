package org.client.bracelet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.Message;
import org.client.bracelet.entity.MessageCode;
import org.client.bracelet.entity.ResponseCode;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class NotificationFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Message> mDatas;
    private NotificationAdapter mAdapter;
    private ApplicationManager manager;
    private SweetAlertDialog pDialog;
    private JSONObject result;
    private BootstrapButton refreshBtn;
    private SimpleDateFormat dateFormat;

    private static NotificationFragment singleton;

    public static NotificationFragment getInstance() {
        if (singleton == null) {
            singleton = new NotificationFragment();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = ApplicationManager.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (ApplicationManager.getInstance().isLogin()) {
            v = inflater.inflate(R.layout.fragment_notification, null);
            refreshBtn = ViewFindUtils.find(v, R.id.btn_refresh);
            initData();
            mRecyclerView = ViewFindUtils.find(v, R.id.id_recyclerview);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mAdapter = new NotificationAdapter());
            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("正在刷新");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    new Thread(refreshRequest).start();
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

    private void initData() {
        mDatas = manager.getUser().getMessages();
        Collections.sort(mDatas);
    }

    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MessageHolder> {

        @Override
        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MessageHolder holder = new MessageHolder(LayoutInflater.from(getActivity()).inflate(R.layout.notification_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MessageHolder holder, int position) {
            holder.from.setText(mDatas.get(position).getFromUserName() + "(" +
                    mDatas.get(position).getFromUserPhone() + ")");
            holder.message.setText(mDatas.get(position).getContent() + "<" + dateFormat.format(mDatas.get(position).getTime()) + ">");
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MessageHolder extends RecyclerView.ViewHolder {

            AwesomeTextView from, message;

            public MessageHolder(View itemView) {
                super(itemView);
                from = ViewFindUtils.find(itemView, R.id.from);
                message = ViewFindUtils.find(itemView, R.id.message);
            }
        }
    }

    Runnable refreshRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.refreshMessages();
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
                    List<Message> messages = new ArrayList<>();
                    try {
                        JSONArray messageArray = result.getJSONArray("messages");
                        for (int i = 0; i < messageArray.length(); i++) {
                            messages.add(new Message(messageArray.getJSONObject(i).toString()));
                        }
                        manager.getUser().setMessages(messages);
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
