package net.comet.lazyorder.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.Address;
import net.comet.lazyorder.util.Constants;
import butterknife.Bind;

public class AddressItemView extends BaseAdapterItemView<Address> {

    @Bind(R.id.view_content)
    View mContentView;

    @Bind(R.id.txt_name)
    TextView mNameTxt;

    @Bind(R.id.txt_gender)
    TextView mGenderTxt;

    @Bind(R.id.txt_mobile)
    TextView mMobileTxt;

    @Bind(R.id.txt_address)
    TextView mAddressTxt;

    @Bind(R.id.btn_delete)
    ImageButton mDeleteBtn;

    @Bind(R.id.btn_edit)
    ImageButton mEditBtn;

    public AddressItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_address_item;
    }

    @Override
    public void bind(Address address) {
        mNameTxt.setText(address.getName());
//        mGenderTxt.setText(address.getGender().getValue());
        mMobileTxt.setText(address.getPhone());
        mAddressTxt.setText(address.getSummary() + address.getDetail());
        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemAction(Constants.ClickType.CLICK_TYPE_DELETE_BTN_CLICKED);
            }
        });
        mEditBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemAction(Constants.ClickType.CLICK_TYPE_EDIT_BTN_CLICKED);
            }
        });
        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemAction(Constants.ClickType.CLICK_TYPE_ADDRESS_CLICKED);
            }
        });
    }
}