package com.example.suivichantier;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface FileDownloadService {
    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
