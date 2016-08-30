package net.comet.lazyorder.network;

import com.google.gson.JsonParseException;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppConfig;
import net.comet.lazyorder.context.AppCookie;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.bean.Token;
import net.comet.lazyorder.util.StringFetcher;
import org.apache.http.conn.ConnectTimeoutException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import static net.comet.lazyorder.util.Constants.HttpCode.*;
import static net.comet.lazyorder.util.Constants.Key.*;

public class ResponseErrorProxy implements InvocationHandler {

    public static final String TAG = ResponseErrorProxy.class.getSimpleName();

    private Object mProxyObject;
    private RestApiClient mRestApiClient;

    public ResponseErrorProxy(Object proxyObject, RestApiClient restApiClient) {
        mProxyObject = proxyObject;
        mRestApiClient = restApiClient;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) {
        return Observable.just(null)
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object o) {
                        try {
                            return (Observable<?>) method.invoke(mProxyObject, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {
                        return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                            @Override
                            public Observable<?> call(Throwable throwable) {
                                ResponseError error = null;
                                if (throwable instanceof ConnectTimeoutException
                                        || throwable instanceof SocketTimeoutException
                                        || throwable instanceof UnknownHostException
                                        || throwable instanceof ConnectException) {
                                    error = new ResponseError(HTTP_NETWORK_ERROR,
                                            StringFetcher.getString(R.string.toast_error_network));
                                } else if (throwable instanceof HttpException) {
                                    HttpException exception = (HttpException) throwable;
                                    try {
                                        error = GsonHelper.builderGson().fromJson(
                                                exception.response().errorBody().string(), ResponseError.class);
                                    } catch (Exception e) {
                                        if (e instanceof JsonParseException) {
                                            error = new ResponseError(HTTP_SERVER_ERROR,
                                                    StringFetcher.getString(R.string.toast_error_server));
                                        } else {
                                            error = new ResponseError(HTTP_UNKNOWN_ERROR,
                                                    StringFetcher.getString(R.string.toast_error_unknown));
                                        }
                                    }
                                } else {
                                    error = new ResponseError(HTTP_UNKNOWN_ERROR,
                                            StringFetcher.getString(R.string.toast_error_unknown));
                                }

                                if (error.getStatus() == HTTP_UNAUTHORIZED) {
                                    return refreshTokenWhenTokenInvalid();
                                } else {
                                    return Observable.error(error);
                                }
                            }
                        });
                    }
                });
    }

    private Observable<?> refreshTokenWhenTokenInvalid() {
        synchronized (ResponseErrorProxy.class) {
            Map<String, String> params = new HashMap<>();
            params.put(PARAM_CLIENT_ID, AppConfig.CLIENT_ID);
            params.put(PARAM_CLIENT_SECRET, AppConfig.CLIENT_SECRET);
            params.put(PARAM_GRANT_TYPE, "refresh_token");
            params.put(PARAM_REFRESH_TOKEN, AppCookie.getRefreshToken());

            return mRestApiClient.tokenService()
                    .refreshToken(params)
                    .doOnNext(new Action1<Token>() {
                        @Override
                        public void call(Token token) {
                            AppCookie.saveAccessToken(token.getAccessToken());
                            AppCookie.saveRefreshToken(token.getRefreshToken());
                            mRestApiClient.setToken(token.getAccessToken());
                        }
                    });
        }
    }
}
