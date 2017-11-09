package org.client.bracelet.ui;

import android.content.Intent;
import android.os.Bundle;
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
import org.client.bracelet.entity.Recipe;
import org.client.bracelet.utils.ViewFindUtils;

import java.util.List;

/**
 * Created by 李浩然
 * on 2017/11/8.
 */

public class FoodFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Food> mDatas;
    private FoodAdapter mAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if (ApplicationManager.getInstance().isLogin()) {
            if (!ApplicationManager.getInstance().hasCacheRecipe()) {
                Toast.makeText(getActivity().getApplicationContext(), "正在获取食谱...", Toast.LENGTH_LONG).show();
                ApplicationManager.getInstance().hasCacheRecipe(true);
                ApplicationManager.getInstance().setRecipe(new Recipe());
            }
            v = inflater.inflate(R.layout.fragment_food, null);
            initData();
            mRecyclerView = ViewFindUtils.find(v, R.id.id_foodlist);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mAdapter = new FoodAdapter());
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
        mDatas = ApplicationManager.getInstance().getRecipe().getFoods();
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
}
