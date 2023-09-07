package com.outmao.xcprojector.api.models;

import java.io.Serializable;


/*
*
*
            "device_sn": "MSH-CHCH-01",
            "location": "天河酒店",
            "room_sn": "304房",
            "repair_name": "张三",
            "close_status": 0 //0=不欠费 1=该设备欠费
*
* */
public class AccountStatusInfo implements Serializable {

    private String device_sn;

    private String location;
    private String room_sn;
    private String repair_name;
    private int close_status;

    public String getDevice_sn() {
        return device_sn;
    }

    public void setDevice_sn(String device_sn) {
        this.device_sn = device_sn;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoom_sn() {
        return room_sn;
    }

    public void setRoom_sn(String room_sn) {
        this.room_sn = room_sn;
    }

    public String getRepair_name() {
        return repair_name;
    }

    public void setRepair_name(String repair_name) {
        this.repair_name = repair_name;
    }

    public int getClose_status() {
        return close_status;
    }

    public void setClose_status(int close_status) {
        this.close_status = close_status;
    }
}
