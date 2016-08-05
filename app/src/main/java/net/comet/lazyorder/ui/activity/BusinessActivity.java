package net.comet.lazyorder.ui.activity;

import android.content.Intent;

import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.ui.Display;

public class BusinessActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_no_drawer;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            display.showBusinessTab((Business) intent.getParcelableExtra(Display.PARAM_OBJ));
        }
    }
}
