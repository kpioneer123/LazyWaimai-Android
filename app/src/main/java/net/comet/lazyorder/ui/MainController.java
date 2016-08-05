package net.comet.lazyorder.ui;

import com.google.common.base.Preconditions;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainController extends BaseUiController<MainController.MainUi, MainController.MainUiCallbacks> {

    public static final String LOG_TAG = MainController.class.getSimpleName();

    private final UserController mUserController;
    private final AddressController mAddressController;
    private final BusinessController mBusinessController;
    private final OrderController mOrderController;

    @Inject
    public MainController(UserController userController, AddressController addressController,
                          BusinessController businessController, OrderController orderController) {
        super();
        mUserController = Preconditions.checkNotNull(userController, "userController cannot be null");
        mAddressController = Preconditions.checkNotNull(addressController, "addressController cannot be null");
        mBusinessController = Preconditions.checkNotNull(businessController, "businessController cannot be null");
        mOrderController = Preconditions.checkNotNull(orderController, "orderController cannot be null");
    }

    @Override
    protected void onInited() {
        super.onInited();
        mUserController.init();
        mAddressController.init();
        mBusinessController.init();
        mOrderController.init();
    }

    @Override
    protected void onSuspended() {
        mUserController.suspend();
        mAddressController.suspend();
        mBusinessController.suspend();
        mOrderController.suspend();
        super.onSuspended();
    }

    @Override
    protected MainUiCallbacks createUiCallbacks(final MainUi ui) {
        return new MainUiCallbacks() {

        };
    }

    public void attachDisplay(Display display) {
        Preconditions.checkNotNull(display, "display is null");
        Preconditions.checkState(getDisplay() == null, "we currently have a display");
        setDisplay(display);
    }

    public void detachDisplay(Display display) {
        Preconditions.checkNotNull(display, "display is null");
        Preconditions.checkState(getDisplay() == display, "display is not attached");
        setDisplay(null);
    }

    @Override
    protected void setDisplay(Display display) {
        super.setDisplay(display);
        mUserController.setDisplay(display);
        mAddressController.setDisplay(display);
        mBusinessController.setDisplay(display);
        mOrderController.setDisplay(display);
    }

    /**
     * 当返回键被按下时
     */
    public void onBackButtonPressed() {
        Display display = getDisplay();
        if (display != null) {
            if (!display.popTopFragmentBackStack()) {
                display.navigateUp();
            }
        }
    }

    public final UserController getUserController() {
        return mUserController;
    }

    public final BusinessController getBusinessController() {
        return mBusinessController;
    }

    public final AddressController getAddressController() {
        return mAddressController;
    }

    public final OrderController getOrderController() {
        return mOrderController;
    }

    public interface MainUi extends BaseUiController.Ui<MainUiCallbacks> {
    }

    /**
     * 留给UI界面调用的接口,此接口会在控制器里被实现
     */
    public interface MainUiCallbacks {
    }
}
