package net.comet.lazyorder.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.util.Constants;
import net.comet.lazyorder.model.ShoppingCart;
import net.comet.lazyorder.util.StringFetcher;
import butterknife.Bind;
import cn.bingoogolapple.badgeview.BGABadgeFrameLayout;

public class BusinessItemView extends BaseAdapterItemView<Business> {

    @Bind(R.id.view_content)
    View mContentView;

    @Bind(R.id.badge_view)
    BGABadgeFrameLayout mBadgeView;

    @Bind(R.id.img_photo)
    ImageView mPhotoImg;

    @Bind(R.id.txt_name)
    TextView mNameTxt;

    @Bind(R.id.txt_month_sales)
    TextView mMonthSalesTxt;

    @Bind(R.id.txt_content)
    TextView mMultiContentTxt;

    public BusinessItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_business_item;
    }

    @Override
    public void bind(Business business) {
        if (!TextUtils.isEmpty(business.getPicUrl())) {
            Picasso.with(getContext())
                    .load(business.getPicUrl())
                    .into(mPhotoImg);
        }
        mNameTxt.setText(item.getName());
        mMonthSalesTxt.setText(StringFetcher.getString(R.string.label_month_sales,
                item.getMonthSales()));
        mMultiContentTxt.setText(StringFetcher.getString(R.string.label_business_multi_content,
                String.valueOf(item.getMinPrice()),
                String.valueOf(item.getShippingFee()),
                String.valueOf(item.getShippingTime())));
        ShoppingCart shoppingCart = ShoppingCart.getInstance();
        int count = shoppingCart.getTotalQuantity();
        if (item.getId().equals(shoppingCart.getBusinessId())
                && count > 0) {
            mBadgeView.showTextBadge(String.valueOf(count));
        } else {
            mBadgeView.hiddenBadge();
        }
        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemAction(Constants.ClickType.CLICK_TYPE_BUSINESS_CLICKED);
            }
        });
    }
}