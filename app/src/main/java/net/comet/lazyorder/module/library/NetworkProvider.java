package net.comet.lazyorder.module.library;

import android.content.Context;

import net.comet.lazyorder.context.AppConfig;
import net.comet.lazyorder.module.qualifiers.ApplicationContext;
import net.comet.lazyorder.module.qualifiers.CacheDirectory;
import net.comet.lazyorder.module.qualifiers.ShareDirectory;
import net.comet.lazyorder.network.RestApiClient;
import net.comet.lazyorder.util.Constants;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
    library = true,
    includes = ContextProvider.class
)
public class NetworkProvider {

    @Provides @Singleton
    public RestApiClient provideRestApiClient(@CacheDirectory File cacheLocation, @ApplicationContext Context context) {
        return new RestApiClient(context, cacheLocation);
    }

    @Provides @Singleton @CacheDirectory
    public File provideHttpCacheLocation(@ApplicationContext Context context) {
        return context.getCacheDir();
    }

    @Provides @Singleton @ShareDirectory
    public File provideShareLocation(@ApplicationContext Context context) {
        return new File(context.getFilesDir(), Constants.Persistence.SHARE_FILE);
    }

}
