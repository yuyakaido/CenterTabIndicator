package com.yuyakaido.android.centertabindicator.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.yuyakaido.android.centertabindicator.CenterTabIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yuyakaido on 2/23/15.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        viewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager(), getTabItems()));

        CenterTabIndicator centerTabIndicator = (CenterTabIndicator) findViewById(R.id.activity_main_center_tab_indicator);
        centerTabIndicator.setViewPager(viewPager);
        centerTabIndicator.setCenterTabColor(Color.BLUE);
        centerTabIndicator.setBackgroundColor(Color.LTGRAY);
    }

    private List<TabItem> getTabItems() {
        List<TabItem> tabItems = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < CenterTabIndicator.MAX_SIZE; i++) {
            int count = random.nextInt(7) + 4;
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < count; j++) {
                builder.append("A");
            }
            tabItems.add(new TabItem(builder.toString()));
        }
        return tabItems;
    }

    private static class MainViewPagerAdapter extends FragmentPagerAdapter {
        private List<TabItem> mTabItems;

        public MainViewPagerAdapter(FragmentManager fragmentManager, List<TabItem> tabItems) {
            super(fragmentManager);
            mTabItems = tabItems;
        }

        @Override
        public Fragment getItem(int i) {
            return TabContentFragment.newInstance(getTabItemAt(i));
        }

        @Override
        public int getCount() {
            return CenterTabIndicator.MAX_SIZE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            TabItem tabItem = getTabItemAt(position);
            return tabItem.getTitle();
        }

        private TabItem getTabItemAt(int position) {
            int actualPosition = position % mTabItems.size();
            return mTabItems.get(actualPosition);
        }
    }

}
