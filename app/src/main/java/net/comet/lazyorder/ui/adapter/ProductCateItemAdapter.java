package net.comet.lazyorder.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.ProductCategory;
import net.comet.lazyorder.model.ShoppingCart;
import cn.bingoogolapple.badgeview.BGABadgeFrameLayout;

public class ProductCateItemAdapter extends BaseListAdapter<ProductCategory> {

    private static final String LOG_TAG = ProductCateItemAdapter.class.getSimpleName();

    public ProductCateItemAdapter(Activity activity) {
        super(activity, R.layout.layout_product_category_item);
    }

    @Override
    protected void bindView(int position, View view, ProductCategory category) {
        final TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        txtName.setText(category.getName());

        final BGABadgeFrameLayout badgeView = (BGABadgeFrameLayout) view.findViewById(R.id.badge_view);
        int count = ShoppingCart.getInstance().getQuantityForCategory(category);
        if (count > 0) {
            badgeView.showTextBadge(String.valueOf(count));
        } else {
            badgeView.hiddenBadge();
        }
    }
}
