# CenterTabIndicator

## Sample
![Sample](https://github.com/yuyakaido/CenterTabIndicator/blob/master/sample/sample.gif)

## Usage
```java
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
```

## Download
```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.yuyakaido.android:center-tab-indicator:1.0.0'
}
```

## License
```
Copyright (C) 2015 yuyakaido. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
