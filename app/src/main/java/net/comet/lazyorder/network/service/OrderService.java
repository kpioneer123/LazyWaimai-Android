package net.comet.lazyorder.network.service;

import net.comet.lazyorder.model.bean.Order;
import net.comet.lazyorder.model.bean.ResultsPage;
import net.comet.lazyorder.model.bean.SettleResult;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface OrderService {

    @FormUrlEncoded
    @POST("orders/check")
    Observable<SettleResult> settle(@Field("business_id") String businessId,
                                    @Field("pay_method") int payMethod,
                                    @Field("product_list") String productListJson);

    @FormUrlEncoded
    @POST("orders")
    Observable<Order> submit(@Field("cart_id") String cartId,
                             @Field("booked_at") long bookedAt,
                             @Field("remark") String remark);

    @GET("orders?expand=business_info")
    Observable<ResultsPage<Order>> orders(@Query("page") int page, @Query("size") int size);

    @GET("orders/{id}?expand=cart_info")
    Observable<Order> detail(@Path("id") String orderId);
}