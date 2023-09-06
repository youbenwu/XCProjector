package com.outmao.xcprojector.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class YYResponseData<T> {


    private T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private int code;
    private boolean success;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        //  return success || code == 0 || code == 200;
        return success || code == 200;
    }

    public String islogin() {
        String str = "";
        if (message.substring(0, 0).equals("4")) {
            return str = "不可登录";
        } else {
            return str = "可登录";
        }
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public boolean isNeedLogin() {
        return code >= 400 && code < 500;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public static <E> YYResponseData<E> parseFromJson(String jsonString, TypeToken typeToken) {
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            Gson gson = new Gson();
            YYResponseData<E> data = gson.fromJson(jsonString, typeToken.getType());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
