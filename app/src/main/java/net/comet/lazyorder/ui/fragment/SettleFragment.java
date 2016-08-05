package net.comet.lazyorder.ui.fragment;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.CartInfo;
import net.comet.lazyorder.model.bean.Order;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.bean.SettleResult;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.OrderController;
import net.comet.lazyorder.ui.adapter.SendTimeItemAdapter;
import net.comet.lazyorder.model.ShoppingCart;
import net.comet.lazyorder.util.ToastUtil;
import net.comet.lazyorder.widget.AddressView;
import net.comet.lazyorder.widget.MultiStateView;
import net.comet.lazyorder.widget.OrderReportView;
import butterknife.Bind;
import butterknife.OnClick;

public class SettleFragment extends BaseFragment<OrderController.OrderUiCallbacks>
        implements OrderController.OrderSettleUi {

    @Bind(R.id.multi_state_view)
    MultiStateView mMultiStateView;

    @Bind(R.id.view_address_info)
    AddressView mAddressInfoView;

    @Bind(R.id.txt_online_payment)
    TextView mOnlinePaymentTxt;

    @Bind(R.id.txt_offline_payment)
    TextView mOfflinePaymentTxt;

    @Bind(R.id.layout_send_time)
    View mSendTimeLayout;

    @Bind(R.id.txt_booked_time)
    TextView mBookedTimeTxt;

    @Bind(R.id.orderReportView)
    OrderReportView mOrderReportView;

    @Bind(R.id.btn_submit_order)
    Button mSubmitOrderBtn;

    private SendTimeItemAdapter mSendTimeAdapter;
    private int mCheckedSendTimeIndex;

    private Drawable mOfflineDrawable;
    private Drawable mOnlineDrawable;

    private CartInfo cartInfo;

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_settle;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getOrderController();
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mMultiStateView.setState(MultiStateView.STATE_LOADING);

        mSendTimeAdapter = new SendTimeItemAdapter(getActivity());

        mOfflineDrawable = getResources().getDrawable(R.drawable.ic_check_off);
        mOnlineDrawable = getResources().getDrawable(R.drawable.ic_check_on);
        mOfflineDrawable.setBounds(0, 0, mOfflineDrawable.getMinimumWidth(),
                mOfflineDrawable.getMinimumHeight());
        mOnlineDrawable.setBounds(0, 0, mOnlineDrawable.getMinimumWidth(),
                mOnlineDrawable.getMinimumHeight());
    }

    /**
     * 显示结算结果
     * @param result
     */
    @Override
    public void onSettleFinish(SettleResult result) {
        setIsPopulated(true);

        cancelLoading();

        mMultiStateView.setState(MultiStateView.STATE_CONTENT);

        // 收货地址
        mAddressInfoView.setData(result.getLastAddress());
        // 付款方式
        mOnlinePaymentTxt.setCompoundDrawables(null, null,
                result.isOnlinePayment() ? mOnlineDrawable : mOfflineDrawable, null);
        mOfflinePaymentTxt.setCompoundDrawables(null, null,
                result.isOnlinePayment() ? mOfflineDrawable : mOnlineDrawable, null);
        // 禁用当前选择的付款方式的点击
        mOnlinePaymentTxt.setEnabled(!result.isOnlinePayment());
        mOfflinePaymentTxt.setEnabled(result.isOnlinePayment());
        // 可选的送达时间
        mSendTimeAdapter.setItems(result.getBookingTimes());
        toggleSendTime(mCheckedSendTimeIndex);
        // 购物车信息
        cartInfo = result.getCartInfo();
        mOrderReportView.setupContent(cartInfo);
        // 控制提交订单的按钮的可用性
        mSubmitOrderBtn.setEnabled(result.isCanSubmit());
    }

    @Override
    public void onNetworkError(ResponseError error) {
        if (!isPopulated()) {
            mMultiStateView.setState(MultiStateView.STATE_ERROR)
                    .setTitle(error.getMessage())
                    .setButton(null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mMultiStateView.setState(MultiStateView.STATE_LOADING);
                            getCallbacks().resettle();
                        }
                    });
        } else {
            ToastUtil.showToast(error.getMessage());
        }
    }

    @Override
    public void orderCreateFinish(final Order order) {
        ShoppingCart.getInstance().clearAll();
        cancelLoading();
        showSnackbar(R.string.toast_success_order_create, Snackbar.LENGTH_SHORT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hasCallbacks()) {
                    getCallbacks().showPayment(order.getId());
                }
            }
        }, 2000);
    }

    @OnClick({R.id.view_address_info, R.id.txt_online_payment, R.id.txt_offline_payment, R.id.btn_submit_order, R.id.layout_send_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_address_info:
                if (hasCallbacks()) {
                    getCallbacks().chooseAddress();
                }
                break;
            case R.id.txt_online_payment:
                if (hasCallbacks()) {
                    showLoading(R.string.label_being_something);
                    getCallbacks().togglePayMethod(true);
                }
                break;
            case R.id.txt_offline_payment:
                if (hasCallbacks()) {
                    showLoading(R.string.label_being_something);
                    getCallbacks().togglePayMethod(false);
                }
                break;
            case R.id.btn_submit_order:
                if (hasCallbacks()) {
                    showLoading(R.string.label_being_something);
                    getCallbacks().orderCreate(cartInfo.getId(),
                            mSendTimeAdapter.getItem(mCheckedSendTimeIndex).getUnixTime(), null);
                }
                break;
            case R.id.layout_send_time:
                popupChooseBookedTime(mCheckedSendTimeIndex);
                break;
        }
    }

    /**
     * 切换显示送达时间
     * @param which
     */
    private void toggleSendTime(int which) {
        mCheckedSendTimeIndex = which;
        mBookedTimeTxt.setText(mSendTimeAdapter.getItem(which).getViewTime());
    }

    /**
     * 弹出选择预订时间的窗口
     */
    private void popupChooseBookedTime(int checked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择送达时间");
        builder.setSingleChoiceItems(mSendTimeAdapter, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                toggleSendTime(which);
            }
        });
        builder.show();
    }
}