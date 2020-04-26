package com.yss.id.core.constans;

/**
 * 常量定义
 */
public class Constants {

    //获取下一缓存阈值为10，即消耗10%时会触发
    public static final int ID_THRESHOLDVALUE = 10;

    public static final String SEGEMNT_URL = "{0}/id/segment/{1}";

    public static final String SEGEMNT_FIEXD_URL = "{0}/id/segment/{1}/{2}";

    public static final String SEGEMNT_INIT_URL = "{0}/id/initSegment/{1}";

    public static final String SWONFLAKE_URL = "{0}/id/snowflake/{1}";

    public static final int THREAD_CORE = 2;

}