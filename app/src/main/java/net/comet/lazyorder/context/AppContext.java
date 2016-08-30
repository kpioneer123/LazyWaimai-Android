package net.comet.lazyorder.context;

import android.app.Application;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import net.comet.lazyorder.BuildConfig;
import net.comet.lazyorder.module.ApplicationModule;
import net.comet.lazyorder.module.library.ContextProvider;
import net.comet.lazyorder.module.library.InjectorModule;
import net.comet.lazyorder.module.qualifiers.ShareDirectory;
import net.comet.lazyorder.network.GsonHelper;
import net.comet.lazyorder.ui.MainController;
import net.comet.lazyorder.util.Injector;
import net.comet.lazyorder.util.PreferenceUtil;
import net.comet.lazyorder.util.ToastUtil;
import java.io.File;
import javax.inject.Inject;
import dagger.ObjectGraph;

public class AppContext extends Application implements Injector {

    private static AppContext mInstance;

    private ObjectGraph mObjectGraph;

    @Inject
    MainController mMainController;

    @Inject
    @ShareDirectory
    File mShareLocation;

    public static AppContext getContext() {
        return mInstance;
    }

    public MainController getMainController() {
        return mMainController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // 吐司初始化
        ToastUtil.init(this);

        // 本地存储工具类初始化
        PreferenceUtil.init(this, GsonHelper.builderGson());

        // 日志打印器初始化
        Logger.init(getPackageName()).setLogLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE);

        // 依赖注解初始化
        mObjectGraph = ObjectGraph.create(
                new ApplicationModule(),
                new ContextProvider(this),
                new InjectorModule(this)
        );
        mObjectGraph.inject(this);
    }

    @Override
    public void inject(Object object) {
        mObjectGraph.inject(object);
    }
}