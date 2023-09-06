package com.outmao.xcprojector.network;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by lgs on 2017/2/8.
 */

public class RxSubscriber<T extends YYResponseData> extends Subscriber<T> implements ResponseListener<T> {

    protected ResponseListener responseListener;
    protected int requestId;

    public RxSubscriber(int requestId, ResponseListener responseListener){
        this.requestId = requestId;
        this.responseListener = responseListener;
    }

    public RxSubscriber(ResponseListener responseListener){
        this.responseListener = responseListener;
    }

    public RxSubscriber(){
        this.responseListener = this;
    }

    @Override
    public void onError(Throwable e) {
        String msg ="网络异常，请检查网络后再试.";
        if (e instanceof SocketTimeoutException){
            msg = "连接超时，请稍后再试.";
        }else if (e instanceof ConnectException){
            String errorMsg = e.getLocalizedMessage();
            msg = "网络异常，请稍后再试.";
        }else if (e instanceof NullPointerException) {
            msg = "网络异常，请稍后再试.";
        }
        Log.e("RxSubscriber",msg+e.getLocalizedMessage(),e);
        if (responseListener != null){
            YYResponseData responseData = new YYResponseData();
            responseData.setCode(500);
            responseData.setMessage(msg);
            responseListener.onFail(responseData);
        }
    }

    @Override
    public void onCompleted() {
        //do nothing here
    }


    /**
     * 对于接口异常状态返回跳转至登录界面的处理
     * */
    @Override
    public void onNext(T t) {
        if (responseListener != null){
            if (t.isSuccess()){
                responseListener.onSuccess(t);
            }else{
                responseListener.onFail(t);
            }
        }
    }

    @Override
    public void onSuccess(T responseData) {
        //需要关心处理结果的话，请重写该方法
    }

    @Override
    public void onFail(T responseData) {
        //
    }


}
