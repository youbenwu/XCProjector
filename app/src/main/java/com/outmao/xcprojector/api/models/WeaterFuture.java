package com.outmao.xcprojector.api.models;
/*
*
* "date":"2023-09-12",
				"temperature":"25\/31℃",
				"weather":"小雨",
				"wid":{
					"day":"07",
					"night":"07"
				},
				"direct":"持续无风向"
*
* */


public class WeaterFuture {

    private String date;

    private String temperature;

    private String weatherweatherweather;

    private String direct;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeatherweatherweather() {
        return weatherweatherweather;
    }

    public void setWeatherweatherweather(String weatherweatherweather) {
        this.weatherweatherweather = weatherweatherweather;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }
}
