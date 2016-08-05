package net.comet.lazyorder.ui.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import net.comet.lazyorder.R;
import net.comet.lazyorder.ui.fragment.OrdersFragment;
import net.comet.lazyorder.ui.fragment.ShopFragment;
import net.comet.lazyorder.ui.fragment.UserCenterFragment;

import butterknife.Bind;

/**
 * author：cheikh on 16/5/9 15:02
 * email：wanghonghi@126.com
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    @Bind(R.id.viewpager_tab)
    SmartTabLayout mViewpagerTab;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        setupTabView();
    }

    protected void setupTabView() {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final int[] tabIcons = {R.drawable.tab_ic_home, R.drawable.tab_ic_orders, R.drawable.tab_ic_me};
        final int[] tabTitles = {R.string.tab_home, R.string.tab_orders, R.string.tab_me};
        FragmentPagerItems pages = FragmentPagerItems.with(this)
                .add(R.string.tab_home, ShopFragment.class)
                .add(R.string.tab_orders, OrdersFragment.class)
                .add(R.string.tab_me, UserCenterFragment.class)
                .create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                pages);

        mViewPager.setOffscreenPageLimit(pages.size());
        mViewPager.setAdapter(adapter);
        mViewpagerTab.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View view = inflater.inflate(R.layout.layout_navigation_bottom_item, container, false);
                ImageView iconView = (ImageView) view.findViewById(R.id.img_icon);
                iconView.setBackgroundResource(tabIcons[position % tabIcons.length]);
                TextView titleView = (TextView) view.findViewById(R.id.txt_title);
                titleView.setText(tabTitles[position % tabTitles.length]);
                return view;
            }
        });
        mViewpagerTab.setViewPager(mViewPager);
    }
}