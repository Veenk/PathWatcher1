package com.droneselfie.network;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

//public interface NetworkAPI {
//    @PUT("")
//    Observable<ResponseBody> uploadFile();
//}
public interface NetworkAPI {
    @Multipart
    @POST("/upload-file-test")
    Observable<UploadFileTestResponse> upload(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @POST("/get-list-of-files")
    Observable<ResponseBody> get_list_of_files();

}