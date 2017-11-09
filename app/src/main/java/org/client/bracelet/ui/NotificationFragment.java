package org.client.bracelet.ui;

import android.content.Intent;
import android.os.Bundle;
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
import org.client.bracelet.utils.ViewFindUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class NotificationFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private NotificationAdapter mAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (ApplicationManager.getInstance().isLogin()) {
            v = inflater.inflate(R.layout.fragment_notification, null);
            initData();
            mRecyclerView = ViewFindUtils.find(v, R.id.id_recyclerview);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mAdapter = new NotificationAdapter());

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
        mDatas = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            mDatas.add(i + "");
        }
    }

    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MessageHoder> {

        @Override
        public MessageHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            MessageHoder hoder = new MessageHoder(LayoutInflater.from(getActivity()).inflate(R.layout.notification_item, parent, false));
            return hoder;
        }

        @Override
        public void onBindViewHolder(MessageHoder holder, int position) {
            holder.from.setText(mDatas.get(position));
            holder.message.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MessageHoder extends RecyclerView.ViewHolder {

            AwesomeTextView from, message;

            public MessageHoder(View itemView) {
                super(itemView);
                from = ViewFindUtils.find(itemView, R.id.from);
                message = ViewFindUtils.find(itemView, R.id.message);
            }
        }
    }


}
