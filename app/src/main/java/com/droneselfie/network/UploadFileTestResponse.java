package com.droneselfie.network;

import com.google.gson.annotations.SerializedName;

public class UploadFileTestResponse {
    @SerializedName("detail")
    String name;
    @SerializedName("loc")
    String img;
    @SerializedName("msg")
    String url;
    @SerializedName("type")
    String type;
}
