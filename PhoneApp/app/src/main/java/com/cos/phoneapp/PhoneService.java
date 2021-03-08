package com.cos.phoneapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PhoneService {

    @GET("phone")
    Call<CMRespDto<List<Phone>>> findAll();

    @GET("phone/{id}")
    Call<CMRespDto<Phone>> findById(@Path("id") Long id);

    // 스프링에서 @RequestBody로 서버에 데이터를 보내면
    // 안드로이드에서는 @Body 어노테이션을 달아서 보내줘야한다.
    @POST("phone")
    Call<CMRespDto<Phone>> save(@Body Phone phone);

    @PUT("phone/{id}")
    Call<CMRespDto<Phone>> update(@Body Phone phone, @Path("id") Long id);

    @DELETE("phone/{id}")
    Call<Void> delete(@Path("id") Long id);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://61.79.41.46:8080/") // 끝에 / 적어주자.
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
