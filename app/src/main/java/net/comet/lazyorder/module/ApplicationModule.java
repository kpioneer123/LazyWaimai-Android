package net.comet.lazyorder.module;

import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.module.library.InjectorModule;
import net.comet.lazyorder.module.library.NetworkProvider;
import net.comet.lazyorder.module.library.UtilProvider;
import dagger.Module;

@Module(
        injects = {
                AppContext.class,
        },
        includes = {
                UtilProvider.class,
                NetworkProvider.class,
                InjectorModule.class,
        }
)
public class ApplicationModule {
}