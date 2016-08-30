package net.comet.lazyorder.network.service;

import net.comet.lazyorder.model.bean.Token;
import java.util.Map;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface TokenService {

    @POST("oauth/access_token")
    Observable<Token> accessToken(@QueryMap Map<String, String> params);

    @POST("oauth/access_token")
    Observable<Token> refreshToken(@QueryMap Map<String, String> params);
}