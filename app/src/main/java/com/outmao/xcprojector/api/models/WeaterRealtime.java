package com.outmao.xcprojector.api.models;


import java.io.Serializable;

/*
*"temperature":"28",
			"humidity":"85",
			"info":"多云",
			"wid":"01",
			"direct":"东南风",
			"power":"2级",
			"aqi":"24"
**/
public class WeaterRealtime implements Serializable {
    private String temperature;
    private String humidity;
    private String info;
    private String wid;
    private String direct;
    private String power;
    private String aqi;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getAqiString(){
        int q=Integer.parseInt(aqi);
        if(q<=50){
            return "优";
        }
        if(q<100){
            return "良";
        }
        if(q<150){
            return "轻度污染";
        }
        if(q<200){
            return "中度污染";
        }
        if(q<300){
            return "重度污染";
        }
        if(q>300){
            return "严重污染";
        }
        return "优";
    }

}


