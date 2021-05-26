package com.topolaris.wmslite.utils;

import com.topolaris.wmslite.model.goods.Goods;

import java.util.ArrayList;

/**
 * @author toPolaris
 * description TODO
 * @date 2021/5/22 18:11
 */
public class Test {
    public static ArrayList<Goods> goods = new ArrayList<>();

    static {
        goods.add(new Goods(1, "NA", "L1", 10, "M1"));
        goods.add(new Goods(2, "NB", "L2", 20, "M2"));
        goods.add(new Goods(3, "NC", "L3", 30, "M3"));
        goods.add(new Goods(4, "ND", "L1", 10, "M1"));
        goods.add(new Goods(5, "NE", "L2", 20, "M2"));
        goods.add(new Goods(6, "NF", "L3", 30, "M3"));
        goods.add(new Goods(7, "NG", "L1", 10, "M1"));
        goods.add(new Goods(8, "NH", "L2", 20, "M2"));
        goods.add(new Goods(9, "NI", "L3", 30, "M3"));
        goods.add(new Goods(10, "NJ", "L1", 10, "M1"));
        goods.add(new Goods(11, "NK", "L2", 20, "M2"));
        goods.add(new Goods(12, "NL", "L3", 30, "M3"));
    }
}
