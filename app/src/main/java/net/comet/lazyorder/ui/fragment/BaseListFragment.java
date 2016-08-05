package net.comet.lazyorder.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppConfig;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.util.CollectionUtil;
import static net.comet.lazyorder.util.Constants.Code.*;
import net.comet.lazyorder.util.ToastUtil;
import net.comet.lazyorder.widget.MultiStateView;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import butterknife.Bind;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;
import io.nlopez.smartadapters.views.BindableLayout;

public abstract class BaseListFragment<I, IV extends BindableLayout, UC> extends BaseFragment<UC>
        implements BaseUiController.ListUi<I>, ViewEventListener<I> {

    @Bind(R.id.multi_state_view)
    MultiStateView mMultiStateView;

    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RecyclerMultiAdapter mAdapter;

    protected abstract void onItemClick(int actionId, I item, int position);

    protected abstract void refresh();

    protected void nextPage() {}

    protected void unauth() {}

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = SmartAdapter.empty()
                .map(getItemClass(), getItemViewClass())
                .listener(this)
                .into(mRecyclerView);

        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                nextPage();
            }
        });

        mMultiStateView.setState(MultiStateView.STATE_LOADING);
    }

    private Class<I> getItemClass() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        return (Class<I>) params[0];
    }

    private Class<IV> getItemViewClass() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        return (Class<IV>) params[1];
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    public void onNetworkError(final ResponseError error, int pageIndex) {
        if (error.getStatus() == HTTP_UNAUTHORIZED) {
            mMultiStateView.setState(MultiStateView.STATE_UNAUTH)
                    .setButton(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unauth();
                        }
                    });
        } else if (pageIndex == 1) {
            mMultiStateView.setState(MultiStateView.STATE_ERROR)
                    .setTitle(error.getMessage())
                    .setButton(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refresh();
                            mMultiStateView.setState(MultiStateView.STATE_LOADING);
                        }
                    });
        } else {
            ToastUtil.showToast(error.getMessage());
        }
    }

    @Override
    public void onChangeItem(List<I> items, int pageIndex) {
        if (!CollectionUtil.isEmpty(items)) {
            if (pageIndex == 1) {
                setIsPopulated(true);
                mAdapter.setItems(items);
                mMultiStateView.setState(MultiStateView.STATE_CONTENT);
            } else {
                mAdapter.addItems(items);
            }
            mRefreshLayout.setLoadMore(items.size() == AppConfig.PAGE_SIZE);
        } else {
            if (pageIndex == 1) {
                mMultiStateView.setState(MultiStateView.STATE_EMPTY)
                        .setButton(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refresh();
                                mMultiStateView.setState(MultiStateView.STATE_LOADING);
                            }
                        });
            } else {
                ToastUtil.showToast(R.string.toast_error_not_have_more);
            }
            mRefreshLayout.setLoadMore(false);
        }
        setIsPopulated(true);
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishRefreshLoadMore();
    }

    @Override
    public void onViewEvent(int actionId, I item, int position, View view) {
        onItemClick(actionId, item, position);
    }
}
