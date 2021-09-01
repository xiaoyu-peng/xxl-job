package com.xxl.job.admin.core.alarm.WeiUtils;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * 全局配置类
 *
 * @author xxh
 * @version 2021-09-01
 */
@Component
public class Global implements EnvironmentAware {

    private static Environment environment;

    public static String getConfig(String key) {
        return environment.getProperty(key);
    }

    public static String getConfig(String key, String defaultValue) {
        String value = getConfig(key);
        return value != null ? value : defaultValue;
    }

    public static Integer getConfigInteger(String key) {
        return environment.getProperty(key, Integer.class);
    }

    public static Integer getConfigInteger(String key, Integer defaultValue) {
        Integer value = getConfigInteger(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public void setEnvironment(Environment environment) {
        Global.environment = environment;
    }

    /**
     * 当前对象实例
     */
    private static Global global = new Global();

    /**
     * 获取当前对象实例
     */
    public static Global getInstance() {
        return global;
    }

}

