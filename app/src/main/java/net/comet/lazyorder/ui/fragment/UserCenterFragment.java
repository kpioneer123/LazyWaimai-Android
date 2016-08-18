package net.comet.lazyorder.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.bean.User;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.UserController;
import net.comet.lazyorder.util.SystemUtil;
import net.comet.lazyorder.widget.section.SectionTextItemView;
import butterknife.Bind;
import butterknife.OnClick;

public class UserCenterFragment extends BaseFragment<UserController.UserUiCallbacks>
        implements UserController.UserCenterUi {

    @Bind(R.id.layout_login_before)
    View mLoginBeforeLayout;

    @Bind(R.id.layout_login_after)
    View mLoginAfterLayout;

    @Bind(R.id.img_avatar)
    ImageView mAvatarImg;

    @Bind(R.id.txt_account_name)
    TextView mAccountNameTxt;

    @Bind(R.id.txt_user_phone)
    TextView mUserPhoneTxt;

    @Bind(R.id.btn_my_address)
    SectionTextItemView mManageAddressBtn;

    @Bind(R.id.btn_update_account)
    SectionTextItemView mUpdateAccountBtn;

    @Bind(R.id.btn_update_password)
    SectionTextItemView mUpdatePasswordBtn;

    @Bind(R.id.btn_check_update)
    SectionTextItemView mCheckUpdateBtn;

    @Bind(R.id.btn_help_feedback)
    SectionTextItemView mHelpFeedbackBtn;

    @Bind(R.id.layout_logout)
    View mLogoutLayout;

    @Bind(R.id.btn_logout)
    TextView mLogoutBtn;

    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getUserController();
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_user_center;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        String versionName = SystemUtil.getAppVersionName(getActivity());
        mCheckUpdateBtn.setText(getString(R.string.label_current_version_name, versionName));
    }

    @OnClick({R.id.layout_login_before, R.id.btn_check_update, R.id.btn_my_address, R.id.btn_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_login_before:
                if (hasCallbacks()) {
                    getCallbacks().showLogin();
                }
                break;
            case R.id.btn_my_address:
                if (hasCallbacks()) {
                    getCallbacks().showAddressList();
                }
                break;
            case R.id.btn_check_update:
                if (hasCallbacks()) {
                    getCallbacks().checkUpdate();
                }
                break;
            case R.id.btn_logout:
                doLogout();
                break;
        }
    }

    @Override
    public void onNetworkError(ResponseError error) {
        showSnackbar(error.getMessage());
    }

    @Override
    public void showUserInfo(User userInfo) {
        if (userInfo != null) {
            mLoginBeforeLayout.setVisibility(View.GONE);
            mLoginAfterLayout.setVisibility(View.VISIBLE);
            mLogoutLayout.setVisibility(View.VISIBLE);

            String url = userInfo.getAvatarUrl();
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(getContext())
                        .load(userInfo.getAvatarUrl())
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(mAvatarImg);
            }
            mAccountNameTxt.setText(userInfo.getUsername());
            mUserPhoneTxt.setText(userInfo.getMobile());
        } else {
            mLoginBeforeLayout.setVisibility(View.VISIBLE);
            mLoginAfterLayout.setVisibility(View.GONE);
            mLogoutLayout.setVisibility(View.GONE);
        }
    }

    private void doLogout() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getCallbacks().logout();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
                .show();
    }
}
