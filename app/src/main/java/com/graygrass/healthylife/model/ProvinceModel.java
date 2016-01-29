package com.graygrass.healthylife.model;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/23.
 */
public class ProvinceModel {
    private boolean status;
    private List<Tngou> tngou;

    public class Tngou {
        private int id;
        private int level;
        private String province;
        private String region;
        private float x;
        private float y;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
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
