package com.nowcoder.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    /**
     * 生成随机字符串
     * @return
     */
    public static String createUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * md5 加密
     * @param key
     * @return
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 获取（转换为） json 字符串
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    /**
     * 方法重载1
     * @param code
     * @param msg
     * @return
     */
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    /**
     * 方法重载2
     * @param code
     * @return
     */
    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    /**
     * 方法重载4，有时候并不需要map来传数据
     * @param code
     * @param o
     * @return
     */
    public static String getJSONString(int code, Object o){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("object", o);
        return json.toJSONString();
    }
}
