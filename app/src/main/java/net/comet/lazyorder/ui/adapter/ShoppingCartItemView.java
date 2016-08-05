package net.comet.lazyorder.ui.adapter;

import android.content.Context;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.ShoppingEntity;
import net.comet.lazyorder.util.StringFetcher;
import net.comet.lazyorder.widget.ShoppingCountView;
import butterknife.Bind;

public class ShoppingCartItemView extends BaseAdapterItemView<ShoppingEntity> {

    @Bind(R.id.txt_name)
    TextView mNameTxt;

    @Bind(R.id.txt_price)
    TextView mPriceTxt;

    @Bind(R.id.shopping_count_view)
    ShoppingCountView mShoppingCountView;

    public ShoppingCartItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_shopping_cart_item;
    }

    @Override
    public void bind(ShoppingEntity entity) {
        mNameTxt.setText(entity.getName());
        mPriceTxt.setText(StringFetcher.getString(R.string.label_price, entity.getTotalPrice()));
        mShoppingCountView.setProduct(entity.getProduct());
    }
}