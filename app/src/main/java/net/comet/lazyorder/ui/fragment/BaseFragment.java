package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.comet.lazyorder.R;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.activity.BaseActivity;
import net.comet.lazyorder.widget.LoadingDialog;
import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseFragment<UC> extends Fragment
        implements BaseUiController.Ui<UC> {

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private UC mCallbacks;
    private boolean mIsPopulated;

    private LoadingDialog mLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getViewLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        handleArguments(getArguments());
        initialViews(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getController().attachUi(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().startUi(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getController().detachUi(this);
    }

    protected void handleArguments(Bundle arguments) {}

    protected abstract int getViewLayoutId();

    protected void initialViews(Bundle savedInstanceState) {}

    protected final boolean hasCallbacks() {
        return mCallbacks != null;
    }

    protected final UC getCallbacks() {
        return mCallbacks;
    }

    @Override
    public void setCallbacks(UC callbacks) {
        mCallbacks = callbacks;
    }

    public void setIsPopulated(boolean isPopulated) {
        mIsPopulated = isPopulated;
    }

    @Override
    public boolean isPopulated() {
        return mIsPopulated;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    protected abstract BaseUiController getController();

    protected final void showSnackbar(int textResId) {
        Snackbar.make(getView(), getString(textResId), Snackbar.LENGTH_LONG).show();
    }

    protected final void showSnackbar(String text) {
        Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).show();
    }

    protected final void showSnackbar(int textResId, int duration) {
        Snackbar.make(getView(), getString(textResId), duration).show();
    }

    protected final void showSnackbar(String text, int duration) {
        Snackbar.make(getView(), text, duration).show();
    }

    protected final void showLoading(@StringRes int textResId) {
        showLoading(getString(textResId));
    }

    protected final void showLoading(String text) {
        cancelLoading();
        if (mLoading == null) {
            mLoading = new LoadingDialog(getContext());
            mLoading.setCancelable(false);
            mLoading.setCanceledOnTouchOutside(false);
        }
        mLoading.setTitle(text);
        mLoading.show();
    }

    protected final void cancelLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }

    protected Display getDisplay() {
        return ((BaseActivity) getActivity()).getDisplay();
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }

    protected void setSupportActionBar(Toolbar toolbar) {
        ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
    }

    protected ActionBar getSupportActionBar() {
        return ((BaseActivity) getActivity()).getSupportActionBar();
    }
}