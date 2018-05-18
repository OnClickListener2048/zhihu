package com.example.fx50j.zhihu.bean;

import java.util.List;

public class LongDiscuss {

    private List<CommentsBean> comments;

    public List<CommentsBean> getComments() {
        return comments;
    }

    public void setComments(List<CommentsBean> comments) {
        this.comments = comments;
    }

    public static class CommentsBean {
        /**
         * author : 呆若木鸡
         * content : 有了腾讯这种体量巨大的大厂，可以规范中国游戏产业。就像上世纪的育碧以及ea一样，规范化中国游戏市场。只能说腾讯上位的过程吃相太难看，上位之后做的很多事也不合人胃口。不过一个大佬的存在，对于现在游戏业发展是有一定必要的。
         * avatar : http://pic1.zhimg.com/v2-dd0f98b20974cbe06c4edc05e8a626b8_im.jpg
         * time : 1526627922
         * reply_to : {"content":"\u2026\u2026为什么把腾讯想得那么重要，不可理喻。魔兽世界不是腾讯的，试问，这种良心网游乱了吗？依我看，少些腾讯和九游，游戏业界会更好！","status":0,"id":31709948,"author":"开心与你常相伴"}
         * id : 31709999
         * likes : 0
         */

        private String author;
        private String content;
        private String avatar;
        private int time;
        private ReplyToBean reply_to;
        private int id;
        private int likes;

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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public ReplyToBean getReply_to() {
            return reply_to;
        }

        public void setReply_to(ReplyToBean reply_to) {
            this.reply_to = reply_to;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public static class ReplyToBean {
            /**
             * content : ……为什么把腾讯想得那么重要，不可理喻。魔兽世界不是腾讯的，试问，这种良心网游乱了吗？依我看，少些腾讯和九游，游戏业界会更好！
             * status : 0
             * id : 31709948
             * author : 开心与你常相伴
             */

            private String content;
            private int status;
            private int id;
            private String author;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }
        }
    }
}
