package com.outmao.xcprojector.api.models;

import java.io.Serializable;

/*
*
*  "device_sn": "MSH-CHCH-01",
        "detail_info": {
            "device_sn": "MSH-CHCH-01",
            "location": "天河酒店",
            "room_sn": "304房",
            "repair_name": "张三",
            "close_status": 0 //0=不欠费 1=该设备欠费
*
* */
public class AccountStatusData implements Serializable {

    private String device_sn;

    private AccountStatusInfo detail_info;

    //0 未激活 1 已激活
    private int status;

    public String getDevice_sn() {
        return device_sn;
    }

    public void setDevice_sn(String device_sn) {
        this.device_sn = device_sn;
    }

    public AccountStatusInfo getDetail_info() {
        return detail_info;
    }

    public void setDetail_info(AccountStatusInfo detail_info) {
        this.detail_info = detail_info;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
