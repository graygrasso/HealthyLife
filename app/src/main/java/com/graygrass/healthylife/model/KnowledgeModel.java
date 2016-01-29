package com.graygrass.healthylife.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 橘沐 on 2015/12/16.
 */
public class KnowledgeModel {
    private int total;
    private List<Tngou> list;

    public class Tngou implements Serializable{
        private int count;//访问次数
        private String description;//描述
        private int fcount;//收藏数
        private int id;
        private String img;
        private String keywords;
        private int loreclass;
        private String message;
        private int rcount;//评论数
        private long time;
        private String title;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getFcount() {
            return fcount;
        }

        public void setFcount(int fcount) {
            this.fcount = fcount;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public int getLoreclass() {
            return loreclass;
        }

        public void setLoreclass(int loreclass) {
            this.loreclass = loreclass;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getRcount() {
            return rcount;
        }

        public void setRcount(int rcount) {
            this.rcount = rcount;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Tngou> getList() {
        return list;
    }

    public void setList(List<Tngou> list) {
        this.list = list;
    }
}
