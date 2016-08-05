package net.comet.lazyorder.ui.adapter;

import android.content.Context;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.CartInfo;
import net.comet.lazyorder.util.StringFetcher;
import butterknife.Bind;

public class ShoppingProductItemView extends BaseAdapterItemView<CartInfo.ShoppingProduct> {

    @Bind(R.id.txt_name)
    TextView nameTxt;

    @Bind(R.id.txt_quantity)
    TextView quantityTxt;

    @Bind(R.id.txt_price)
    TextView priceTxt;

    public ShoppingProductItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_shopping_product_item;
    }

    @Override
    public void bind(CartInfo.ShoppingProduct shoppingProduct) {
        nameTxt.setText(item.getName());
        quantityTxt.setText(StringFetcher.getString(R.string.label_quantity,
                item.getQuantity()));
        priceTxt.setText(StringFetcher.getString(R.string.label_price,
                item.getTotalPrice()));
    }
}

