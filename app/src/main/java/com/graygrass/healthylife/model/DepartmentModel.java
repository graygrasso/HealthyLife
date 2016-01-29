package com.graygrass.healthylife.model;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/23.
 */
public class DepartmentModel {
    private boolean status;
    private List<Tngou> tngou;

    public class Tngou {
        private int id;
        private String name;
        private List<Departments> departments;

        public class Departments {
            private int department;
            private int id;
            private String name;

            public int getDepartment() {
                return department;
            }

            public void setDepartment(int department) {
                this.department = department;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Departments> getDepartments() {
            return departments;
        }

        public void setDepartments(List<Departments> departments) {
            this.departments = departments;
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
