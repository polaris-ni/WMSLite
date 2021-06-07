package com.topolaris.wmslite.model.base;

/**
 * @author Liangyong Ni
 * description 实现此接口的类需要提供Sql语句的获取方法
 * @date 2021/5/19 14:10
 */
public interface SqlGetter {
    /**
     * 获取类对应的Sql语句，例如"("admin", "test", 123)"
     * @return 字符串类型的语句
     */
    String getSql();
}
