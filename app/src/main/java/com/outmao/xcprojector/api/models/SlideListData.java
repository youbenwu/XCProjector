package com.outmao.xcprojector.api.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class SlideListData implements Serializable {

    private SlideInfo main_slide;

    private SlideListSubSlides sub_slides;

    public SlideInfo getMain_slide() {
        return main_slide;
    }

    public void setMain_slide(SlideInfo main_slide) {
        this.main_slide = main_slide;
    }

    public SlideListSubSlides getSub_slides() {
        return sub_slides;
    }

    public void setSub_slides(SlideListSubSlides sub_slides) {
        this.sub_slides = sub_slides;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public static SlideListData fromJson(String json){
        return new Gson().fromJson(json,SlideListData.class);
    }
}
