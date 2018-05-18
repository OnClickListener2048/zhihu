package com.example.fx50j.zhihu.bean;

import java.util.List;

public class Discuss {
    public Discuss(List<DiscussContent> comments) {
        this.comments = comments;
    }

    private List<DiscussContent> comments;

    public List<DiscussContent> getComments() {
        return comments;
    }

    public void setComments(List<DiscussContent> comments) {
        this.comments = comments;
    }

}
