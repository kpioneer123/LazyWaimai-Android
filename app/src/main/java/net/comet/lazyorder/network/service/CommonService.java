package net.comet.lazyorder.network.service;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface CommonService {

    @Multipart
    @POST("files")
    Observable<String> singleFileUpload(@Part MultipartBody.Part part);
}