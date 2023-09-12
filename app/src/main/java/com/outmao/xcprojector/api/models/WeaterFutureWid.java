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
public class WeaterFutureWid {

    private String day;

    private String night;


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getNight() {
        return night;
    }

    public void setNight(String night) {
        this.night = night;
    }
}
