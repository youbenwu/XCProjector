package com.outmao.xcprojector.api.models;

import java.io.Serializable;

/**
 * "city":"广州",
 * 		"realtime":{
 * 			"temperature":"28",
 * 			"humidity":"85",
 * 			"info":"多云",
 * 			"wid":"01",
 * 			"direct":"东南风",
 * 			"power":"2级",
 * 			"aqi":"24"
 *                },
 *
 * */
public class WeaterInfo implements Serializable {

    private String city;

    private WeaterRealtime realtime;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public WeaterRealtime getRealtime() {
        return realtime;
    }

    public void setRealtime(WeaterRealtime realtime) {
        this.realtime = realtime;
    }
}
