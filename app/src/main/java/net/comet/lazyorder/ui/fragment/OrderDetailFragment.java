package net.comet.lazyorder.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.Order;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.OrderController;
import net.comet.lazyorder.util.DateUtil;
import net.comet.lazyorder.widget.MultiStateView;
import net.comet.lazyorder.widget.OrderReportView;
import java.util.Date;
import butterknife.Bind;

public class OrderDetailFragment extends BaseFragment<OrderController.OrderUiCallbacks>
        implements OrderController.OrderDetailUi, BusinessNameClickHandle {

    @Bind(R.id.multi_state_view)
    MultiStateView mMultiStateView;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.orderReportView)
    OrderReportView mOrderReportView;

    @Bind(R.id.txt_deliver_time)
    TextView mDeliverTimeTxt;

    @Bind(R.id.txt_deliver_name)
    TextView mDeliverNameTxt;

    @Bind(R.id.txt_deliver_phone)
    TextView mDeliverPhoneTxt;

    @Bind(R.id.txt_deliver_address)
    TextView mDeliverAddressTxt;

    @Bind(R.id.txt_order_num)
    TextView mOrderNumTxt;

    @Bind(R.id.txt_created_at)
    TextView mCreateAtTxt;

    @Bind(R.id.txt_pay_method)
    TextView mPayMethodTxt;

    public static OrderDetailFragment create(String orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        if (!TextUtils.isEmpty(orderId)) {
            Bundle bundle = new Bundle();
            bundle.putString(Display.PARAM_ID, orderId);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getOrderController();
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_order_detail;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mMultiStateView.setState(MultiStateView.STATE_LOADING);

        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (hasCallbacks()) {
                    getCallbacks().refresh();
                }
            }
        });

        mOrderReportView.setNameClickHandle(this);
    }

    @Override
    public void onNetworkError(ResponseError error) {
        mMultiStateView.setState(MultiStateView.STATE_ERROR)
                .setTitle(error.getMessage())
                .setButton(null, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMultiStateView.setState(MultiStateView.STATE_LOADING);
                        getCallbacks().refresh();
                    }
                });
    }

    @Override
    public void setOrderInfo(Order order) {
        mMultiStateView.setState(MultiStateView.STATE_CONTENT);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        // 商品信息
        mOrderReportView.setupContent(order.getCartInfo());
        // 配送信息
        if (order.getBookedTime() == 0) {
            mDeliverTimeTxt.setText("立即送出");
        } else {
            mDeliverTimeTxt.setText(DateUtil.DateToString(new Date(order.getBookedTime()),
                    DateUtil.DateStyle.YYYY_MM_DD_HH_MM));
        }
        mDeliverNameTxt.setText(order.getConsignee());
        mDeliverPhoneTxt.setText(order.getPhone());
        mDeliverAddressTxt.setText(order.getAddress());
        // 订单信息
        mOrderNumTxt.setText(String.valueOf(order.getOrderNum()));
        mCreateAtTxt.setText(DateUtil.DateToString(new Date(order.getCreatedAt()), DateUtil.DateStyle.YYYY_MM_DD_HH_MM));
        mPayMethodTxt.setText(order.getPayMethod().getName());
    }

    @Override
    public String getRequestParameter() {
        return getArguments().getString(Display.PARAM_ID);
    }

    @Override
    public void onBusinessNameClick(Business business) {
        if (business != null && hasCallbacks()) {
            getCallbacks().showBusiness(business);
        }
    }
}
