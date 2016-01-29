package com.graygrass.healthylife.model;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/18.
 */
public class BookShowModel {
    private String author;
    private int bookclass;
    private String count;
    private int fcount;
    private long id;
    private String img;
    private List<T> list;

    public class T {
        private int book;
        private long id;
        private String message;
        private int seq;
        private String title;

        public int getBook() {
            return book;
        }

        public void setBook(int book) {
            this.book = book;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
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

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
