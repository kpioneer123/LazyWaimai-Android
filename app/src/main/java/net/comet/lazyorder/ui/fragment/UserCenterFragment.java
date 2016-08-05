package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
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
import net.comet.lazyorder.widget.SectionTextView;
import butterknife.Bind;
import butterknife.OnClick;

public class UserCenterFragment extends BaseFragment<UserController.UserUiCallbacks>
        implements UserController.UserCenterUi {

    @Bind(R.id.layout_login_before)
    View mLoginBeforeLayout;

    @Bind(R.id.layout_login_after)
    View mLoginAfterLayout;

    @Bind(R.id.img_account_avatar)
    ImageView mImgAccountAvatar;

    @Bind(R.id.txt_account_name)
    TextView mTxtAccountName;

    @Bind(R.id.txt_user_phone)
    TextView mTxtUserPhone;

    @Bind(R.id.btn_my_address)
    SectionTextView mBtnManageAddress;

    @Bind(R.id.btn_update_account)
    SectionTextView mBtnUpdateAccount;

    @Bind(R.id.btn_update_password)
    SectionTextView mBtnUpdatePassword;

    @Bind(R.id.btn_check_update)
    SectionTextView mBtnCheckUpdate;

    @Bind(R.id.btn_help_feedback)
    SectionTextView mBtnHelpFeedback;

    @Bind(R.id.btn_logout)
    TextView mLogoutBtn;

    private User mUserInfo;

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
        mBtnCheckUpdate.setSubTitle(getString(R.string.label_current_version_name, versionName));
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
                if (hasCallbacks()) {
                    getCallbacks().logout();
                }
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
            mUserInfo = userInfo;

            mLoginBeforeLayout.setVisibility(View.GONE);
            mLoginAfterLayout.setVisibility(View.VISIBLE);
            mLogoutBtn.setVisibility(View.VISIBLE);

            String url = mUserInfo.getAvatarUrl();
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(getContext())
                        .load(mUserInfo.getAvatarUrl())
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(mImgAccountAvatar);
            }
            mTxtAccountName.setText(mUserInfo.getUsername());
            mTxtUserPhone.setText(userInfo.getMobile());
        } else {
            mLoginBeforeLayout.setVisibility(View.VISIBLE);
            mLoginAfterLayout.setVisibility(View.GONE);
            mLogoutBtn.setVisibility(View.GONE);
        }
    }
}
