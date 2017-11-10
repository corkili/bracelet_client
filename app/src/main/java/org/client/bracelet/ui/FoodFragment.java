package org.client.bracelet.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;

import org.client.bracelet.R;
import org.client.bracelet.entity.ApplicationManager;
import org.client.bracelet.entity.Food;
import org.client.bracelet.entity.Message;
import org.client.bracelet.entity.MessageCode;
import org.client.bracelet.entity.Recipe;
import org.client.bracelet.entity.ResponseCode;
import org.client.bracelet.utils.ViewFindUtils;
import org.client.bracelet.utils.Webservice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class FoodFragment extends Fragment {

    private static long oneDay;

    private RecyclerView mRecyclerView;
    private List<Food> mDatas;
    private FoodAdapter mAdapter;
    private ApplicationManager manager;
    private SweetAlertDialog pDialog;
    private JSONObject result;
    private BootstrapButton refreshBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SimpleDateFormat dateFormat;

    private static FoodFragment singleton;

    public static FoodFragment getInstance() {
        if (singleton == null) {
            singleton = new FoodFragment();
        }
        return singleton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = ApplicationManager.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        oneDay = 24 * 60 * 60 * 1000;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (ApplicationManager.getInstance().isLogin()) {
            v = inflater.inflate(R.layout.fragment_food, null);
            refreshBtn = ViewFindUtils.find(v, R.id.btn_refresh);
            try {
                String dateStr = sharedPreferences.getString("lastCacheRecipeTime", "");
                if ("".equals(dateStr)) {
                    manager.setLastCacheRecipeTime(new Date(System.currentTimeMillis() - oneDay));
                } else {
                    manager.setLastCacheRecipeTime(new Date(dateFormat.parse(dateStr).getTime()));
                }
            } catch (ParseException e) {
                manager.setLastCacheRecipeTime(new Date(System.currentTimeMillis() - oneDay));
            }
            String recipeJsonStr = sharedPreferences.getString("recipe", "");
            manager.setRecipe("".equals(recipeJsonStr) ? null : new Recipe(recipeJsonStr));
            initData();
            mRecyclerView = ViewFindUtils.find(v, R.id.id_foodlist);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mAdapter = new FoodAdapter());
            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!manager.hasCacheRecipe() || manager.needRefreshRecipe()) {
                        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("正在刷新");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        new Thread(refreshRequest).start();
                    } else {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("温馨提示")
                                .setContentText("已获取过最新的推荐食谱")
                                .show();
                    }
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
        if (manager.hasCacheRecipe()) {
            mDatas = manager.getRecipe().getFoods();
        } else {
            mDatas = new ArrayList<>();
        }
    }

    class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodHolder> {


        @Override
        public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FoodHolder holder = new FoodHolder(LayoutInflater.from(getActivity()).inflate(R.layout.food_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(FoodHolder holder, int position) {
            Food food = mDatas.get(position);
            System.out.println(food.getName());
            holder.foodName.setText(food.getName());
            holder.calories.setText(food.getHeatContent().intValue() + "千卡/100克");
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class FoodHolder extends RecyclerView.ViewHolder {

            AwesomeTextView foodName, calories;

            public FoodHolder(View itemView) {
                super(itemView);
                foodName = ViewFindUtils.find(itemView, R.id.food_name);
                calories = ViewFindUtils.find(itemView, R.id.calories);
            }
        }
    }

    Runnable refreshRequest = new Runnable() {
        @Override
        public void run() {
            android.os.Message msg = new android.os.Message();
            result = Webservice.refreshRecipe();
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
                        manager.setRecipe(new Recipe(result.getJSONObject("recipe").toString()));
                        manager.updateLastCacheRecipeTime();
                        editor = sharedPreferences.edit();
                        editor.putString("lastCacheRecipeTime", dateFormat.format(manager.getLastCacheRecipeTime()));
                        editor.putString("recipe", manager.getRecipe().toString());
                        editor.apply();
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
