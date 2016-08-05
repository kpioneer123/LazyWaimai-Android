package net.comet.lazyorder.module.library;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.AssetManager;
import com.google.common.base.Preconditions;
import net.comet.lazyorder.module.qualifiers.ApplicationContext;
import net.comet.lazyorder.module.qualifiers.FilesDirectory;
import java.io.File;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module(
    library = true
)
public class ContextProvider {

    private final Context mApplicationContext;

    public ContextProvider(Context context) {
        mApplicationContext = Preconditions.checkNotNull(context, "context cannot be null");
    }

    @Provides @ApplicationContext
    public Context provideApplicationContext() {
        return mApplicationContext;
    }

    @Provides @FilesDirectory
    public File providePrivateFileDirectory() {
        return mApplicationContext.getFilesDir();
    }

    @Provides @Singleton
    public AssetManager provideAssetManager() {
        return mApplicationContext.getAssets();
    }

    @Provides @Singleton
    public AlarmManager provideAlarmManager() {
        return (AlarmManager) mApplicationContext.getSystemService(Context.ALARM_SERVICE);
    }
}
