package com.graygrass.healthylife.model;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/18.
 * 健康图书分类
 */
public class BookClassifyModel {
    private boolean status;
    private List<Tngou> tngou;

    public class Tngou {
        private String description;
        private int id;
        private String keywords;
        private String name;
        private int seq;
        private String title;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Tngou> getTngou() {
        return tngou;
    }

    public void setTngou(List<Tngou> tngou) {
        this.tngou = tngou;
    }
}
