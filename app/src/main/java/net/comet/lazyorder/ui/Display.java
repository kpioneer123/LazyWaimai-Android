package net.comet.lazyorder.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.google.common.base.Preconditions;
import com.orhanobut.logger.Logger;

import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppCookie;
import net.comet.lazyorder.model.Route;
import net.comet.lazyorder.model.bean.Address;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.ui.activity.AddressActivity;
import net.comet.lazyorder.ui.activity.BusinessActivity;
import net.comet.lazyorder.ui.activity.LoginActivity;
import net.comet.lazyorder.ui.activity.OrderDetailActivity;
import net.comet.lazyorder.ui.activity.RegisterActivity;
import net.comet.lazyorder.ui.activity.SettleActivity;
import net.comet.lazyorder.ui.fragment.AccountLoginFragment;
import net.comet.lazyorder.ui.fragment.AddressesFragment;
import net.comet.lazyorder.ui.fragment.AddressUpdateFragment;
import net.comet.lazyorder.ui.fragment.BusinessTabFragment;
import net.comet.lazyorder.ui.fragment.OrderDetailFragment;
import net.comet.lazyorder.ui.fragment.PaymentFragment;
import net.comet.lazyorder.ui.fragment.RegisterFirstStepFragment;
import net.comet.lazyorder.ui.fragment.RegisterSecondStepFragment;
import net.comet.lazyorder.ui.fragment.RegisterThirdStepFragment;
import net.comet.lazyorder.ui.fragment.SettleFragment;
import net.comet.lazyorder.util.Constants;
import net.comet.lazyorder.util.RegisterStep;

public class Display {

    public static final String PARAM_ID = "_id";
    public static final String PARAM_OBJ = "_obj";
    public static final String PARAM_PAGE = "_page";

    private final AppCompatActivity mActivity;

    public Display(AppCompatActivity activity) {
        mActivity = Preconditions.checkNotNull(activity, "activity cannot be null");
    }

    /**
     * 设置app bar上的主标题
     * @param title
     */
    public void setActionBarTitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }

    /**
     * 设置app bar上的子标题
     * @param title
     */
    public void setActionBarSubtitle(CharSequence title) {
        ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(title);
        }
    }

    /**
     * 显示向上导航的按钮
     * @param isShow
     */
    public void showUpNavigation(boolean isShow) {
        final ActionBar ab = mActivity.getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(isShow);
            ab.setHomeButtonEnabled(isShow);
            if (isShow) {
                ab.setHomeAsUpIndicator(R.drawable.ic_back);
            }
        }
    }

    /**
     * 判断当前activity是否已经嵌套了fragment
     * @return
     */
    public boolean hasMainFragment() {
        return getBackStackFragmentCount() > 0;
    }

    /**
     * 获取回退栈里fragment的数量
     * @return
     */
    public int getBackStackFragmentCount() {
        return mActivity.getSupportFragmentManager().getBackStackEntryCount();
    }

    /**
     * 弹出回退栈里最顶上的fragment
     * 如果栈里只有一个fragment的话,则在弹出的同时并结束掉当前的activity
     * @return
     */
    public boolean popTopFragmentBackStack() {
        final FragmentManager fm = mActivity.getSupportFragmentManager();
        final int backStackCount = fm.getBackStackEntryCount();
        if (backStackCount > 1) {
            fm.popBackStack();
            return true;
        }

        return false;
    }

    /**
     * 弹出回退栈里的所有fragment
     * @return
     */
    public boolean popEntireFragmentBackStack() {
        final FragmentManager fm = mActivity.getSupportFragmentManager();
        final int backStackCount = fm.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            fm.popBackStack();
        }
        return backStackCount > 0;
    }

    /**
     * 回退
     * @return
     */
    public void navigateUp() {
        final Intent intent = NavUtils.getParentActivityIntent(mActivity);
        if (intent != null) {
            NavUtils.navigateUpTo(mActivity, intent);
        } else {
            finishActivity();
        }
    }

    public void finishActivity() {
        mActivity.finish();
    }

    private void startActivity(Route route) {
        if (route.isIsNeedLogin() && !AppCookie.isLoggin()) {
            Intent intent = new Intent(mActivity, LoginActivity.class);
            intent.putExtra("CALLBACK_ROUTE", route);
            mActivity.startActivity(intent);
        } else {
            Intent intent = new Intent(mActivity, route.getTargetActivity());
            mActivity.startActivity(intent, route.getArguments());
        }
    }

    public void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        mActivity.startActivity(intent);
    }

    public void startBusinessActivity(Business business) {
        Intent intent = new Intent(mActivity, BusinessActivity.class);
        intent.putExtra(PARAM_OBJ, business);
        mActivity.startActivity(intent);
    }

    public void startLoginActivity() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivity(intent);
    }

    public void startRegisterActivity() {
        Intent intent = new Intent(mActivity, RegisterActivity.class);
        mActivity.startActivity(intent);
    }

    public void startSettingActivity() {
        Intent intent = new Intent(mActivity, SettleActivity.class);
        mActivity.startActivity(intent);
    }

    public void startOrderDetailActivity(String orderId) {
        Intent intent = new Intent(mActivity, OrderDetailActivity.class);
        intent.putExtra(PARAM_ID, orderId);
        mActivity.startActivity(intent);
    }

    public void startAddressListActivity() {
        Intent intent = new Intent(mActivity, AddressActivity.class);
        intent.putExtra(PARAM_PAGE, Constants.Page.PAGE_ADDRESS_LIST);
        mActivity.startActivity(intent);
    }

    public void startChooseAddressActivity() {
        Intent intent = new Intent(mActivity, AddressActivity.class);
        intent.putExtra(PARAM_PAGE, Constants.Page.PAGE_ADDRESS_CHOOSE);
        mActivity.startActivity(intent);
    }

    private void showFragment(Fragment fragment) {
        mActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(hasMainFragment() ? R.anim.right_in : R.anim.fade_in, R.anim.left_out,
                        R.anim.left_in, R.anim.right_out)
                .add(R.id.fragment_main, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void showAccountLogin() {
        showFragment(new AccountLoginFragment());
    }

    public void showRegisterStep(RegisterStep step, String mobile) {
        switch (step) {
            case STEP_FIRST:
                showFragment(new RegisterFirstStepFragment());
                break;
            case STEP_SECOND:
                showFragment(RegisterSecondStepFragment.create(mobile));
                break;
            case STEP_THIRD:
                showFragment(RegisterThirdStepFragment.create(mobile));
                break;
        }
    }

    public void showAddressList() {
        showFragment(new AddressesFragment());
    }

    public void showChooseAddress() {
        showFragment(AddressesFragment.choose());
    }

    public void showCreateAddress() {
        showFragment(new AddressUpdateFragment());
    }

    public void showChangeAddress(Address address) {
        showFragment(AddressUpdateFragment.create(address));
    }

    public void showSettle() {
        showFragment(new SettleFragment());
    }

    public void showPayment(String orderId) {
        showFragment(PaymentFragment.create(orderId));
    }

    public void showBusinessTab(Business business) {
        showFragment(BusinessTabFragment.create(business));
    }

    public void showOrderDetail(String orderId) {
        showFragment(OrderDetailFragment.create(orderId));
    }
}
