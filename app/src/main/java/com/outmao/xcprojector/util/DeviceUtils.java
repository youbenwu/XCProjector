package com.outmao.xcprojector.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.config.MyApplication;

import java.lang.reflect.Method;
import java.util.UUID;

public class DeviceUtils {

    public static String getUUID(){
        String imei=SharepreferencesUtils.getShareInstance().getString(AppConfig.IMEI_KEY);
        if(imei==null) {
            imei = getIMEI();
            if (imei == null || imei.length() == 0) {
                imei = getAndroidId();
            }
            if (imei == null || imei.length() == 0) {
                imei = UUID.randomUUID().toString();
            }
            SharepreferencesUtils.getShareInstance().putString(AppConfig.IMEI_KEY,imei);
        }
        return imei;
    }

    public static String getIMEI() {
        return ((TelephonyManager) MyApplication.getAppContext().getSystemService(
                Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static String getAndroidId() {
        return android.provider.Settings.Secure.getString(
                MyApplication.getAppContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    public static String getSimSerialNumber() {
        return ((TelephonyManager) MyApplication.getAppContext().getSystemService(
                Context.TELEPHONY_SERVICE)).getSimSerialNumber();
    }

    public static String getSerialNumber1() {
        return android.os.Build.SERIAL;
    }


    /**
     * getSerialNumber
     * @return result is same to getSerialNumber1()
     */
    public static String getSerialNumber(){
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }



}
