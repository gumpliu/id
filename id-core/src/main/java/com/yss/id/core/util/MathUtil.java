package com.yss.id.core.util;

import com.yss.id.core.exception.IdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 算数功能类
 *
 * @Author gumpLiu
 * @Date 2020-04-07
 * @Version V1.0
 **/
public class MathUtil {

    private static Map<String, Long> maxValueMap = new ConcurrentHashMap<String, Long>();


    /**
     * 获取位数为length的最大值
     * @param length
     * @return
     */
    public static long maxValue(String bizTag, int length){

        String maxValueKey = bizTag + length;
        Long maxValue =  maxValueMap.get(maxValueKey);

        if(maxValue == null){

            StringBuffer initValue = new StringBuffer();
            for(int i = 0; i < length; i++){
                initValue.append(9);
            }
            maxValueMap.put(maxValueKey, Long.parseLong(initValue.toString()));
            maxValue = maxValueMap.get(maxValueKey);
        }

        return maxValue;
    }

    /**
     * val1 与 val2 比较大小
     * @param val1
     * @param val2
     * @return val1 >= val2  return true
     *         val1 < val2 return false
     */
    public static boolean greaterThanEqual (long val1, long val2){

        return BigDecimal.valueOf(val1).compareTo(BigDecimal.valueOf(val2)) >= 0;
    }

    /**
     * 拼接0
     * @param id
     * @param length
     * @return
     */
    public static String appendZero(String id, int length){

        if(id.length() == length){
            return id;
        }
        int differenceValue = length - id.length();
        if(differenceValue > 0 ){
            StringBuffer initValue = new StringBuffer();
            for(int i = 0; i < differenceValue; i++ ){
                initValue.append("0");
            }
            return initValue.toString() + id ;
        }else{
            throw new IdException("id length is error !!");
        }
    }

}
