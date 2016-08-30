package net.comet.lazyorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.levelmoney.velodrome.Velodrome;

import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.MainController;
import net.comet.lazyorder.util.ActivityStack;
import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private MainController mMainController;
    private Display mDisplay;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        ButterKnife.bind(this);

        ActivityStack.create().add(this);
        mDisplay = new Display(this);
        mMainController = AppContext.getContext().getMainController();

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        handleIntent(getIntent(), getDisplay());
        initialViews(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, getDisplay());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainController.attachDisplay(mDisplay);
        mMainController.init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainController.suspend();
        mMainController.detachDisplay(mDisplay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisplay = null;
        ActivityStack.create().remove(this);
    }

    protected void initialViews(Bundle savedInstanceState) {}

    protected void handleIntent(Intent intent, Display display) {}

    protected int getContentViewLayoutId() {
        return R.layout.activity_no_drawer;
    }

    @Override
    public void onBackPressed() {
        getMainController().onBackButtonPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getMainController().onBackButtonPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取MainController对象
     * @return
     */
    protected final MainController getMainController() {
        return mMainController;
    }

    /**
     * 获取display对象
     * @return
     */
    public Display getDisplay() {
        return mDisplay;
    }

    @Override
    public final void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }
}