package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.common.base.Preconditions;
import com.orhanobut.logger.Logger;

import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.BusinessController;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.util.StringFetcher;
import java.util.ArrayList;

public class BusinessTabFragment extends BaseTabFragment<BusinessController.BusinessUiCallbacks>
        implements BusinessController.BusinessTabUi {

    private BusinessController.BusinessTab[] mTabs;

    public static BusinessTabFragment create(Business business) {
        BusinessTabFragment fragment = new BusinessTabFragment();
        if (business != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Display.PARAM_OBJ, business);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_business, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_call:
                if (hasCallbacks()) {
                    getCallbacks().callBusinessPhone();
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getBusinessController();
    }

    @Override
    public void setTabs(BusinessController.BusinessTab... tabs) {
        Preconditions.checkNotNull(tabs, "tabs cannot be null");
        mTabs = tabs;

        if (getAdapter().getCount() != tabs.length) {
            ArrayList<Fragment> fragments = new ArrayList<>();
            for (BusinessController.BusinessTab tab : tabs) {
                fragments.add(createFragmentForTab(tab));
            }
            setFragments(fragments);
        }
    }

    @Override
    protected String getTabTitle(int position) {
        if (mTabs != null) {
            return StringFetcher.getString(mTabs[position]);
        }
        return null;
    }

    private Fragment createFragmentForTab(BusinessController.BusinessTab tab) {
        switch (tab) {
            case PRODUCT:
                return ProductFragment.create(getRequestParameter());
            case COMMENT:
                return CommentFragment.create(getRequestParameter());
            case DETAIL:
                return BusinessDetailFragment.create(getRequestParameter());
        }
        return null;
    }

    @Override
    public Business getRequestParameter() {
        return getArguments().getParcelable(Display.PARAM_OBJ);
    }
}
