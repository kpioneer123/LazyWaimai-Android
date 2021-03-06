package net.comet.lazyorder.ui.activity;

import android.content.Intent;
import com.github.mzule.activityrouter.annotation.Router;
import net.comet.lazyorder.R;
import net.comet.lazyorder.ui.Display;

@Router("orders/:orderId")
public class OrderDetailActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_no_drawer;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            display.showOrderDetail(intent.getStringExtra("orderId"));
        }
    }
}
