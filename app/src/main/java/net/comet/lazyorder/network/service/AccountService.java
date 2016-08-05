package net.comet.lazyorder.network.service;

import net.comet.lazyorder.model.bean.Token;
import net.comet.lazyorder.model.bean.User;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;


public interface AccountService {

    /**
     * 发送验证码
     * @param mobile
     * @return
     */
    @FormUrlEncoded
    @POST("users")
    Observable<Boolean> sendCode(@Field("action") String action,
                                @Field("mobile") String mobile);

    /**
     * 检查验证码
     * @param mobile
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST("users")
    Observable<Boolean> verifyCode(@Field("action") String action,
                                 @Field("mobile") String mobile,
                                 @Field("code") String code);

    /**
     * 创建帐号
     * @param mobile
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("users")
    Observable<Boolean> register(@Field("action") String action,
                               @Field("mobile") String mobile,
                               @Field("password") String password);

    /**
     * 用户登录
     * @param params
     * @return
     */
    @POST("oauth/access_token")
    Observable<Token> login(@QueryMap Map<String, String> params);

    /**
     * 获取用户资料
     * @param id
     * @return
     */
    @GET("users/{id}")
    Observable<User> profile(@Path("id") String id);

    /**
     * 设置默认地址
     * @param userId
     * @param addressId
     * @return
     */
    @FormUrlEncoded
    @PUT("users/{id}")
    Observable<User> setLastAddress(@Path("id") String userId,
                             @Field("last_address_id") String addressId);
}