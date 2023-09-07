package com.outmao.xcprojector.api;

import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.network.YYResponseData;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface HttpApiServiceInterface {

   //广告列表
    @GET(HttpApiServiceUrl.SLIDE_LIST)
    Observable<YYResponseData<SlideListData>> slide_list();

    //激活
    @FormUrlEncoded
    @POST(HttpApiServiceUrl.ACCOUNT_ACTIVE)
    Observable<YYResponseData<Object>> account_active(@Field("location")String location,
                                                             @Field("room_sn")String room_sn,
                                                             @Field("repair_name")String repair_name,
                                                             @Field("province")String province,
                                                             @Field("city")String city,
                                                             @Field("area")String area,
                                                             @Field("longitude")String longitude,
                                                             @Field("latitude")String latitude);

 //设备状态

 @GET(HttpApiServiceUrl.ACCOUNT_STATUS)
 Observable<YYResponseData<AccountStatusData>> account_status();

 //设置服务密码
 @FormUrlEncoded
 @POST(HttpApiServiceUrl.ACCOUNT_PWD)
 Observable<YYResponseData<Object>> account_pwd(@Field("password")String password);

 //设置服务密码
 @FormUrlEncoded
 @POST(HttpApiServiceUrl.ACCOUNT_CHECK_PWD)
 Observable<YYResponseData<Object>> account_check_pwd(@Field("password")String password);

}
