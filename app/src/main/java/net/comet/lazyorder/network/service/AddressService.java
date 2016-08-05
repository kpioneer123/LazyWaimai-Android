package net.comet.lazyorder.network.service;

import net.comet.lazyorder.model.bean.Address;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by comet on 15/8/29.
 */
public interface AddressService {

    @FormUrlEncoded
    @POST("addresses")
    Observable<Address> create(
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("summary") String summary,
            @Field("detail") String detail
    );

    @DELETE("addresses/{id}")
    Observable<Object> delete(@Path("id") String id);

    @FormUrlEncoded
    @PATCH("addresses/{id}")
    Observable<Address> change(
            @Path("id") String id,
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("summary") String summary,
            @Field("detail") String detail
    );

    @GET("addresses")
    Observable<List<Address>> addresses();
}
