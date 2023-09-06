package com.outmao.xcprojector.api;

import com.outmao.xcprojector.api.models.UserInfo;
import com.outmao.xcprojector.network.YYResponseData;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface HttpApiServiceInterface {

    @FormUrlEncoded
    @POST(HttpApiServiceUrl.LOGIN_URL)
    Observable<YYResponseData<UserInfo>> login(@Field("tel") String account, @Field("pwd") String pwd, @Field("imei") String imei);

}
