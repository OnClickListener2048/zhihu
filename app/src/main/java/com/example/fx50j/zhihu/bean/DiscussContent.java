package com.example.fx50j.zhihu.bean;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/6/28 15:18
 * 描 述 ：评论列表信息对象
 * 修订历史 ：
 * ============================================================
 **/
public class DiscussContent {

    private String author;//作者
    private String content;//评论内容
    private int likes;//点赞
    private long time;//时间
    private String avatar;//图片地址

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    private boolean isLiked = false;

    public DiscussContent(String author, String content, int likes, long time, String avatar,boolean isLiked) {
        this.author = author;
        this.content = content;
        this.likes = likes;
        this.time = time;
        this.avatar = avatar;
        this.isLiked = isLiked;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
