package net.comet.lazyorder.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.Order;
import net.comet.lazyorder.ui.fragment.BusinessNameClickHandle;
import net.comet.lazyorder.util.Constants;
import net.comet.lazyorder.util.DateUtil;
import net.comet.lazyorder.util.StringFetcher;
import net.comet.lazyorder.widget.PicassoImageView;
import java.util.Date;
import butterknife.Bind;

public class OrderItemView extends BaseAdapterItemView<Order> implements View.OnClickListener {

    @Bind(R.id.view_content)
    View mContentView;

    @Bind(R.id.img_business_photo)
    PicassoImageView businessPhotoImg;

    @Bind(R.id.txt_business_name)
    TextView businessNameTxt;

    @Bind(R.id.txt_total_price)
    TextView totalPriceTxt;

    @Bind(R.id.txt_created_at)
    TextView createAtTxt;

    @Bind(R.id.txt_order_status)
    TextView orderStatusTxt;

    private BusinessNameClickHandle mNameClickHandle;

    public OrderItemView(Context context) {
        super(context);
    }

    public void setBusinessNameClickHandle(BusinessNameClickHandle nameClickHandle) {
        mNameClickHandle = nameClickHandle;
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_order_list_item;
    }

    @Override
    public void bind(Order order) {
        Business business = order.getBusinessInfo();
        if (business != null) {
            // 商家图片
            businessPhotoImg.loadBusinessPhoto(business);
            // 商家名称
            businessNameTxt.setTag(business);
            businessNameTxt.setText(business.getName());
            businessNameTxt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemAction(Constants.ClickType.CLICK_TYPE_BUSINESS_CLICKED);
                }
            });
        }
        // 订单金额
        totalPriceTxt.setText(StringFetcher.getString(R.string.label_price,
                item.getTotalPrice()));
        // 下单时间
        createAtTxt.setText(DateUtil.DateToString(new Date(item.getCreatedAt()),
                DateUtil.DateStyle.YYYY_MM_DD_HH_MM
        ));
        // 订单状态
        orderStatusTxt.setText(item.getStatus().toString());
        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemAction(Constants.ClickType.CLICK_TYPE_ORDER_CLICKED);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Business business = (Business) v.getTag();
        if (mNameClickHandle != null) {
            mNameClickHandle.onBusinessNameClick(business);
        }
    }
}