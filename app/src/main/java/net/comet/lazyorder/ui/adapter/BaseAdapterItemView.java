package net.comet.lazyorder.ui.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import net.comet.lazyorder.module.qualifiers.ClickType;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableLayout;

public abstract class BaseAdapterItemView<T> extends BindableLayout<T> {

    public BaseAdapterItemView(Context context) {
        super(context);
    }

    public BaseAdapterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseAdapterItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
    }

    public void notifyItemAction(@ClickType int actionId, T theItem, View view) {
        if(viewEventListener != null) {
            viewEventListener.onViewEvent(actionId, theItem, position, view);
        }
    }

    public void notifyItemAction(@ClickType int actionId, View view) {
        notifyItemAction(actionId, item, view);
    }

    public void notifyItemAction(@ClickType int actionId) {
        notifyItemAction(actionId, item, this);
    }
}