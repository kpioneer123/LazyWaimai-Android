package net.comet.lazyorder.module.library;

import com.squareup.otto.Bus;
import net.comet.lazyorder.module.qualifiers.ForDatabase;
import net.comet.lazyorder.module.qualifiers.GeneralPurpose;
import net.comet.lazyorder.network.executor.BackgroundExecutor;
import java.util.concurrent.Executors;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module(
    includes = ContextProvider.class,
    library = true
)
public class UtilProvider {

    @Provides @Singleton
    public Bus provideEventBus() {
        return new Bus();
    }

    @Provides @Singleton @GeneralPurpose
    public BackgroundExecutor provideMultiThreadExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new BackgroundExecutor(Executors.newFixedThreadPool(numberCores * 2 + 1));
    }

    @Provides @Singleton @ForDatabase
    public BackgroundExecutor provideDatabaseThreadExecutor() {
        return new BackgroundExecutor(Executors.newSingleThreadExecutor());
    }
}
