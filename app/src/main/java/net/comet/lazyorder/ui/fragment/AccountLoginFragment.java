package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.UserController;
import net.comet.lazyorder.util.SystemUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class AccountLoginFragment extends BaseFragment<UserController.UserUiCallbacks>
        implements UserController.UserLoginUi {

    @Bind(R.id.et_user_account)
    EditText mEtAccount;

    @Bind(R.id.et_user_password)
    EditText mEtPassword;

    @Bind(R.id.iv_delete_account)
    ImageView mIvDeleteAccount;

    @Bind(R.id.iv_delete_password)
    ImageView mIvDeletePassword;

    @Bind(R.id.btn_login)
    Button mBtnLogin;

    @Bind(R.id.tv_forget_password)
    TextView mTvForgetPassword;

    @Bind(R.id.tv_go_to_register)
    TextView mTvGoToRegister;

    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getUserController();
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mEtAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int visible = TextUtils.isEmpty(s.toString()) ? View.GONE : View.VISIBLE;
                mIvDeleteAccount.setVisibility(visible);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }
        });
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int visible = TextUtils.isEmpty(s.toString()) ? View.GONE : View.VISIBLE;
                mIvDeletePassword.setVisibility(visible);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }
        });
    }

    @Override
    public void onNetworkError(ResponseError error) {
        cancelLoading();
        mBtnLogin.setEnabled(true);
        showSnackbar(error.getMessage());
    }

    @Override
    public void userLoginFinish() {
        cancelLoading();
        showSnackbar(R.string.toast_success_login);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Display display = getDisplay();
                if (display != null) {
                    display.finishActivity();
                }
            }
        }, 1500);
    }

    @OnClick({R.id.iv_delete_account, R.id.iv_delete_password, R.id.btn_login, R.id.tv_forget_password, R.id.tv_go_to_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_delete_account:
                mEtAccount.setText("");
                break;
            case R.id.iv_delete_password:
                mEtPassword.setText("");
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_forget_password:
                if (hasCallbacks()) {
                    getCallbacks().showRegister();
                }
                break;
            case R.id.tv_go_to_register:
                if (hasCallbacks()) {
                    getCallbacks().showRegister();
                }
                break;
        }
    }

    /**
     * 执行登录操作
     */
    private void login() {
        if (hasCallbacks()) {
            // 隐藏软键盘
            SystemUtil.hideKeyBoard(getContext());

            // 验证用户名是否为空
            final String account = mEtAccount.getText().toString().trim();
            if (TextUtils.isEmpty(account)) {
                showSnackbar(R.string.toast_error_empty_account);
                return;
            }
            // 验证密码是否为空
            final String password = mEtPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                showSnackbar(R.string.toast_error_empty_password);
                return;
            }
            // 禁用登录按钮,避免重复点击
            mBtnLogin.setEnabled(false);
            // 显示提示对话框
            showLoading(R.string.label_being_something);
            // 发起登录的网络请求
            getCallbacks().login(account, password);
        }
    }
}
