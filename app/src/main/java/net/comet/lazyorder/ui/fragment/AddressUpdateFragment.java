package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Address;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.AddressController;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.Display;
import butterknife.Bind;
import butterknife.OnClick;

public class AddressUpdateFragment extends BaseFragment<AddressController.AddressUiCallbacks>
        implements AddressController.UpdateAddressUi {

    @Bind(R.id.et_input_name)
    EditText mEtName;

    @Bind(R.id.et_input_phone)
    EditText mEtPhone;

    @Bind(R.id.txt_poi_address)
    TextView mPoiAddressTxt;

    @Bind(R.id.et_detail_address)
    EditText mDetailAddressEt;

    @Bind(R.id.btn_submit)
    Button mBtnModifyOrCreate;


    public static AddressUpdateFragment create(Address address) {
        AddressUpdateFragment fragment = new AddressUpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Display.PARAM_OBJ, address);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getAddressController();
    }

    @Override
    public Address getRequestParameter() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getParcelable(Display.PARAM_OBJ);
        }

        return null;
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_address_update;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        if (!isNewCreate()) {
            Address address = getRequestParameter();
            mEtName.setText(address.getName());
            mEtPhone.setText(address.getPhone());
            mPoiAddressTxt.setText(address.getSummary());
            mDetailAddressEt.setText(address.getDetail());
            mBtnModifyOrCreate.setText(R.string.btn_confirm_update);
        } else {
            mBtnModifyOrCreate.setText(R.string.btn_confirm_create);
        }
    }

    public boolean isNewCreate() {
        return getRequestParameter() == null;
    }

    @Override
    public void onNetworkError(ResponseError error) {
        cancelLoading();
        showSnackbar(error.getMessage());
    }

    @Override
    public void updateFinish() {
        cancelLoading();
        showSnackbar(isNewCreate() ? R.string.toast_success_address_create : R.string.toast_success_address_update);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Display display = getDisplay();
                if (display != null) {
                    display.popTopFragmentBackStack();
                }
            }
        }, 1500);
    }

    @OnClick({R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                doCreateOrChange();
                break;
        }
    }

    private void doCreateOrChange() {
        // 验证名字是否为空
        final String name = mEtName.getText().toString().trim();
        if (!getCallbacks().isNameValid(name)) {
            showSnackbar(R.string.toast_error_empty_name);
            return;
        }
        // 验证电话是否为空
        final String phone = mEtPhone.getText().toString().trim();
        if (!getCallbacks().isMobileValid(phone)) {
            showSnackbar(R.string.toast_error_empty_phone);
            return;
        }
        // 验证小区/学校/大楼是否为空
        final String summary = mPoiAddressTxt.getText().toString().trim();
        if (!getCallbacks().isSummaryValid(summary)) {
            showSnackbar(R.string.toast_error_empty_address_summary);
            return;
        }
        // 验证详细地址是否为空
        final String detail = mDetailAddressEt.getText().toString().trim();
        if (!getCallbacks().isDetailValid(detail)) {
            showSnackbar(R.string.toast_error_empty_address_detail);
            return;
        }

        // 开始进行新增或者修改操作
        Address address = new Address();
        address.setName(name);
        address.setPhone(phone);
        address.setSummary(summary);
        address.setDetail(detail);
        if (hasCallbacks()) {
            showLoading(R.string.label_being_something);
            if (isNewCreate()) {
                getCallbacks().create(address);
            } else {
                getCallbacks().change(address);
            }
        }
    }
}
