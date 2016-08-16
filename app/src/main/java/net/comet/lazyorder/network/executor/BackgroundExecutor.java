package net.comet.lazyorder.network.executor;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import com.google.common.base.Preconditions;
import com.orhanobut.logger.Logger;
import net.comet.lazyorder.BuildConfig;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.network.GsonHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;

public class BackgroundExecutor {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private final ExecutorService mExecutorService;

    public BackgroundExecutor(ExecutorService executorService) {
        mExecutorService = Preconditions.checkNotNull(executorService, "executorService cannot be null");
    }

    public <T> void execute(NetworkCallRunnable<T> runnable) {
        mExecutorService.execute(new NetworkCallRunner<>(runnable));
    }

    public <T> void execute(BackgroundCallRunnable<T> runnable) {
        mExecutorService.execute(new BackgroundCallRunner<>(runnable));
    }

    private class BackgroundCallRunner<T> implements Runnable {
        private final BackgroundCallRunnable<T> mBackgroundRunnable;

        BackgroundCallRunner(BackgroundCallRunnable<T> runnable) {
            mBackgroundRunnable = runnable;
        }

        @Override
        public final void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBackgroundRunnable.preExecute();
                }
            });
            T result = mBackgroundRunnable.runAsync();
            sHandler.post(new ResultCallback(result));
        }

        private class ResultCallback implements Runnable {
            private final T mResult;

            private ResultCallback(T result) {
                mResult = result;
            }

            @Override
            public void run() {
                mBackgroundRunnable.postExecute(mResult);
            }
        }
    }

    class NetworkCallRunner<T> implements Runnable {

        private final NetworkCallRunnable<T> mBackgroundRunnable;

        NetworkCallRunner(NetworkCallRunnable<T> runnable) {
            mBackgroundRunnable = runnable;
        }

        @Override
        public final void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBackgroundRunnable.onPreCall();
                }
            });
            T result = null;
            ResponseError responseError = null;
            Map<String, String> extras = null;
            // 先检测设备是否联网
//            if (!SystemUtil.checkNet(ActivityStack.create().top())) {
//                responseError = new ResponseError(HTTP_NOT_HAVE_NETWORK, StringFetcher.getString(R.string.toast_error_not_have_network));
//                sHandler.post(new ResultCallback(null, null, responseError));
//                return;
//            }
            try {
                Call<T> call = mBackgroundRunnable.doBackgroundCall();
                Response<T> response = call.execute();
                if (BuildConfig.DEBUG) {
                    Logger.json(response.raw().toString());
                }
                if (response.isSuccessful()) {
                    result = response.body();
                    extras = new HashMap<>();
                    Headers headers = response.headers();
                    for (int i = 0, size = headers.size(); i < size; i++) {
                        extras.put(headers.name(i), headers.value(i));
                    }
                } else {
                    responseError = GsonHelper.builderGson().fromJson(response.errorBody().string(), ResponseError.class);
                }
            } catch (Exception e) {
//                if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException) {
//                    responseError = new ResponseError(HTTP_NETWORK_ERROR, StringFetcher.getString(R.string.toast_error_network));
//                } else if (e instanceof JsonParseException) {
//                    responseError = new ResponseError(HTTP_SERVER_ERROR, StringFetcher.getString(R.string.toast_error_server));
//                } else {
//                    responseError = new ResponseError(HTTP_UNKNOWN_ERROR, StringFetcher.getString(R.string.toast_error_unknown));
//                }

                if (BuildConfig.DEBUG) {
                    Logger.e("ResponseError while completing network call");
                    e.printStackTrace();
                }
            }
            sHandler.post(new ResultCallback(result, extras, responseError));
        }

        private class ResultCallback implements Runnable {
            private final T mResult;
            private final Map<String, String> mExtras;
            private final ResponseError mResponseError;

            private ResultCallback(T result, Map<String, String> extras, ResponseError responseError) {
                mResult = result;
                mExtras = extras;
                mResponseError = responseError;
            }

            @Override
            public void run() {
                if (mResponseError == null) {
                    mBackgroundRunnable.onSuccess(mResult, mExtras);
                } else {
                    mBackgroundRunnable.onError(mResponseError);
                }
                mBackgroundRunnable.onFinished();
            }
        }
    }
}
