package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.OrderController;
import butterknife.Bind;
import butterknife.OnClick;


public class PaymentFragment extends BaseFragment<OrderController.OrderUiCallbacks>
        implements OrderController.OrderPaymentUi {

    @Bind(R.id.btn_payment)
    Button mBtnPayment;

    public static PaymentFragment create(String orderId) {
        PaymentFragment fragment = new PaymentFragment();
        if (!TextUtils.isEmpty(orderId)) {
            Bundle bundle = new Bundle();
            bundle.putString(Display.PARAM_ID, orderId);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_payment;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getOrderController();
    }

    @OnClick({R.id.btn_payment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_payment:
                payment();
                break;
        }
    }

    @Override
    public String getRequestParameter() {
        return getArguments().getString(Display.PARAM_ID);
    }

    /**
     * 执行付款操作
     */
    private void payment() {
        if (hasCallbacks()) {
            showLoading("正在模拟支付");
            mBtnPayment.setEnabled(false);
            getCallbacks().payment();
        }
    }

    @Override
    public void onNetworkError(ResponseError error) {
        cancelLoading();
        showSnackbar(error.getMessage());
    }

    @Override
    public void finishPayment(String orderId) {
        mBtnPayment.setEnabled(true);
        cancelLoading();
        showSnackbar(R.string.toast_success_online_payment);
        Display display = getDisplay();
        if (display != null) {
            display.startOrderDetailActivity(orderId);
            display.finishActivity();
        }
    }
}