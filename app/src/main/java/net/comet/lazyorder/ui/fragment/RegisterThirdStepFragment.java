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
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.UserController;
import net.comet.lazyorder.util.RegisterStep;
import net.comet.lazyorder.util.SystemUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * author：cheikh on 16/5/14 20:45
 * email：wanghonghi@126.com
 */
public class RegisterThirdStepFragment extends BaseRegisterStepFragment
        implements UserController.RegisterThirdStepUi {

    @Bind(R.id.edit_password)
    EditText mPasswordEdit;

    @Bind(R.id.btn_clear_password)
    ImageView mClearPasswordBtn;

    @Bind(R.id.edit_password_again)
    EditText mPasswordAgainEdit;

    @Bind(R.id.btn_clear_password_again)
    ImageView mClearPasswordAgainBtn;

    @Bind(R.id.btn_register)
    Button mRegisterBtn;

    public static RegisterThirdStepFragment create(String mobile) {
        RegisterThirdStepFragment fragment = new RegisterThirdStepFragment();
        if (!TextUtils.isEmpty(mobile)) {
            Bundle bundle = new Bundle();
            bundle.putString(Display.PARAM_OBJ, mobile);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getUserController();
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_register_third_step;
    }

    @Override
    public RegisterStep getRegisterStep() {
        return RegisterStep.STEP_THIRD;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int visible = TextUtils.isEmpty(s.toString()) ? View.GONE : View.VISIBLE;
                mClearPasswordBtn.setVisibility(visible);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}
        });
        mPasswordAgainEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int visible = TextUtils.isEmpty(s.toString()) ? View.GONE : View.VISIBLE;
                mClearPasswordAgainBtn.setVisibility(visible);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}
        });
    }

    @Override
    public void userCreateFinish() {
        cancelLoading();
        mRegisterBtn.setEnabled(true);
        showSnackbar(R.string.toast_success_register);

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

    @Override
    public void onNetworkError(ResponseError error) {
        cancelLoading();
        mRegisterBtn.setEnabled(true);
        showSnackbar(error.getMessage());
    }

    @OnClick({R.id.btn_clear_password, R.id.btn_clear_password_again, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear_password:
                mPasswordEdit.setText("");
                break;
            case R.id.btn_clear_password_again:
                mPasswordAgainEdit.setText("");
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }

    /**
     * 执行创建用户的操作
     */
    private void register() {
        if (hasCallbacks()) {
            // 隐藏软键盘
            SystemUtil.hideKeyBoard(getContext());

            // 验证密码是否为空
            final String password = mPasswordEdit.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                showSnackbar(R.string.toast_error_empty_password);
                return;
            }
            // 验证确认密码是否为空
            final String passwordAgain = mPasswordAgainEdit.getText().toString().trim();
            if (TextUtils.isEmpty(passwordAgain)) {
                showSnackbar(R.string.toast_error_empty_password_confirm);
                return;
            }
            // 验证两次的密码输入是否一致
            if (!password.equals(passwordAgain)) {
                showSnackbar(R.string.toast_error_password_not_consistent);
                return;
            }

            showLoading(R.string.label_being_something);
            // 避免重复点击
            mRegisterBtn.setEnabled(false);
            // 发起发送验证码的API请求
            getCallbacks().createUser(getRequestParameter(), password);
        }
    }
}