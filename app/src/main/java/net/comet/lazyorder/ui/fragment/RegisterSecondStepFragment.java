package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
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
import net.comet.lazyorder.util.RegisterStep;
import net.comet.lazyorder.util.SystemUtil;
import net.comet.lazyorder.widget.CountDownTimerView;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * author：cheikh on 16/5/14 20:45
 * email：wanghonghi@126.com
 */
public class RegisterSecondStepFragment extends BaseRegisterStepFragment
        implements UserController.RegisterSecondStepUi {

    @Bind(R.id.txt_title)
    TextView mTitleTxt;

    @Bind(R.id.edit_code)
    EditText mCodeEdit;

    @Bind(R.id.btn_clear_code)
    ImageView mClearCodeBtn;

    @Bind(R.id.btn_send_code)
    CountDownTimerView mSendCodeBtn;

    @Bind(R.id.btn_submit)
    Button mSubmitCodeBtn;

    public static RegisterSecondStepFragment create(String mobile) {
        RegisterSecondStepFragment fragment = new RegisterSecondStepFragment();
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
        return R.layout.fragment_register_second_step;
    }

    @Override
    public RegisterStep getRegisterStep() {
        return RegisterStep.STEP_SECOND;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mTitleTxt.setText(getString(R.string.label_send_code_title,
                getRequestParameter()));
        mCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int visible = TextUtils.isEmpty(s.toString()) ? View.GONE : View.VISIBLE;
                mClearCodeBtn.setVisibility(visible);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {}
        });
        mSendCodeBtn.setOnCountDownListener(new CountDownTimerView.OnCountDownListener() {
            @Override
            public boolean onCountDownFinishState() {
                return !TextUtils.isEmpty(mCodeEdit.getText().toString());
            }
        });
        mSendCodeBtn.countDown(30000);
        showSnackbar(R.string.toast_success_send_sms_code);
    }

    @Override
    public void sendCodeFinish() {
        // 开始倒计时
        mSendCodeBtn.countDown(30000);
        cancelLoading();
        showSnackbar(R.string.toast_success_send_sms_code);
    }

    @Override
    public void verifyMobileFinish() {
        cancelLoading();
        mSubmitCodeBtn.setEnabled(true);
        Display display = getDisplay();
        if (display != null) {
            display.showRegisterStep(RegisterStep.STEP_THIRD,
                    getRequestParameter());
        }
    }

    @Override
    public void onNetworkError(ResponseError error) {
        cancelLoading();
        mSubmitCodeBtn.setEnabled(true);
        showSnackbar(error.getMessage());
    }

    @OnClick({R.id.btn_clear_code, R.id.btn_send_code, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear_code:
                mCodeEdit.setText("");
                break;
            case R.id.btn_send_code:
                sendSmsCode();
                break;
            case R.id.btn_submit:
                submitCode();
                break;
        }
    }

    /**
     * 执行发送验证码的操作
     */
    private void sendSmsCode() {
        if (hasCallbacks()) {
            showLoading(R.string.label_being_something);
            // 发起发送验证码的API请求
            getCallbacks().sendCode(getRequestParameter());
        }
    }

    /**
     * 执行发送验证码的操作
     */
    private void submitCode() {
        if (hasCallbacks()) {
            // 隐藏软键盘
            SystemUtil.hideKeyBoard(getContext());

            // 验证验证码是否为空
            final String code = mCodeEdit.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                showSnackbar(R.string.toast_error_empty_code);
                return;
            }

            showLoading(R.string.label_being_something);
            // 避免重复点击
            mSubmitCodeBtn.setEnabled(false);
            // 发起发送验证码的API请求
            getCallbacks().checkCode(getRequestParameter(), code);
        }
    }
}