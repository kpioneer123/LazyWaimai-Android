package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.BusinessController;
import net.comet.lazyorder.ui.Display;

import java.util.List;

/**
 * Created by comet on 15/11/1.
 */
public class CommentFragment extends BaseFragment<BusinessController.BusinessUiCallbacks>
        implements BusinessController.CommentListUi {

    public static CommentFragment create(Business business) {
        CommentFragment fragment = new CommentFragment();
        if (business != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Display.PARAM_OBJ, business);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_comment;
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
