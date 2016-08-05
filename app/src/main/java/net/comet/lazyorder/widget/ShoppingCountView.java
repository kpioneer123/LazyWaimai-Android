package net.comet.lazyorder.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.Product;
import net.comet.lazyorder.model.ShoppingCart;
import net.comet.lazyorder.util.ShoppingCartAnimation;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShoppingCountView extends FrameLayout {

    @Bind(R.id.btn_add_count)
    ImageView mAddCountBtn;

    @Bind(R.id.btn_sub_count)
    ImageView mSubCountBtn;

    @Bind(R.id.txt_selected_count)
    TextView mSelectedNumTxt;

    private Product mProduct;
    private ShoppingCartAnimation mShoppingCartAnim;

    public ShoppingCountView(Context context) {
        this(context, null);
    }

    public ShoppingCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context)
                .inflate(R.layout.layout_shopping_count_view, this);
        ButterKnife.bind(this);

        mShoppingCartAnim = new ShoppingCartAnimation((Activity) context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mProduct == null) {
            throw new RuntimeException(getClass().getSimpleName()+" must be called setProduct().");
        }
    }

    public void setProduct(Product product) {
        mProduct = product;
        updateView();
    }

    private void updateView() {
        int quantity = ShoppingCart.getInstance().getQuantityForProduct(mProduct);
        if (quantity > 0) {
            mSubCountBtn.setVisibility(View.VISIBLE);
            mSelectedNumTxt.setVisibility(View.VISIBLE);
            mSelectedNumTxt.setText(String.valueOf(quantity));
        } else {
            mSubCountBtn.setVisibility(View.INVISIBLE);
            mSelectedNumTxt.setVisibility(View.INVISIBLE);
        }
    }

    private void showClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_clear_shopping_cart_title);
        builder.setMessage(R.string.dialog_clear_shopping_cart_message);
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.setPositiveButton(R.string.dialog_clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShoppingCart.getInstance().clearAll();
            }
        });
        builder.create().show();
    }

    private void playAnimation() {
        if (mShoppingCartAnim != null) {
            mShoppingCartAnim.startAnimation(this, null);
        }
    }

    @OnClick({R.id.btn_add_count, R.id.btn_sub_count})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sub_count:
                if (ShoppingCart.getInstance().pop(mProduct)) {
                    updateView();
                }
                break;
            case R.id.btn_add_count:
                if (ShoppingCart.getInstance().push(mProduct)) {
                    playAnimation();
                    updateView();
                } else {
                    showClearDialog();
                }
                break;
        }
    }
}