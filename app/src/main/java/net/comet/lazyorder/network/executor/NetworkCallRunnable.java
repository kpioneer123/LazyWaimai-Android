package net.comet.lazyorder.network.executor;

import net.comet.lazyorder.model.bean.ResponseError;
import java.util.Map;
import retrofit2.Call;

public abstract class NetworkCallRunnable<R> {

    public void onPreCall() {}

    public abstract Call<R> doBackgroundCall();

    public abstract void onSuccess(R result, Map<String, String> headers);

    public abstract void onError(ResponseError responseError);

    public void onFinished() {}

 }
