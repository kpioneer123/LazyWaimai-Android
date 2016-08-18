package net.comet.lazyorder.widget.refresh;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import net.comet.lazyorder.widget.CircleImageView;
import net.comet.lazyorder.widget.MaterialProgressDrawable;

/**
 * 默认的头部下拉刷新view
 */
public class DefaultRefreshHeaderView extends BaseRefreshHeaderView {

    // 旋转View的默认背景
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // 旋转View的直径
    private static final int CIRCLE_DIAMETER = 40;
    // 旋转View的最低透明度
    private static final int MIN_ALPHA = 60;

    private CircleImageView circleImageView;
    private MaterialProgressDrawable progressDrawable;

    public DefaultRefreshHeaderView(Context context) {
        super(context);
        progressDrawable = new MaterialProgressDrawable(getContext(), this);
        progressDrawable.setBackgroundColor(CIRCLE_BG_LIGHT);

        circleImageView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER / 2);
        circleImageView.setImageDrawable(progressDrawable);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = 50;
        layoutParams.bottomMargin = 50;
        addView(circleImageView, layoutParams);
    }

    @Override
    public HeaderConfig getConfig() {
        HeaderConfig config = new HeaderConfig();
        config.isOverlay = true;
        config.maxOffset = 300;
        return config;
    }

    @Override
    public void onPull(float fraction) {
        progressDrawable.showArrow(false);

        progressDrawable.setStartEndTrim(0, getEndTrim(fraction)); // 箭头的长度
        progressDrawable.setAlpha(getTopAlpha(fraction));
        progressDrawable.setRotation(getRotation(fraction));
        progressDrawable.setArrowScale(getArrowScale(fraction));
    }

    private float getEndTrim(float fraction) {
        return 0.8f * fraction;
    }

    private int getTopAlpha(float fraction) {
        return MIN_ALPHA + (int) ((200.0f - MIN_ALPHA) * fraction);
    }

    private float getArrowScale(float fraction) {
        return fraction;
    }

    private int getRotation(float fraction) {
        return 0;
    }

    public void setColorSchemeColors(int... colors) {
        progressDrawable.setColorSchemeColors(colors);
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onRefreshing() {
        progressDrawable.start();
    }

    @Override
    public void onComplete() {
        progressDrawable.stop();
        ScaleAnimation animation = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(150);
        circleImageView.clearAnimation();
        circleImageView.startAnimation(animation);
    }
}