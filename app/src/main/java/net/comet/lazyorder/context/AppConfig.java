package net.comet.lazyorder.context;

import net.comet.lazyorder.BuildConfig;

public class AppConfig {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String SERVER_URL = BuildConfig.SERVER_URL;
    public static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    public static final String CLIENT_ID = "android";
    public static final String CLIENT_SECRET = "afegewlnbnl987nfelwn";
    public static final int PAGE_SIZE = 10;
}
