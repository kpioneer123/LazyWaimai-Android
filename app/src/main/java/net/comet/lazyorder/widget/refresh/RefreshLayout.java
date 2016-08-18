package net.comet.lazyorder.widget.refresh;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

/**
 * Created by panwankun on 16/7/13.
 */
public class RefreshLayout extends FrameLayout {

    private static final float OFFSET_RADIO = 1.2f;

    private View mChildView;
    private View mScrollableView;
    private BaseRefreshHeaderView mHeaderView;
    private BaseRefreshFooterView mFooterView;

    private float mHeaderHeight;
    private float mFooterHeight;
    private float mTouchY;
    private int mTouchSlop;

    private boolean mIsOverlay;
    private int mMaxOffset;

    private boolean mEnableLoadMore;
    private boolean mIsRefreshing;
    private boolean mIsLoadMoreing;

    private OnRefreshListener mOnRefreshListener;

    public RefreshLayout(Context context) {
        this(context, null, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (getChildCount() > 1) {
            throw new RuntimeException("can only have one child view");
        }

        mChildView = getChildAt(0);
        if (mChildView == null) {
            throw new RuntimeException("must be have one child view");
        }

        if (isScrollableView(mChildView)) {
            mScrollableView = mChildView;
        } else {
            mScrollableView = findScrollableChildView(mChildView);
        }
        if (mScrollableView == null) {
            mScrollableView = mChildView;
        }

        initHeaderView();
        initFooterView();
        bindScrollListener();
    }

    /**
     * 初始化刷新的顶部视图
     */
    private void initHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = new DefaultRefreshHeaderView(getContext());
        }
        // 初始化配置
        HeaderConfig headerConfig = mHeaderView.getConfig();
        mIsOverlay = headerConfig.isOverlay;
        mMaxOffset = headerConfig.maxOffset;
        // 提前测量刷新视图的宽高
        measureView(mHeaderView);
        int height = mHeaderView.getMeasuredHeight();
        if (height > 0) {
            mHeaderHeight = height;
        } else {
            throw new RuntimeException("the height of the header view is 0!");
        }
        // 设置layout params
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) mHeaderHeight);
        layoutParams.gravity = Gravity.TOP;
        mHeaderView.setLayoutParams(layoutParams);

        ViewCompat.setTranslationY(mHeaderView, -mHeaderHeight);

        addView(mHeaderView);
    }

    /**
     * 初始化加载更多的底部视图
     */
    private void initFooterView() {
        if (mFooterView == null) {
            mFooterView = new DefaultRefreshFooterView(getContext());
        }
        measureView(mFooterView);
        int height = mFooterView.getMeasuredHeight();
        if (height > 0) {
            mFooterHeight = height;
            Log.e("------", "mFooterHeight:" + mFooterHeight);
        } else {
            throw new RuntimeException("the height of the feader view is 0!");
        }

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) mFooterHeight);
        layoutParams.gravity = Gravity.BOTTOM;
        mFooterView.setLayoutParams(layoutParams);

        mFooterView.setVisibility(View.GONE);

        addView(mFooterView);
    }

    /**
     * 测量视图的宽高
     * @param view
     */
    private void measureView(View view) {
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }

    /**
     * 是否是可滚动的View
     * @return
     */
    private boolean isScrollableView(View view) {
        return view instanceof AbsListView || view instanceof ScrollView
                || view instanceof RecyclerView;
    }

    /**
     * 查找可滚动的子View
     * @param root
     * @return
     */
    private View findScrollableChildView(View root) {
        if (root == null) {
            return null;
        }

        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            View view = null;
            for (int i = 0; i < group.getChildCount(); i++) {
                view = group.getChildAt(i);
                if (isScrollableView(view)) {
                    return view;
                } else {
                    view = findScrollableChildView(view);
                    if (view != null) {
                        return view;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 绑定滚动的监听器以触发加载更多
     */
    private void bindScrollListener() {
        if (mScrollableView instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) mScrollableView;
            absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == RecyclerView.SCROLL_STATE_IDLE && !canChildScrollDown()) {
                        resolveLoadMoreLogic();
                    }
                }
                @Override
                public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
        } else if (mScrollableView instanceof RecyclerView) {
            final RecyclerView recyclerView = (RecyclerView) mScrollableView;
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                    if (scrollState == RecyclerView.SCROLL_STATE_IDLE && !canChildScrollDown()) {
                        resolveLoadMoreLogic();
                    }
                }
            });
        } else {
            throw new RuntimeException("can't support view type.");
        }
    }

    /**
     * 触发加载更多的逻辑
     */
    private void resolveLoadMoreLogic() {
        if (mEnableLoadMore && !mIsLoadMoreing) {
            // 让列表视图向上平移以留出空位来显示加载更多的视图
            LayoutParams lp = (LayoutParams) mChildView.getLayoutParams();
            lp.bottomMargin = (int) mFooterHeight;
            mChildView.setLayoutParams(lp);
            mChildView.requestLayout();
            // 显示加载更多的视图
            mFooterView.setVisibility(VISIBLE);
            startLoadMore();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY() - mTouchY;
                if (dy > mTouchSlop && !canChildScrollUp()) {
                    if (mOnRefreshListener != null) {
                        return mOnRefreshListener.enableRefresh();
                    } else {
                        return true;
                    }
                }
                break;
            default:
                // nothing to do
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 能否继续往上滑动（是否到达顶部）
     */
    public boolean canChildScrollUp() {
        if (Build.VERSION.SDK_INT < 14) {
            if (mScrollableView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mScrollableView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mScrollableView, -1) || mScrollableView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mScrollableView, -1);
        }
    }

    /**
     * 能否继续往下滑动（是否到达底部）
     */
    public boolean canChildScrollDown() {
        if (Build.VERSION.SDK_INT < 14) {
            if (mScrollableView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mScrollableView;
                if (absListView.getChildCount() > 0) {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1 && lastChildBottom <= absListView.getMeasuredHeight();
                } else {
                    return false;
                }
            } else {
                return ViewCompat.canScrollVertically(mScrollableView, 1) || mScrollableView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mScrollableView, 1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = e.getY() - mTouchY;
                dy = Math.max(0, dy);
                // 按照比例缩减滑动的有效距离
                float offsetY = dy / OFFSET_RADIO;
                offsetY = Math.min(offsetY, mMaxOffset);
                // 下拉刷新视图的可见度
                float fraction = offsetY / mHeaderHeight;
                fraction = fraction < 1 ? fraction : 1;

                // 逐渐显示下拉刷新的视图
                ViewCompat.setTranslationY(mHeaderView, (int) (offsetY - mHeaderHeight));

                // 如果不是覆盖模式,则将列表视图向下平移以留出空位来显示下拉刷新的视图
                if (!mIsOverlay) {
                    ViewCompat.setTranslationY(mChildView, offsetY);
                }
                if (offsetY >= mHeaderHeight) {
                    mHeaderView.onRelease();
                } else {
                    mHeaderView.onPull(fraction);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsOverlay) {
                    if (ViewCompat.getTranslationY(mHeaderView) >= 0) {
                        viewAnimateTranslationY(mHeaderView, 0);
                        startRefresh();
                    } else {
                        viewAnimateTranslationY(mHeaderView, -mHeaderHeight);
                    }
                } else {
                    if (ViewCompat.getTranslationY(mHeaderView) >= 0) {
                        viewAnimateTranslationY(mHeaderView, 0);
                        viewAnimateTranslationY(mChildView, mHeaderHeight);
                        startRefresh();
                    } else {
                        viewAnimateTranslationY(mHeaderView, -mHeaderHeight);
                        viewAnimateTranslationY(mChildView, 0);
                    }
                }
                return true;
            default:
                // nothing to do
                break;
        }

        return super.onTouchEvent(e);
    }

    /**
     * 使用动画的方式将视图在 Y 轴上平移
     * @param v
     * @param y
     */
    private void viewAnimateTranslationY(final View v, final float y) {
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(v);
        viewPropertyAnimatorCompat.setDuration(250);
        viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
        viewPropertyAnimatorCompat.translationY(y);
        viewPropertyAnimatorCompat.start();
    }

    /**
     * 开始刷新
     */
    private void startRefresh() {
        mIsRefreshing = true;
        mHeaderView.onRefreshing();
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * 开始加载更多
     */
    private void startLoadMore() {
        mIsLoadMoreing = true;
        mFooterView.onLoadMore();
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onLoadMore();
        }
    }

    /**
     * 是否正在刷新
     * @return
     */
    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    /**
     * 是否正在加载更多
     * @return
     */
    public boolean isLoadMoreing() {
        return mIsLoadMoreing;
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        if (mIsRefreshing) {
            return;
        }
        if (mIsOverlay) {
            viewAnimateTranslationY(mHeaderView, 0);
        } else {
            viewAnimateTranslationY(mHeaderView, 0);
            viewAnimateTranslationY(mChildView, mHeaderHeight);
        }
        startRefresh();
    }

    /**
     * 完成下拉刷新
     */
    public void finishRefresh() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mIsRefreshing) {
                    mIsRefreshing = false;

                    if (mIsOverlay) {
                        viewAnimateTranslationY(mHeaderView, -mHeaderHeight);
                    } else {
                        viewAnimateTranslationY(mHeaderView, -mHeaderHeight);
                        viewAnimateTranslationY(mChildView, 0);
                    }

                    mHeaderView.onComplete();
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onFinish();
                    }
                }
            }
        });
    }

    /**
     * 完成加载更多
     */
    public void finishLoadMore() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mIsLoadMoreing) {
                    mIsLoadMoreing = false;

                    LayoutParams lp = (LayoutParams) mChildView.getLayoutParams();
                    lp.bottomMargin = 0;
                    mChildView.setLayoutParams(lp);
                    mChildView.requestLayout();

                    mFooterView.setVisibility(GONE);
                    mFooterView.onComplete();
                }
            }
        });
    }

    /**
     * 设置自定义的下拉刷新视图
     * @param headerView
     */
    public void setRefreshHeaderView(BaseRefreshHeaderView headerView) {
        mHeaderView = headerView;
    }

    /**
     * 设置自定义的加载更多视图
     * @param footerView
     */
    public void setRefreshFooterView(BaseRefreshFooterView footerView) {
        mFooterView = footerView;
    }

    /**
     * 获取下拉刷新视图
     */
    public BaseRefreshHeaderView getRefreshHeaderView() {
        return mHeaderView;
    }

    /**
     * 获取加载更多视图
     */
    public BaseRefreshFooterView getRefreshFooterView() {
        return mFooterView;
    }

    /**
     * 设置下拉刷新视图是否为覆盖模式
     * @param isOverlay
     */
    public void setIsOverlay(boolean isOverlay) {
        mIsOverlay = isOverlay;
    }

    /**
     * 设置加载更多是否可用
     * @param enableLoadMore
     */
    public void setEnableLoadMore(boolean enableLoadMore) {
        mEnableLoadMore = enableLoadMore;
    }

    /**
     * 设置刷新的监听器
     * @param onRefreshListener
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }
}
