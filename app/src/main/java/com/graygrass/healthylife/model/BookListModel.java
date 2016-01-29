package com.graygrass.healthylife.model;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/18.
 * 健康图书列表
 */
public class BookListModel {
    private List<T> list;

    public class T {
        private String author;
        private int bookclass;
        private String count;
        private int fcount;
        private long id;
        private String img;
        private String name;
        private int rcount;
        private String summary;
        private long time;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getBookclass() {
            return bookclass;
        }

        public void setBookclass(int bookclass) {
            this.bookclass = bookclass;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public int getFcount() {
            return fcount;
        }

        public void setFcount(int fcount) {
            this.fcount = fcount;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRcount() {
            return rcount;
        }

        public void setRcount(int rcount) {
            this.rcount = rcount;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
