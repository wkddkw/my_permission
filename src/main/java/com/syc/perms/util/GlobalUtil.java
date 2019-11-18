package com.syc.perms.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 公用的配置文件处理类
 */
public class GlobalUtil {

    private static Logger LOGGER = Logger.getLogger(GlobalUtil.class);

    /**
     * 文件配置路径
     */
    //public static final String PATHCONFIG = "/resources/conf/global.properties";

    /**
     * 获得配置的值
     */
    public static String getValue(String key) {
        try {
            Properties properties = new Properties();
            InputStream in = GlobalUtil.class.getResourceAsStream("/resources/conf/global.properties");
            properties.load(in);
            in.close();
            return properties.getProperty(key);
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
        return null;
    }

}
