package net.comet.lazyorder.ui.activity;

import android.content.Intent;
import net.comet.lazyorder.R;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.util.Constants.Page;

public class AddressActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_no_drawer;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (intent.hasExtra(Display.PARAM_PAGE)) {
            String page = intent.getStringExtra(Display.PARAM_PAGE);
            if (page.equals(Page.PAGE_ADDRESS_CHOOSE)) {
                display.showChooseAddress();
            } else {
                display.showAddressList();
            }
        } else {
            display.showAddressList();
        }
    }
}
