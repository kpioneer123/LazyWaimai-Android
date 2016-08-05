package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.ProductCategory;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.BusinessController;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.adapter.ProductCateItemAdapter;
import net.comet.lazyorder.ui.adapter.ProductItemAdapter;
import net.comet.lazyorder.util.CollectionUtil;
import net.comet.lazyorder.model.ShoppingCart;
import net.comet.lazyorder.util.StringFetcher;
import net.comet.lazyorder.widget.MultiStateView;
import net.comet.lazyorder.widget.ShoppingCartPanel;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

public class ProductFragment extends BaseFragment<BusinessController.BusinessUiCallbacks>
        implements BusinessController.ProductListUi, BaseUiController.SubUi {

    @Bind(R.id.multi_state_view)
    MultiStateView mMultiStateView;

    @Bind(R.id.bottom_sheet_layout)
    BottomSheetLayout mBottomSheetLayout;

    @Bind(R.id.lv_product_category)
    ListView mCategoryListView;

    @Bind(R.id.lv_product_list)
    PinnedHeaderListView mProductListView;

    @Bind(R.id.shopping_info_layout)
    LinearLayout mShoppingInfoLayout;

    @Bind(R.id.img_cart_logo)
    ImageView mCartLogoImg;

    @Bind(R.id.txt_selected_count)
    TextView mSelectedCountTxt;

    @Bind(R.id.txt_total_price)
    TextView mTotalPriceTxt;

    @Bind(R.id.btn_settle)
    Button mSettleBtn;

    private ProductCateItemAdapter mCategoryItemAdapter;
    private ProductItemAdapter mProductItemAdapter;

    private ShoppingCartPanel mShoppingCartPanel;

    private boolean isClickTrigger;

    public static ProductFragment create(Business business) {
        ProductFragment fragment = new ProductFragment();
        if (business != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Display.PARAM_OBJ, business);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_product;
    }

    @Override
    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getBusinessController();
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        // 商品分类的列表
        mCategoryItemAdapter = new ProductCateItemAdapter(getActivity());
        mCategoryListView.setAdapter(mCategoryItemAdapter);
        mCategoryListView.setSelection(0);
        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int productPos = 0;
                for (int index = 0; index < position; index++) {
                    // 加1是因为section也算一个位置
                    productPos += mProductItemAdapter.getCountForSection(index) + 1;
                }
                isClickTrigger = true;
                mProductListView.setSelection(productPos);
            }
        });

        // 商品的列表
        mProductItemAdapter = new ProductItemAdapter(getActivity());
        mProductListView.setAdapter(mProductItemAdapter);
        mProductListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isClickTrigger) {
                    isClickTrigger = false;
                } else {
                    int section = mProductItemAdapter.getSectionForPosition(firstVisibleItem);
                    mCategoryListView.setItemChecked(section, true);
                }
            }
        });
        refreshBottom();

        mShoppingCartPanel = new ShoppingCartPanel(getContext());
        mMultiStateView.setState(MultiStateView.STATE_LOADING);
    }

    /**
     * 更新底部面板
     */
    private void refreshBottom() {
        ShoppingCart shoppingCart = ShoppingCart.getInstance();
        int totalCount = shoppingCart.getTotalQuantity();
        double totalPrice = shoppingCart.getTotalPrice();
        mCartLogoImg.setSelected(totalCount > 0);
        mSelectedCountTxt.setText(StringFetcher.getString(R.string.label_count, totalCount));
        mTotalPriceTxt.setText(StringFetcher.getString(R.string.label_price, totalPrice));
        // 结算按钮是否可点击
        if (hasCallbacks()) {
            mSettleBtn.setEnabled(getCallbacks().enableSettle());
        }
    }

    /**
     * 显示购物车面板
     */
    private void showShoppingCartPanel() {
        int count = ShoppingCart.getInstance().getTotalQuantity();
        if (count > 0 && !mBottomSheetLayout.isSheetShowing()) {
            mBottomSheetLayout.showWithSheetView(mShoppingCartPanel);
        } else {
            mBottomSheetLayout.dismissSheet();
        }
    }

    @Override
    public void onShoppingCartChange() {
        refreshBottom();
        mShoppingCartPanel.refreshPanel();
        mCategoryItemAdapter.notifyDataSetChanged();
        mProductItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNetworkError(ResponseError error) {
        mMultiStateView.setState(MultiStateView.STATE_ERROR)
                .setTitle(error.getMessage())
                .setButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasCallbacks()) {
                            getCallbacks().refresh();
                        }
                    }
                });
    }

    @Override
    public void onChangeItem(List<ProductCategory> items) {
        if (!CollectionUtil.isEmpty(items)) {
            mCategoryItemAdapter.setItems(items);
            mProductItemAdapter.setItems(items);
            mMultiStateView.setState(MultiStateView.STATE_CONTENT);
        } else {
            mMultiStateView.setState(MultiStateView.STATE_EMPTY)
                    .setTitle(R.string.label_empty_product_list)
                    .setButton(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (hasCallbacks()) {
                                getCallbacks().refresh();
                            }
                        }
                    });
        }
    }

    @OnClick({R.id.btn_settle, R.id.shopping_info_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_settle:
                if (hasCallbacks()) {
                    getCallbacks().showSettle();
                }
                break;
            case R.id.shopping_info_layout:
                showShoppingCartPanel();
                break;
        }
    }

    @Override
    public Business getRequestParameter() {
        return getArguments().getParcelable(Display.PARAM_OBJ);
    }
}