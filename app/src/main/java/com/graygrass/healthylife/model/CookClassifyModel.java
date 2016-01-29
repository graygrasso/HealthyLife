package com.graygrass.healthylife.model;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/29.
 */
public class CookClassifyModel {
    private boolean status;
    private List<Tngou> tngou;

    public class Tngou {
        private int cookclass;
        private String name;
        private int id;

        public int getCookclass() {
            return cookclass;
        }

        public void setCookclass(int cookclass) {
            this.cookclass = cookclass;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
