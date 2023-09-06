package com.outmao.xcprojector.network;

/**
 * Created by lgs on 16/12/14.
 */

public interface ResponseListener<T> {
    public void onSuccess(T responseData);
    public void onFail(T responseData);

}
