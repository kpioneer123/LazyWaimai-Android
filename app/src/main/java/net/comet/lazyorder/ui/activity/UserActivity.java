package net.comet.lazyorder.ui.activity;

import android.content.Intent;
import net.comet.lazyorder.R;
import net.comet.lazyorder.ui.Display;

/**
 * author：cheikh.wang on 16/8/23 13:38
 * email：wanghonghi@126.com
 */
public class UserActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_no_drawer;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            display.showUserProfile();
        }
    }
}
