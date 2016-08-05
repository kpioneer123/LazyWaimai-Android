package net.comet.lazyorder.module.qualifiers;

import android.support.annotation.IntDef;
import net.comet.lazyorder.util.Constants;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({
        Constants.ClickType.CLICK_TYPE_DELETE_BTN_CLICKED,
        Constants.ClickType.CLICK_TYPE_EDIT_BTN_CLICKED,
        Constants.ClickType.CLICK_TYPE_BUSINESS_CLICKED,
        Constants.ClickType.CLICK_TYPE_ORDER_CLICKED,
        Constants.ClickType.CLICK_TYPE_ADDRESS_CLICKED,
        Constants.ClickType.CLICK_TYPE_PRODUCT_CATEGORY_CLICKED,
        Constants.ClickType.CLICK_TYPE_SHOPPING_CART_CLICKED
})
public @interface ClickType {
}