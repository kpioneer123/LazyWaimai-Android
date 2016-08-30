package net.comet.lazyorder.network.service;

import net.comet.lazyorder.model.bean.Business;
import net.comet.lazyorder.model.bean.ProductCategory;
import net.comet.lazyorder.model.bean.ResultsPage;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface BusinessService {

    @GET("businesses?category=1")
    Observable<ResultsPage<Business>> restaurants(@Query("page") int page, @Query("size") int size);

    @GET("businesses?category=2")
    Call<ResultsPage<Business>> stores(@Query("page") int page);

    @GET("businesses?category=3")
    Call<ResultsPage<Business>> drinks(@Query("page") int page);

    @GET("businesses/{bid}/products")
    Observable<List<ProductCategory>> products(@Path("bid") String businessId);
}
