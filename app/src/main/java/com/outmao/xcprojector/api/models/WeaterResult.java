package com.outmao.xcprojector.api.models;
/*
*
* {
	"reason":"查询成功!",
	"result":{
		"city":"广州",
		"realtime":{
			"temperature":"28",
			"humidity":"85",
			"info":"多云",
			"wid":"01",
			"direct":"东南风",
			"power":"2级",
			"aqi":"24"
		},
	},
	"error_code":0
}
*
* */


import com.outmao.xcprojector.network.YYResponseData;

import java.io.Serializable;

public class WeaterResult extends YYResponseData implements Serializable {

    private int error_code;

    private WeaterInfo result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public WeaterInfo getResult() {
        return result;
    }

    public void setResult(WeaterInfo result) {
        this.result = result;
    }
}
