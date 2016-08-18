package net.comet.lazyorder.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Address;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.AddressController;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.adapter.AddressItemView;
import net.comet.lazyorder.util.CollectionUtil;
import net.comet.lazyorder.util.ToastUtil;
import net.comet.lazyorder.widget.MultiStateView;
import net.comet.lazyorder.widget.refresh.OnRefreshListener;
import net.comet.lazyorder.widget.refresh.RefreshLayout;

import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;
import static net.comet.lazyorder.util.Constants.ClickType.CLICK_TYPE_ADDRESS_CLICKED;
import static net.comet.lazyorder.util.Constants.ClickType.CLICK_TYPE_DELETE_BTN_CLICKED;
import static net.comet.lazyorder.util.Constants.ClickType.CLICK_TYPE_EDIT_BTN_CLICKED;

public class AddressesFragment extends BaseFragment<AddressController.AddressUiCallbacks>
        implements AddressController.AddressListUi, ViewEventListener<Address> {

    private static final String ARG_CHOOSE_ADDRESS = "arg_choose_address";

    @Bind(R.id.multi_state_view)
    MultiStateView mMultiStateView;

    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.btn_create_address)
    LinearLayout mCreateAddressView;

    private RecyclerMultiAdapter mAdapter;


    public static AddressesFragment choose() {
        AddressesFragment fragment = new AddressesFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_CHOOSE_ADDRESS, true);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_address_list;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getAddressController();
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = SmartAdapter.empty()
                .map(Address.class, AddressItemView.class)
                .listener(this)
                .into(mRecyclerView);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (hasCallbacks()) {
                    getCallbacks().refresh();
                }
            }
        });

        mMultiStateView.setState(MultiStateView.STATE_LOADING);
    }

    /**
     * 是否是地址选择模式
     */
    public boolean isChooseAddress() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return getArguments().getBoolean(ARG_CHOOSE_ADDRESS, false);
        }

        return false;
    }

    @OnClick({R.id.btn_create_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_address:
                if (hasCallbacks()) {
                    getCallbacks().showCreateAddress();
                }
                break;
        }
    }

    @Override
    public void onNetworkError(ResponseError error) {
        cancelLoading();
        mRefreshLayout.finishRefresh();

        if (!isPopulated()) {
            mMultiStateView.setState(MultiStateView.STATE_ERROR)
                    .setTitle(error.getMessage())
                    .setButton(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMultiStateView.setState(MultiStateView.STATE_LOADING);
                            getCallbacks().refresh();
                        }
                    });
        } else {
            ToastUtil.showToast(error.getMessage());
        }
    }

    @Override
    public void onChangeItem(final List<Address> items) {
        setIsPopulated(true);
        mRefreshLayout.finishRefresh();
        if (!CollectionUtil.isEmpty(items)) {
            mAdapter.setItems(items);
            mMultiStateView.setState(MultiStateView.STATE_CONTENT);
        } else {
            mMultiStateView.setState(MultiStateView.STATE_EMPTY)
                    .setTitle(R.string.label_empty_address_list)
                    .setButton(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMultiStateView.setState(MultiStateView.STATE_LOADING);
                            getCallbacks().refresh();
                        }
                    });
        }
    }

    @Override
    public void deleteFinish(Address address) {
        cancelLoading();
        showSnackbar(R.string.toast_success_address_delete);
        mAdapter.delItem(address);
    }

    @Override
    public void setDefaultFinish() {
        cancelLoading();
        Display display = getDisplay();
        if (display != null) {
            display.finishActivity();
        }
    }

    @Override
    public void onViewEvent(int actionId, Address address, int position, View view) {
        switch (actionId) {
            case CLICK_TYPE_ADDRESS_CLICKED: // 设置默认地址
                if (isChooseAddress() && hasCallbacks()) {
                    showLoading(R.string.label_being_something);
                    getCallbacks().setDefaultAddress(address);
                }
                break;
            case CLICK_TYPE_EDIT_BTN_CLICKED: // 修改
                if (hasCallbacks()) {
                    getCallbacks().showChangeAddress(address);
                }
                break;
            case CLICK_TYPE_DELETE_BTN_CLICKED: // 删除
                deleteAddress(address);
                break;
        }
    }

    private void deleteAddress(final Address address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_delete_title);
        builder.setMessage(R.string.dialog_delete_message);
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (hasCallbacks()) {
                    showLoading(R.string.label_being_something);
                    getCallbacks().delete(address);
                }
            }
        });
        builder.create().show();
    }
}
