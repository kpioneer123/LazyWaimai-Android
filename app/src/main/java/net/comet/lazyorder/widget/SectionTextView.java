package net.comet.lazyorder.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.comet.lazyorder.R;
import net.comet.lazyorder.util.DensityUtil;

/**
 * Created by comet on 15/11/10.
 */
public class SectionTextView extends RelativeLayout {

    private ImageView mIconImg;
    private TextView mTitleTxt;
    private TextView mSubTitleTxt;
    private ImageView mNextImg;

    private Drawable mIcon;
    private CharSequence mTitle;
    private CharSequence mSubTitle;
    private boolean mIsNext;

    public SectionTextView(Context context) {
        this(context, null);
    }

    public SectionTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        inflate(context, R.layout.layout_section_item, this);
        mIconImg = (ImageView) findViewById(R.id.img_section_item_icon);
        mTitleTxt = (TextView) findViewById(R.id.txt_section_item_title);
        mSubTitleTxt = (TextView) findViewById(R.id.txt_section_item_subtitle);
        mNextImg = (ImageView) findViewById(R.id.img_section_item_next);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SectionTextView);
        mIcon = array.getDrawable(R.styleable.SectionTextView_section_icon);
        mTitle = array.getText(R.styleable.SectionTextView_section_title);
        mSubTitle = array.getText(R.styleable.SectionTextView_section_subtitle);
        mIsNext = array.getBoolean(R.styleable.SectionTextView_section_next, false);
        array.recycle();

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 58.0f)));

        renderIcon();
        renderTitle();
        renderExtension();
    }

    private void renderIcon() {
        if(mIcon != null) {
            mIconImg.setImageDrawable(mIcon);
            mIconImg.setVisibility(View.VISIBLE);
        } else {
            mIconImg.setVisibility(View.GONE);
        }
    }

    private void renderTitle() {
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleTxt.setText(mTitle);
        } else {
            throw new IllegalArgumentException("title must be set value!!!");
        }
    }

    protected void renderExtension() {
        mSubTitleTxt.setVisibility(View.GONE);
        mNextImg.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mSubTitle)) {
            mSubTitleTxt.setText(mSubTitle);
            mSubTitleTxt.setVisibility(View.VISIBLE);
        } else if (mIsNext) {
            mNextImg.setVisibility(View.VISIBLE);
        }
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            mIcon = icon;
            renderIcon();
        }
    }

    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle = title;
            renderTitle();
        }
    }

    public void setSubTitle(CharSequence subTitle) {
        if (!TextUtils.isEmpty(subTitle)) {
            mSubTitle = subTitle;
            renderExtension();
        }
    }
}
