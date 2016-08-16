package net.comet.lazyorder.ui;

import android.util.Log;
import com.google.common.base.Preconditions;
import net.comet.lazyorder.context.AppConfig;
import net.comet.lazyorder.model.bean.ResponseError;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class BaseUiController<U extends BaseUiController.Ui<UC>, UC> extends BaseController {

    private final Set<U> mUis;
    private final Set<U> mUnmodifiableUis;

    public BaseUiController() {
        mUis = new CopyOnWriteArraySet<>();
        mUnmodifiableUis = Collections.unmodifiableSet(mUis);
    }

    /**
     * 绑定UI
     * @param ui
     */
    public synchronized final void attachUi(U ui) {
        Preconditions.checkArgument(ui != null, "ui cannot be null");
        Preconditions.checkState(!mUis.contains(ui), "UI is already attached");
        mUis.add(ui);
        ui.setCallbacks(createUiCallbacks(ui));
        if (isInited()) {
            onUiAttached(ui);
        }
    }

    /**
     * 启动UI
     * @param ui
     */
    public synchronized final void startUi(U ui) {
        Preconditions.checkArgument(ui != null, "ui cannot be null");
        Preconditions.checkState(mUis.contains(ui), "ui is not attached");
        if (!(ui instanceof SubUi)) {
            displayUpNavigation(!ui.isModal());
            updateDisplayTitle(ui);
        }
        if (!ui.isPopulated()) {
            populateUi(ui);
        }
        onUiStarted(ui);
    }

    /**
     * 分离UI
     * @param ui
     */
    public synchronized final void detachUi(U ui) {
        Preconditions.checkArgument(ui != null, "ui cannot be null");
        Preconditions.checkState(mUis.contains(ui), "ui is not attached");
        ui.setCallbacks(null);
        mUis.remove(ui);
        onUiDetached(ui);
    }

    /**
     * UI绑定后的回调
     * @param ui
     */
    protected void onUiAttached(U ui) {}

    /**
     * UI启动后的回调
     * @param ui
     */
    protected void onUiStarted(U ui) {}

    /**
     * UI分离后的回调
     * @param ui
     */
    protected void onUiDetached(U ui) {}

    /**
     * 留给子类用来返回『UI的回调接口』的方法
     * @param ui
     * @return
     */
    protected abstract UC createUiCallbacks(U ui);

    /**
     * 留给子类『加载UI』的方法
     * @param ui
     */
    protected void populateUi(U ui) {}

    /**
     * 填充可变UI集合里的所有UI
     */
    protected synchronized final void populateUis() {
        if (AppConfig.DEBUG) {
            Log.d(getClass().getSimpleName(), "populateUis");
        }
        for (U ui : mUis) {
            populateUi(ui);
        }
    }

    /**
     * 显示返回的home按钮
     */
    protected final void displayUpNavigation(boolean isShowArrow) {
        Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(isShowArrow);
        }
    }

    /**
     * 显示指定的标题
     * @param title
     */
    protected final void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    /**
     * 显示指定UI的标题
     * @param ui
     */
    protected final void updateDisplayTitle(U ui) {
        updateDisplayTitle(getUiTitle(ui));
    }

    /**
     * 留给子类用来返回『指定UI的标题』的方法
     * @param ui
     * @return
     */
    protected String getUiTitle(U ui) {
        return null;
    }

    /**
     * 返回不可变的所有UI集合
     * @return
     */
    protected final Set<U> getUis() {
        return mUnmodifiableUis;
    }

    /**
     * 返回指定UI的ID
     * @param ui
     * @return
     */
    protected int getId(U ui) {
        return ui.hashCode();
    }

    /**
     * 通过ID查找UI
     * @param id
     * @return
     */
    protected synchronized U findUi(final int id) {
        for (U ui : mUis) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    /**
     * UI类必须实现此接口
     * @param <UC>
     */
    public interface Ui<UC> {
        boolean isModal();

        boolean isPopulated();

        void setCallbacks(UC callbacks);
    }

    public interface ListUi<E> {
        void onChangeItem(List<E> items, int pageIndex);

        void onNetworkError(ResponseError error, int pageIndex);
    }

    public interface SubUi {}
}
