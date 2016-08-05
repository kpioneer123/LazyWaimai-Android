package net.comet.lazyorder.ui;

import com.google.common.base.Preconditions;
import com.squareup.otto.Subscribe;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppCookie;
import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.ProductCategory;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.bean.ResultsPage;
import net.comet.lazyorder.model.event.ShoppingCartChangeEvent;
import net.comet.lazyorder.network.RestApiClient;
import net.comet.lazyorder.network.action.ErrorAction;
import net.comet.lazyorder.util.EventUtil;
import net.comet.lazyorder.model.ShoppingCart;
import net.comet.lazyorder.util.StringFetcher;
import java.util.List;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BusinessController extends BaseUiController<BusinessController.BusinessUi,
        BusinessController.BusinessUiCallbacks> {

    private static final String LOG_TAG = BusinessController.class.getSimpleName();

    private final RestApiClient mRestApiClient;

    private int mPageIndex;

    @Inject
    public BusinessController(RestApiClient restApiClient) {
        super();
        mRestApiClient = Preconditions.checkNotNull(restApiClient, "restApiClient cannot be null");
    }

    @Subscribe
    public void onShoppingCartChanged(ShoppingCartChangeEvent event) {
        for (BusinessUi ui : getUis()) {
            if (ui instanceof ProductListUi) {
                ((ProductListUi) ui).onShoppingCartChange();
                break;
            }
        }
    }

    @Override
    protected void onInited() {
        super.onInited();
        EventUtil.register(this);
    }

    @Override
    protected void onSuspended() {
        EventUtil.unregister(this);
        super.onSuspended();
    }

    @Override
    protected String getUiTitle(BusinessUi ui) {
        if (ui instanceof BusinessListUi) {
            return StringFetcher.getString(R.string.title_shop);
        } else if (ui instanceof BusinessTabUi) {
            final Business business = ui.getRequestParameter();
            if (business != null) {
                return business.getName();
            }
        }
        return null;
    }

    @Override
    protected void populateUi(BusinessUi ui) {
        if (ui instanceof BusinessListUi) {
            populateBusinessListUi((BusinessListUi) ui);
        } else if (ui instanceof BusinessTabUi) {
            populateBusinessTabUi((BusinessTabUi) ui);
        } else if (ui instanceof ProductListUi) {
            populateProductListUi((ProductListUi) ui);
        } else if (ui instanceof CommentListUi) {
            populateCommentListUi((CommentListUi) ui);
        } else if (ui instanceof BusinessProfileUi) {
            populateBusinessProfileUi((BusinessProfileUi) ui);
        }
    }

    private void populateBusinessListUi(BusinessListUi ui) {
        final QueryType queryType = ui.getQueryType();
        if (queryType == null) {
            return;
        }
        switch (queryType) {
            case RESTAURANTS:
                fetchRestaurants(getId(ui));
                break;
            case STORES:
                fetchStores(getId(ui));
                break;
            case DRINKS:
                fetchDrinks(getId(ui));
                break;
        }
    }

    private void populateBusinessTabUi(BusinessTabUi ui) {
        ui.setTabs(BusinessTab.PRODUCT, BusinessTab.COMMENT, BusinessTab.DETAIL);
    }

    private void populateProductListUi(ProductListUi ui) {
        fetchProducts(getId(ui), ui.getRequestParameter());
    }

    private void populateCommentListUi(CommentListUi ui) {

    }

    private void populateBusinessProfileUi(BusinessProfileUi ui) {

    }

    /**
     * 获取商家列表数据
     * @param callingId
     */
    private void fetchRestaurants(final int callingId) {
        mPageIndex = 1;
        fetchRestaurants(callingId, mPageIndex);
    }

    /**
     * 分页获取商家列表数据
     * @param callingId
     * @param page
     */
    private void fetchRestaurants(final int callingId, final int page) {
        mRestApiClient.businessService()
                .restaurants(page)
                .map(new Func1<ResultsPage<Business>, List<Business>>() {
                    @Override
                    public List<Business> call(ResultsPage<Business> results) {
                        return results.results;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Business>>() {
                    @Override
                    public void call(List<Business> businesses) {
                        BusinessUi ui = findUi(callingId);
                        if (ui instanceof BusinessListUi) {
                            ((BusinessListUi) ui).onChangeItem(businesses, mPageIndex);
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        BusinessUi ui = findUi(callingId);
                        if (ui instanceof BusinessListUi) {
                            ((BusinessListUi) ui).onNetworkError(error, mPageIndex);
                        }
                    }
                });
    }

    private void fetchDrinks(final int callingId) {

    }

    private void fetchDrinks(final int callingId, final int page) {

    }

    private void fetchStores(final int callingId) {

    }

    private void fetchStores(final int callingId, final int page) {

    }

    /**
     * 获取指定商家下的所有商品数据
     * @param callingId
     * @param business
     */
    private void fetchProducts(final int callingId, Business business) {
        mRestApiClient.businessService()
                .products(business.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ProductCategory>>() {
                    @Override
                    public void call(List<ProductCategory> businesses) {
                        BusinessUi ui = findUi(callingId);
                        if (ui instanceof ProductListUi) {
                            ((ProductListUi) ui).onChangeItem(businesses);
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        BusinessUi ui = findUi(callingId);
                        if (ui instanceof ProductListUi) {
                            ((ProductListUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    @Override
    protected BusinessUiCallbacks createUiCallbacks(final BusinessUi ui) {
        return new BusinessUiCallbacks() {

            @Override
            public void refresh() {
                if (ui instanceof BusinessListUi) {
                    QueryType queryType = ((BusinessListUi) ui).getQueryType();
                    if (queryType == QueryType.RESTAURANTS) {
                        mPageIndex = 1;
                        fetchRestaurants(getId(ui), mPageIndex);
                    }
                } else if (ui instanceof ProductListUi) {
                    fetchProducts(getId(ui), ui.getRequestParameter());
                }
            }

            @Override
            public void nextPage() {
                if (ui instanceof BusinessListUi) {
                    QueryType queryType = ((BusinessListUi) ui).getQueryType();
                    if (queryType == QueryType.RESTAURANTS) {
                        ++mPageIndex;
                        fetchRestaurants(getId(ui), mPageIndex);
                    }
                }
            }

            @Override
            public boolean enableSettle() {
                ShoppingCart shoppingCart = ShoppingCart.getInstance();
                Business business = ui.getRequestParameter();
                return business != null
                        && (shoppingCart.getTotalPrice() > business.getMinPrice())
                        && (shoppingCart.getTotalQuantity() > 0);
            }

            @Override
            public void callBusinessPhone() {
                Business business = ui.getRequestParameter();
                Display display = getDisplay();
                if (business != null && display != null) {
                    display.callPhone(business.getPhone());
                }
            }

            @Override
            public void showBusiness(Business business) {
                Preconditions.checkNotNull(business, "business cannot be null");

                Display display = getDisplay();
                if (display != null) {
                    display.startBusinessActivity(business);
                }
            }

            @Override
            public void showSettle() {
                Display display = getDisplay();
                if (display != null) {
                    if (AppCookie.isLoggin()) {
                        display.startSettingActivity();
                    } else {
                        display.startLoginActivity();
                    }
                }
            }
        };
    }

    public enum BusinessTab {
        PRODUCT, COMMENT, DETAIL
    }

    public enum QueryType {
        RESTAURANTS,
        STORES,
        DRINKS,
    }

    public interface BusinessUiCallbacks {
        void refresh();

        void nextPage();

        boolean enableSettle();

        void callBusinessPhone();

        void showSettle();

        void showBusiness(Business business);
    }

    public interface BusinessUi extends BaseUiController.Ui<BusinessUiCallbacks> {
        Business getRequestParameter();
    }

    public interface BusinessTabUi extends BusinessUi {
        void setTabs(BusinessTab... tabs);
    }

    public interface BusinessListUi<E> extends BusinessUi, BaseUiController.ListUi<E> {
        QueryType getQueryType();
    }

    public interface ProductListUi extends BaseUiController.SubUi, BusinessUi {
        void onChangeItem(List<ProductCategory> items);

        void onNetworkError(ResponseError error);

        void onShoppingCartChange();
    }

    public interface CommentListUi extends BaseUiController.SubUi, BusinessUi {
        void onNetworkError(ResponseError error);
    }

    public interface BusinessProfileUi extends BaseUiController.SubUi, BusinessUi {
        void onNetworkError(ResponseError error);
    }
}
