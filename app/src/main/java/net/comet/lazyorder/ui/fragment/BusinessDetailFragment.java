package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.BusinessController;
import net.comet.lazyorder.ui.Display;

public class BusinessDetailFragment extends BaseFragment<BusinessController.BusinessUiCallbacks>
        implements BusinessController.BusinessProfileUi {

    public static BusinessDetailFragment create(Business business) {
        BusinessDetailFragment fragment = new BusinessDetailFragment();
        if (business != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Display.PARAM_OBJ, business);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_business_detail;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getBusinessController();
    }

    @Override
    public void onNetworkError(ResponseError error) {

    }

    @Override
    public Business getRequestParameter() {
        return getArguments().getParcelable(Display.PARAM_OBJ);
    }
}