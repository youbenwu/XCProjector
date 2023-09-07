package com.outmao.xcprojector.api;


import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.network.ObservableHelper;
import com.outmao.xcprojector.network.YYHttpCreator;
import com.outmao.xcprojector.network.YYResponseData;

import retrofit2.http.Field;
import rx.Observable;

public class HttpApiService {

    private static HttpApiService httpApiService;

    public synchronized static HttpApiService getInstance() {
        if (httpApiService == null) {
            httpApiService = new HttpApiService();
        }
        return httpApiService;
    }

    public Observable<YYResponseData<SlideListData>> slide_list() {
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<SlideListData>> result = api.slide_list();
        return ObservableHelper.subscribeOn(result);
    }


    public Observable<YYResponseData<Object>> account_active(String location,
                                                             String room_sn,
                                                             String repair_name,
                                                             String province,
                                                             String city,
                                                             String area,
                                                             String longitude,
                                                             String latitude) {
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<Object>> result = api.account_active(location,room_sn,repair_name,province,city,area,longitude,latitude);
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

}
