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

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public int getCode() {
        return code;
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
