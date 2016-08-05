package net.comet.lazyorder.ui;

import android.os.Handler;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppCookie;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.Order;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.bean.ResultsPage;
import net.comet.lazyorder.model.bean.SettleResult;
import net.comet.lazyorder.model.bean.User;
import net.comet.lazyorder.model.event.AccountChangedEvent;
import net.comet.lazyorder.network.RestApiClient;
import net.comet.lazyorder.network.action.ErrorAction;
import net.comet.lazyorder.model.ShoppingCart;
import net.comet.lazyorder.util.EventUtil;
import net.comet.lazyorder.util.StringFetcher;
import java.util.List;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class OrderController extends BaseUiController<OrderController.OrderUi,
        OrderController.OrderUiCallbacks> {

    private static final String LOG_TAG = OrderController.class.getSimpleName();

    private final RestApiClient mRestApiClient;

    private int mPageIndex;

    @Inject
    public OrderController(RestApiClient restApiClient) {
        super();
        mRestApiClient = Preconditions.checkNotNull(restApiClient, "restApiClient cannot be null");
    }

    @Subscribe
    public void onAccountChanged(AccountChangedEvent event) {
        User user = event.getUser();
        if (user != null) {
            AppCookie.saveUserInfo(user);
            AppCookie.saveAccessToken(user.getAccessToken());
            AppCookie.saveLastPhone(user.getMobile());
            mRestApiClient.setToken(user.getAccessToken());
        } else {
            AppCookie.saveUserInfo(null);
            AppCookie.saveAccessToken(null);
            mRestApiClient.setToken(null);
        }

        populateUis();
    }

    @Override
    protected void onInited() {
        super.onInited();
        EventUtil.register(this);
    }

    @Override
    protected void onSuspended() {
        EventUtil.unregister(this);
        super.onSuspended();
    }

    @Override
    protected String getUiTitle(OrderUi ui) {
        if (ui instanceof OrderSettleUi) {
            return StringFetcher.getString(R.string.title_settle);
        } else if (ui instanceof OrderListUi) {
            return StringFetcher.getString(R.string.title_order);
        } else if (ui instanceof OrderPaymentUi) {
            return StringFetcher.getString(R.string.title_online_payment);
        } else if (ui instanceof OrderDetailUi) {
            return StringFetcher.getString(R.string.title_order_detail);
        }
        return null;
    }

    @Override
    protected void populateUi(OrderUi ui) {
        if (ui instanceof OrderSettleUi) {
            populateOrderSettleUi((OrderSettleUi) ui);
        } else if (ui instanceof OrderListUi) {
            populateOrderListUi((OrderListUi) ui);
        } else if (ui instanceof OrderDetailUi) {
            populateOrderDetailUi((OrderDetailUi) ui);
        }
    }

    private void populateOrderSettleUi(OrderSettleUi ui) {
        doOrderSettle(getId(ui), true);
    }

    private void populateOrderListUi(OrderListUi ui) {
        fetchOrders(getId(ui));
    }

    private void populateOrderDetailUi(OrderDetailUi ui) {
        fetchOrderDetail(getId(ui), ui.getRequestParameter());
    }

    private void fetchOrders(int callingId) {
        mPageIndex = 1;
        fetchOrders(callingId, mPageIndex);
    }

    private void fetchOrders(final int callingId, final int page) {
        mRestApiClient.orderService()
                .orders(page)
                .map(new Func1<ResultsPage<Order>, List<Order>>() {
                    @Override
                    public List<Order> call(ResultsPage<Order> results) {
                        return results.results;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Order>>() {
                    @Override
                    public void call(List<Order> orders) {
                        OrderUi ui = findUi(callingId);
                        if (ui instanceof OrderListUi) {
                            ((OrderListUi) ui).onChangeItem(orders, mPageIndex);
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        OrderUi ui = findUi(callingId);
                        if (ui instanceof OrderListUi) {
                            ((OrderListUi) ui).onNetworkError(error, mPageIndex);
                        }
                    }
                });
    }

    /**
     * 获取订单详情
     * @param callingId
     */
    private void fetchOrderDetail(final int callingId, String id) {
        mRestApiClient.orderService()
                .detail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Order>() {
                    @Override
                    public void call(Order order) {
                        OrderUi ui = findUi(callingId);
                        if (ui instanceof OrderDetailUi) {
                            ((OrderDetailUi) ui).setOrderInfo(order);
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        OrderUi ui = findUi(callingId);
                        if (ui instanceof OrderDetailUi) {
                            ((OrderDetailUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    /**
     * 执行订单结算
     * @param callingId
     */
    private void doOrderSettle(final int callingId, boolean isOnlinePayment) {
        ShoppingCart shoppingCart = ShoppingCart.getInstance();
        mRestApiClient.orderService()
                .settle(shoppingCart.getBusinessId(), isOnlinePayment ? 1 : 0,
                        new Gson().toJson(shoppingCart.getShoppingList()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SettleResult>() {
                    @Override
                    public void call(SettleResult settleResult) {
                        OrderUi ui = findUi(callingId);
                        if (ui instanceof OrderSettleUi) {
                            ((OrderSettleUi) ui).onSettleFinish(settleResult);
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        OrderUi ui = findUi(callingId);
                        if (ui instanceof OrderSettleUi) {
                            ((OrderSettleUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    /**
     * 执行订单提交
     * @param callingId
     * @param cartId
     * @param remark
     * @param bookedAt
     */
    private void doOrderCreate(final int callingId, String cartId, long bookedAt, String remark) {
        mRestApiClient.orderService()
                .submit(cartId, bookedAt, remark)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Order>() {
                    @Override
                    public void call(Order order) {
                        OrderUi ui = findUi(callingId);
                        if (ui instanceof OrderSettleUi) {
                            ((OrderSettleUi) ui).orderCreateFinish(order);
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        OrderUi ui = findUi(callingId);
                        if (ui instanceof OrderSettleUi) {
                            ((OrderSettleUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    @Override
    protected OrderUiCallbacks createUiCallbacks(final OrderUi ui) {
        return new OrderUiCallbacks() {

            @Override
            public void refresh() {
                if (ui instanceof OrderListUi) {
                    mPageIndex = 1;
                    fetchOrders(getId(ui), mPageIndex);
                } else if (ui instanceof OrderDetailUi) {
                    fetchOrderDetail(getId(ui), ((OrderDetailUi) ui).getRequestParameter());
                }
            }

            @Override
            public void nextPage() {
                if (ui instanceof OrderListUi) {
                    ++mPageIndex;
                    fetchOrders(getId(ui), mPageIndex);
                }
            }

            @Override
            public void showLogin() {
                Display display = getDisplay();
                if (display != null) {
                    display.startLoginActivity();
                }
            }

            @Override
            public void chooseAddress() {
                Display display = getDisplay();
                if (display != null) {
                    display.startChooseAddressActivity();
                }
            }

            @Override
            public void resettle() {
                // 重新结算需要隐藏弹出式loading对话框,支付方式默认为在线支付
                doOrderSettle(getId(ui), true);
            }

            @Override
            public void orderCreate(String cartId, long bookedAt, String remark) {
               doOrderCreate(getId(ui), cartId, bookedAt, remark);
            }

            @Override
            public void togglePayMethod(boolean isOnlinePayment) {
                // 切换付款方式需要显示弹出式loading对话框
                doOrderSettle(getId(ui), isOnlinePayment);
            }

            @Override
            public void showPayment(String orderId) {
                Display display = getDisplay();
                if (display != null) {
                    display.showPayment(orderId);
                }
            }

            @Override
            public void showOrderDetail(String orderId) {
                Display display = getDisplay();
                if (display != null) {
                    display.startOrderDetailActivity(orderId);
                }
            }

            @Override
            public void showBusiness(Business business) {
                Preconditions.checkNotNull(business, "business cannot be null");
                Display display = getDisplay();
                if (display != null) {
                    display.startBusinessActivity(business);
                }
            }

            @Override
            public void payment() {
                // 模拟在线付款
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ui instanceof OrderPaymentUi) {
                            OrderPaymentUi orderPaymentUi = (OrderPaymentUi) ui;
                            orderPaymentUi.finishPayment(orderPaymentUi.getRequestParameter());
                        }
                    }
                }, 3000);
            }
        };
    }

    public interface OrderUiCallbacks {
        void refresh();

        void nextPage();

        void showLogin();

        void resettle();

        void chooseAddress();

        void orderCreate(String cartId, long bookedAt, String remark);

        void togglePayMethod(boolean isOnlinePayment);

        void showPayment(String orderId);

        void showOrderDetail(String orderId);

        void showBusiness(Business business);

        void payment();
    }

    public interface OrderUi extends BaseUiController.Ui<OrderUiCallbacks> {}

    public interface OrderListUi extends OrderUi, BaseUiController.ListUi<Order> {
    }

    public interface OrderDetailUi extends OrderUi {
        void setOrderInfo(Order order);

        void onNetworkError(ResponseError error);

        String getRequestParameter();
    }

    public interface OrderSettleUi extends OrderUi {
        void onNetworkError(ResponseError error);

        void onSettleFinish(SettleResult result);

        void orderCreateFinish(Order order);
    }

    public interface OrderPaymentUi extends OrderUi {
        void onNetworkError(ResponseError error);

        String getRequestParameter();

        void finishPayment(String orderId);
    }
}
