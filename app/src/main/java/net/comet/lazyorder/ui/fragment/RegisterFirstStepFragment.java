package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
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
public class RegisterFirstStepFragment extends BaseRegisterStepFragment
        implements UserController.RegisterFirstStepUi {

    @Bind(R.id.edit_mobile)
    EditText mMobileEdit;

    @Bind(R.id.btn_clear_mobile)
    ImageView mClearMobileBtn;

    @Bind(R.id.btn_send_code)
    Button mSendCodeBtn;

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getUserController();
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_register_first_step;
    }

    @Override
    public RegisterStep getRegisterStep() {
        return RegisterStep.STEP_FIRST;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mMobileEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int visible = TextUtils.isEmpty(s.toString()) ? View.GONE : View.VISIBLE;
                mClearMobileBtn.setVisibility(visible);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}
        });
    }

    @Override
    public void sendCodeFinish() {
        cancelLoading();
        mSendCodeBtn.setEnabled(true);
        // 跳转到步骤2页面
        Display display = getDisplay();
        if (display != null) {
            display.showRegisterStep(RegisterStep.STEP_SECOND,
                    mMobileEdit.getText().toString().trim());
        }
    }

    @Override
    public void onNetworkError(ResponseError error) {
        cancelLoading();
        mSendCodeBtn.setEnabled(true);
        showSnackbar(error.getMessage());
    }

    @OnClick({R.id.btn_clear_mobile, R.id.btn_send_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear_mobile:
                mMobileEdit.setText("");
                break;
            case R.id.btn_send_code:
                sendSmsCode();
                break;
        }
    }

    /**
     * 执行发送验证码的操作
     */
    private void sendSmsCode() {
        if (hasCallbacks()) {
            // 隐藏软键盘
            SystemUtil.hideKeyBoard(getContext());

            // 验证手机号是否为空
            final String mobile = mMobileEdit.getText().toString().trim();
            if (TextUtils.isEmpty(mobile)) {
                showSnackbar(R.string.toast_error_empty_phone);
                return;
            }

            showLoading(R.string.label_being_something);
            // 避免重复点击
            mSendCodeBtn.setEnabled(false);
            // 发起发送验证码的API请求
            getCallbacks().sendCode(mobile);
        }
    }
}