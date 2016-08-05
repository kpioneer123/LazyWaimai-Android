package net.comet.lazyorder.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.CartInfo;
import net.comet.lazyorder.util.StringFetcher;
import butterknife.Bind;

public class DiscountInfoItemView extends BaseAdapterItemView<CartInfo.DiscountInfo> {

    @Bind(R.id.txt_icon)
    TextView iconTxt;

    @Bind(R.id.txt_name)
    TextView nameTxt;

    @Bind(R.id.txt_price)
    TextView priceTxt;

    @Bind(R.id.txt_desc)
    TextView descTxt;

    public DiscountInfoItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_discount_info_item;
    }

    @Override
    public void bind(CartInfo.DiscountInfo discountInfo) {
        iconTxt.setText(item.getIconName());
        iconTxt.setBackgroundColor(Color.parseColor(item.getIconColor()));
        nameTxt.setText(item.getName());
        priceTxt.setText(StringFetcher.getString(R.string.label_discount_price, item.getPrice()));
        if (!TextUtils.isEmpty(item.getDescription())) {
            descTxt.setText(item.getDescription());
            descTxt.setVisibility(View.VISIBLE);
        } else {
            descTxt.setVisibility(View.GONE);
        }
    }
}