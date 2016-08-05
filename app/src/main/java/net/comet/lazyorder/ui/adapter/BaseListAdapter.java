package net.comet.lazyorder.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected final Activity mActivity;
    private final LayoutInflater mLayoutInflater;

    private final int mViewLayoutId;

    private List<T> mItems;

    public BaseListAdapter(Activity activity, int viewLayoutId) {
        mActivity = activity;
        mLayoutInflater = activity.getLayoutInflater();
        mViewLayoutId = viewLayoutId;
    }

    public void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup viewGroup) {
        final T item = getItem(position);
        View view = convertView;

        if (view == null) {
            view = mLayoutInflater.inflate(mViewLayoutId, viewGroup, false);
        }
        bindView(position, view, item);

        return view;
    }

    protected abstract void bindView(int position, View view, T item);
}
