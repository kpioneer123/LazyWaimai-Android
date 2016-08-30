package net.comet.lazyorder.widget.section;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.util.DensityUtil;

public abstract class SectionItemView extends RelativeLayout {

    protected RelativeLayout itemLayout;
    protected TextView nameTxt;
    protected ImageView indicatorImg;
    protected FrameLayout extensionLayout;

    public SectionItemView(Context context) {
        this(context, null);
    }

    public SectionItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View view = LayoutInflater.from(context).inflate(R.layout.view_section_item, this);
        itemLayout = (RelativeLayout) view.findViewById(R.id.layout_item);
        nameTxt = (TextView) view.findViewById(R.id.txt_name);
        indicatorImg = (ImageView) view.findViewById(R.id.img_indicator);
        extensionLayout = (FrameLayout) view.findViewById(R.id.layout_extension);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SectionItemView);
        CharSequence name = array.getText(R.styleable.SectionItemView_siv_name);
        Drawable icon = array.getDrawable(R.styleable.SectionItemView_siv_icon);
        boolean indicator = array.getBoolean(R.styleable.SectionItemView_siv_indicator, false);
        array.recycle();

        nameTxt.setText(name);
        if (icon != null) {
            renderIconView(icon);
        }
        if (indicator) {
            renderIndicator();
        }
        renderExtensionView();
    }

    private void renderIconView(Drawable icon) {
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        nameTxt.setCompoundDrawables(icon, null, null, null);
        nameTxt.setCompoundDrawablePadding(DensityUtil.dip2px(getContext(), 15));
    }

    private void renderIndicator() {
        indicatorImg.setVisibility(View.VISIBLE);
    }

    protected abstract void renderExtensionView();

    public String getName() {
        return (String) nameTxt.getText();
    }

    public void setName(String name) {
        nameTxt.setText(name);
    }
}