package com.example.fx50j.zhihu.bean;

import java.io.Serializable;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/6/23 15:09
 * 描 述 ：广告轮播对象
 * 修订历史 ：
 * ============================================================
 **/
public class TopStories implements Serializable {

    private String image;
    private int id;
    private int ga_prefix;
    private String title;
    private int type;
    private String date;

    public TopStories(String image, int id, int ga_prefix, String title, int type, String date) {
        this.image = image;
        this.id = id;
        this.ga_prefix = ga_prefix;
        this.title = title;
        this.type = type;
        this.date = date;
    }

    public TopStories(String image, int id, int ga_prefix, String title) {
        this.image = image;
        this.id = id;
        this.ga_prefix = ga_prefix;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(int ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
