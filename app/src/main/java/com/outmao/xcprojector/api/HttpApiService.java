package com.outmao.xcprojector.api;


import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.api.models.WeaterResult;
import com.outmao.xcprojector.network.ObservableHelper;
import com.outmao.xcprojector.network.YYHttpCreator;
import com.outmao.xcprojector.network.YYResponseData;

import retrofit2.http.Field;
import retrofit2.http.Query;
import rx.Observable;

public class HttpApiService {

    private static HttpApiService httpApiService;

    public synchronized static HttpApiService getInstance() {
        if (httpApiService == null) {
            httpApiService = new HttpApiService();
        }
        return httpApiService;
    }

    public Observable<YYResponseData<SlideListData>> slide_list(int page,int limit) {
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<SlideListData>> result = api.slide_list(page,limit);
        return ObservableHelper.subscribeOn(result);
    }


    public Observable<YYResponseData<Object>> account_active(String location,
                                                             String room_sn,
                                                             String repair_name,
                                                             String province,
                                                             String city,
                                                             String area,
                                                             String longitude,
                                                             String latitude,String device_sn) {
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<Object>> result = api.account_active(location,room_sn,repair_name,province,city,area,longitude,latitude,device_sn);
        return ObservableHelper.subscribeOn(result);
    }

    public Observable<YYResponseData<AccountStatusData>> account_status(){
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<AccountStatusData>> result = api.account_status();
        return ObservableHelper.subscribeOn(result);
    }

    public Observable<YYResponseData<Object>> account_pwd(String password){
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<Object>> result = api.account_pwd(password);
        return ObservableHelper.subscribeOn(result);
    }

    public Observable<YYResponseData<Object>> account_check_pwd(String password){
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<Object>> result = api.account_check_pwd(password);
        return ObservableHelper.subscribeOn(result);
    }


    public Observable<YYResponseData<Object>> account_line(){
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<Object>> result = api.account_line("");
        return ObservableHelper.subscribeOn(result);
    }

    public Observable<YYResponseData<Object>> slide_info(String id){
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<Object>> result = api.slide_info(id);
        return ObservableHelper.subscribeOn(result);
    }

    public Observable<WeaterResult> weatherQuery(String city){
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<WeaterResult> result = api.weatherQuery(city);
        return ObservableHelper.subscribeOn(result);
    }

}
