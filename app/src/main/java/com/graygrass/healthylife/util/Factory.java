package com.graygrass.healthylife.util;

/**
 * Created by 橘沐 on 2015/12/22.
 */
public class Factory {
    public static Object getInstance(String className) {
        Object obj=null;
        Class cla;
        try {
            cla = Class.forName(className);
            obj=cla.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
