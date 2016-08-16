package net.comet.lazyorder.widget.section;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import net.comet.lazyorder.R;
import net.comet.lazyorder.util.DensityUtil;

/**
 * author：cheikh.wang on 16/8/11 11:12
 * email：wanghonghi@126.com
 */
public class SectionListView extends LinearLayout {

    public SectionListView(Context context) {
        this(context, null);
    }

    public SectionListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.bg_section_list);
        int padding = DensityUtil.dip2px(context, 1.0f);
        setPadding(0, padding, 0, padding);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        hideLastItemLine(getChildCount());
    }

    public void refresh() {
        int childCount = getChildCount();
        if (childCount > 0) {
            int visibleChildCount = 0;
            for (int i = 0; i < childCount; i++) {
                RelativeLayout itemView = (RelativeLayout) getChildAt(i);
                View divider = itemView.findViewById(R.id.divider);
                if (divider != null) {
                    divider.setVisibility(VISIBLE);
                }
                if (itemView.getVisibility() != GONE) {
                    visibleChildCount++;
                }
            }
            hideLastItemLine(visibleChildCount);
        }
    }

    private void hideLastItemLine(int count) {
        if (count > 0) {
            RelativeLayout lastChildView = (RelativeLayout) getChildAt(count - 1);
            View divider = lastChildView.findViewById(R.id.divider);
            if (divider != null) {
                divider.setVisibility(GONE);
            }
        }
    }
}