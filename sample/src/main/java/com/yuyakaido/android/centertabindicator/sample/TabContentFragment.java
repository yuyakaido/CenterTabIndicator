package com.yuyakaido.android.centertabindicator.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabContentFragment extends Fragment {

    private TabItem mTabItem;

    public static Fragment newInstance(TabItem tabItem) {
        TabContentFragment tabContentFragment = new TabContentFragment();
        tabContentFragment.mTabItem = tabItem;
        return tabContentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_content, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.fragment_tab_content_text_view)).setText(mTabItem.getTitle());
    }

}
