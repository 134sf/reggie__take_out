package com.gyh.reggie.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前登录用户id
 * 每个线程单独保存自己的副本
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
