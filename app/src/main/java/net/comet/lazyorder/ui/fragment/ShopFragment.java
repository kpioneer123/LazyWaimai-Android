package net.comet.lazyorder.ui.fragment;

import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.BusinessController;
import net.comet.lazyorder.ui.adapter.BusinessItemView;
import net.comet.lazyorder.ui.BusinessController.BusinessUiCallbacks;
import static net.comet.lazyorder.util.Constants.ClickType.CLICK_TYPE_BUSINESS_CLICKED;

public class ShopFragment extends BaseListFragment<Business, BusinessItemView, BusinessUiCallbacks>
        implements BusinessController.BusinessListUi<Business>  {

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
    public boolean isModal() {
        return true;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getBusinessController();
    }

    @Override
    public void onItemClick(int actionId, Business business, int position) {
        switch (actionId) {
            case CLICK_TYPE_BUSINESS_CLICKED:
                if (hasCallbacks()) {
                    getCallbacks().showBusiness(business);
                }
                break;
        }
    }

    @Override
    public BusinessController.QueryType getQueryType() {
        return BusinessController.QueryType.RESTAURANTS;
    }

    @Override
    public Business getRequestParameter() {
        return null;
    }
}
