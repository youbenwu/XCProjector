package com.outmao.xcprojector.network;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lgs on 2017/1/19.
 */

public class ObservableHelper {

    /**
     * 在主线程设置订阅
     * @param observable
     * @param <T>
     * @return
     */
    public static <T> Observable subscribeOn(Observable<T> observable){
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
