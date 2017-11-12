package org.client.bracelet.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.client.bracelet.R;
import org.client.bracelet.utils.ViewFindUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Fragment> mFragments;
    private boolean[] fragmentUpdates;
    private String[] mTitles = {"运动", "膳食", "通知", "好友", "设置"};
    private View mDecorView;
    private SegmentTabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity.onCreate()...");
        setContentView(R.layout.activity_main);
        mFragments = new ArrayList<>();
        mFragments.add(SportFragment.getInstance());
        mFragments.add(FoodFragment.getInstance());
        mFragments.add(NotificationFragment.getInstance());
        mFragments.add(FriendFragment.getInstance());
        mFragments.add(SettingFragment.getInstance());

        fragmentUpdates = new boolean[mFragments.size()];
        for (int i = 0; i < fragmentUpdates.length; i++) {
            fragmentUpdates[i] = false;
        }

        mDecorView = getWindow().getDecorView();

        mTabLayout = ViewFindUtils.find(mDecorView, R.id.tl);

        init();
    }

    private void init() {
        final ViewPager vp = ViewFindUtils.find(mDecorView, R.id.vp);
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        mTabLayout.setTabData(mTitles);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp.setCurrentItem(position % mFragments.size());
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCurrentTab(position % mFragments.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp.setCurrentItem(mFragments.size() - 1);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        FragmentManager fm;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position % mFragments.size()];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position % mFragments.size());
        }
    }
}
