package com.outmao.xcprojector.api.models;

import java.io.Serializable;
import java.util.List;

/*
*
* "id": 2,
				"type": 1,
				"thumbs_txt": ["http://tengdamy.cn/storage/20230907/c9600c95208465f0f6d29a3145c0cf7f.png", "http://tengdamy.cn/storage/20230907/c9600c95208465f0f6d29a3145c0cf7f.png"],
				"video_url_txt": ""
* */
public class SlideInfo implements Serializable {

    private int type;
    private List<String> thumbs_txt;
    private String video_url_txt;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getThumbs_txt() {
        return thumbs_txt;
    }

    public void setThumbs_txt(List<String> thumbs_txt) {
        this.thumbs_txt = thumbs_txt;
    }

    public String getVideo_url_txt() {
        return video_url_txt;
    }

    public void setVideo_url_txt(String video_url_txt) {
        this.video_url_txt = video_url_txt;
    }
}