package com.topolaris.wmslite.model.base;

import java.util.HashMap;

/**
 * @author toPolaris
 * description 实现此接口的类能够从map中获取数据并转换为对象
 * @date 2021/5/19 14:00
 */
public interface MapConvertible {
    /**
     * map转对象函数
     * @param map 需要转换的HashMap
     */
    void convertFromMap(HashMap<String, String> map);
}
