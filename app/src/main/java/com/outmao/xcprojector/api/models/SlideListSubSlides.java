package com.outmao.xcprojector.api.models;

import java.io.Serializable;
import java.util.List;

/*
*
* "current_page": 1,
			"last_page": 1,
			"total": 0,
			"page_size": 20,
			"list": []
* */
public class SlideListSubSlides implements Serializable {

    private int page;
    private int last_page;
    private int total;
    private int page_size;
    private List<SlideInfo> list;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public List<SlideInfo> getList() {
        return list;
    }

    public void setList(List<SlideInfo> list) {
        this.list = list;
    }
}
