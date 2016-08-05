package net.comet.lazyorder.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.CartInfo;
import net.comet.lazyorder.ui.adapter.DiscountInfoItemView;
import net.comet.lazyorder.ui.adapter.ExtraFeeItemView;
import net.comet.lazyorder.ui.adapter.ShoppingProductItemView;
import net.comet.lazyorder.ui.fragment.BusinessNameClickHandle;
import net.comet.lazyorder.util.CollectionUtil;
import net.comet.lazyorder.util.StringFetcher;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;

public class OrderReportView extends FrameLayout {

    @Bind(R.id.txt_name)
    TextView mBusinessNameTxt;

    @Bind(R.id.img_arrow)
    ImageView mArrowImg;

    @Bind(R.id.recycler_view)
    RecyclerView mShoppingProductRecyclerView;

    @Bind(R.id.divider)
    View mExtraFeeDivider;

    @Bind(R.id.recycler_view2)
    RecyclerView mExtraFeeRecyclerView;

    @Bind(R.id.divider2)
    View mDiscountListDivider;

    @Bind(R.id.recycler_view3)
    RecyclerView mDiscountInfoRecyclerView;

    @Bind(R.id.txt_origin_price)
    TextView mOriginPriceTxt;

    @Bind(R.id.txt_discount_price)
    TextView mDiscountPriceTxt;

    @Bind(R.id.txt_total_price)
    TextView mTotalPriceTxt;

    private RecyclerMultiAdapter mShoppingProductAdapter;
    private RecyclerMultiAdapter mExtraFeeAdapter;
    private RecyclerMultiAdapter mDiscountInfoAdapter;

    private BusinessNameClickHandle mNameClickHandle;

    public OrderReportView(Context context) {
        this(context, null);
    }

    public OrderReportView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_order_report_view, this);
        ButterKnife.bind(view);
        initViews();
    }

    private void initViews() {
        mShoppingProductRecyclerView.setLayoutManager(new FixedLinearLayoutManager(getContext()));
        mShoppingProductAdapter = SmartAdapter.empty()
                .map(CartInfo.ShoppingProduct.class, ShoppingProductItemView.class)
                .into(mShoppingProductRecyclerView);

        mExtraFeeRecyclerView.setLayoutManager(new FixedLinearLayoutManager(getContext()));
        mExtraFeeAdapter = SmartAdapter.empty()
                .map(CartInfo.ExtraFee.class, ExtraFeeItemView.class)
                .into(mExtraFeeRecyclerView);

        mDiscountInfoRecyclerView.setLayoutManager(new FixedLinearLayoutManager(getContext()));
        mDiscountInfoAdapter = SmartAdapter.empty()
                .map(CartInfo.DiscountInfo.class, DiscountInfoItemView.class)
                .into(mDiscountInfoRecyclerView);
    }

    public void setupContent(CartInfo cartInfo) {
        // 价格
        mOriginPriceTxt.setText(StringFetcher.getString(R.string.label_price, cartInfo.getOriginPrice()));
        mDiscountPriceTxt.setText(StringFetcher.getString(R.string.label_price, cartInfo.getDiscountPrice()));
        mTotalPriceTxt.setText(StringFetcher.getString(R.string.label_price, cartInfo.getTotalPrice()));
        // 商家名称
        final Business businessInfo = cartInfo.getBusiness();
        if (businessInfo != null) {
            mBusinessNameTxt.setText(businessInfo.getName());
            mBusinessNameTxt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNameClickHandle != null) {
                        mNameClickHandle.onBusinessNameClick(businessInfo);
                    }
                }
            });
        }
        // 选购的商品
        mShoppingProductAdapter.setItems(cartInfo.getShoppingProducts());
        // 商家额外费用
        List<CartInfo.ExtraFee> extraFees = cartInfo.getExtraFees();
        mExtraFeeAdapter.setItems(extraFees);
        mExtraFeeDivider.setVisibility(CollectionUtil.isEmpty(extraFees) ? View.GONE : View.VISIBLE);
        // 商家活动优惠
        List<CartInfo.DiscountInfo> discountInfos = cartInfo.getDiscountInfos();
        mDiscountInfoAdapter.setItems(discountInfos);
        mDiscountListDivider.setVisibility(CollectionUtil.isEmpty(discountInfos) ? View.GONE : View.VISIBLE);
    }

    public void setNameClickHandle(BusinessNameClickHandle nameClickHandle) {
        if (nameClickHandle != null) {
            mNameClickHandle = nameClickHandle;
            mArrowImg.setVisibility(View.VISIBLE);
        }
    }
}
