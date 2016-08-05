package net.comet.lazyorder.model.bean;

import com.google.gson.JsonParseException;
import com.orhanobut.logger.Logger;
import net.comet.lazyorder.BuildConfig;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppCookie;
import net.comet.lazyorder.network.GsonHelper;
import net.comet.lazyorder.util.StringFetcher;
import org.apache.http.conn.ConnectTimeoutException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import retrofit2.adapter.rxjava.HttpException;
import static net.comet.lazyorder.util.Constants.Code.HTTP_NETWORK_ERROR;
import static net.comet.lazyorder.util.Constants.Code.HTTP_SERVER_ERROR;
import static net.comet.lazyorder.util.Constants.Code.HTTP_UNKNOWN_ERROR;
import static net.comet.lazyorder.util.Constants.Code.HTTP_UNAUTHORIZED;

public class ResponseError {

    private int status;
    private String message;

    public ResponseError(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ResponseError handle(Throwable throwable) {
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
                error = GsonHelper.builderGson().
                        fromJson(exception.response().errorBody().string(),
                                ResponseError.class);
                if (error.getStatus() == HTTP_UNAUTHORIZED) {
                    if (AppCookie.isLoggin()) {
                        error = new ResponseError(HTTP_UNAUTHORIZED,
                                StringFetcher.getString(R.string.toast_error_credentials_have_expired));
                    } else {
                        error = new ResponseError(HTTP_UNAUTHORIZED,
                                StringFetcher.getString(R.string.toast_error_not_login));
                    }
                }
            } catch (Exception e) {
                if (e instanceof JsonParseException) {
                    error = new ResponseError(HTTP_SERVER_ERROR,
                            StringFetcher.getString(R.string.toast_error_server));
                } else {
                    error = new ResponseError(HTTP_UNKNOWN_ERROR,
                            StringFetcher.getString(R.string.toast_error_unknown));
                }

                if (BuildConfig.DEBUG) {
                    Logger.e("ResponseError while completing network call");
                    e.printStackTrace();
                }
            }
        } else {
            error = new ResponseError(HTTP_UNKNOWN_ERROR,
                    StringFetcher.getString(R.string.toast_error_unknown));

            if (BuildConfig.DEBUG) {
                throwable.printStackTrace();
            }
        }

        return error;
    }
}