package com.topolaris.wmslite.model.base;

import java.util.HashMap;

/**
 * @author Liangyong Ni
 * description 实体类基类，所有实体类需要继承此类
 * @date 2021/5/19 15:38
 */
public abstract class BaseEntity {
    /**
     * map转对象函数
     *
     * @param map 需要转换的HashMap
     */
    public abstract void convertFromMap(HashMap<String, String> map);

    /**
     * 获取类对应的Sql语句，例如"("admin", "test", 123)"
     *
     * @return 字符串类型的语句
     */
    public abstract String getSql();
}
