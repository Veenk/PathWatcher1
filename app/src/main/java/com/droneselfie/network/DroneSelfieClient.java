package com.droneselfie.network;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


import static android.content.ContentValues.TAG;

public class DroneSelfieClient {
    private String TAG = this.getClass().getSimpleName();
    private String droneSelfieBase;
    private static DroneSelfieClient instance;
    private static NetworkAPI api;
    private static List<String> list = new ArrayList<>();

    public DroneSelfieClient(String URL){
        this.droneSelfieBase = URL;
        Gson gson = new GsonBuilder().create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(droneSelfieBase)
                .client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(NetworkAPI.class);
    }

    public static DroneSelfieClient getInstance(String URL){
        if (instance == null){
            instance = new DroneSelfieClient(URL);
        }
        return instance;
    }

    public List<String> getFileListFromServer(){
        list.clear();
        Observable<ResponseBody> listOfFiles = api.get_list_of_files();
        listOfFiles.observeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        list.add(responseBody.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return list;
    }

    public void sendFile(File mediafile){
        MultipartBody.Part mediaToUpload = MultipartBody.Part.createFormData("file",
                mediafile.getName(),
                RequestBody.create(MediaType.parse("application/octet-stream"), mediafile));

        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        Observable<UploadFileTestResponse> mediaFileObservable = api
                .upload(description,mediaToUpload);
        mediaFileObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UploadFileTestResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UploadFileTestResponse uploadFileTestResponse) {
                        Log.d(TAG, "On Next: ");
                        System.out.println(uploadFileTestResponse.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private List<String> handleResults(ResponseBody responseBody){
        List<String> list = new ArrayList<>();
        list.add(responseBody.toString());
        Log.d(TAG, "handleResults: "+ responseBody.toString());
        return list;
    }

    private void handleErr(Throwable t){
        Log.d(TAG, "handleErr: ");
    }


}


