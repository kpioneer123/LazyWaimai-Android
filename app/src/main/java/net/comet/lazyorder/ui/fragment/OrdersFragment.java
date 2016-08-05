package net.comet.lazyorder.ui.fragment;

import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.Order;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.OrderController;
import net.comet.lazyorder.ui.adapter.OrderItemView;
import io.nlopez.smartadapters.utils.ViewEventListener;
import static net.comet.lazyorder.util.Constants.ClickType.CLICK_TYPE_BUSINESS_CLICKED;
import static net.comet.lazyorder.util.Constants.ClickType.CLICK_TYPE_ORDER_CLICKED;

public class OrdersFragment extends BaseListFragment<Order, OrderItemView, OrderController.OrderUiCallbacks>
        implements OrderController.OrderListUi, ViewEventListener<Order> {

    @Override
    protected void refresh() {
        if (hasCallbacks()) {
            getCallbacks().refresh();
        }
    }

    @Override
    protected void nextPage() {
        if (hasCallbacks()) {
            getCallbacks().nextPage();
        }
    }

    @Override
    protected void unauth() {
        if (hasCallbacks()) {
            getCallbacks().showLogin();
        }
    }

    @Override
    public boolean isModal() {
        return true;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getOrderController();
    }

    @Override
    protected void onItemClick(int actionId, Order order, int position) {
        switch (actionId) {
            case CLICK_TYPE_ORDER_CLICKED:
                if (hasCallbacks()) {
                    getCallbacks().showOrderDetail(order.getId());
                }
                break;
            case CLICK_TYPE_BUSINESS_CLICKED:
                Business business = order.getBusinessInfo();
                if (business != null && hasCallbacks()) {
                    getCallbacks().showBusiness(business);
                }
                break;
        }
    }
}