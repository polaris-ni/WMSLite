package com.topolaris.wmslite.model.order;

/**
 * @author Liangyong Ni
 * description 订单类型
 * @date 2021/6/9 19:53
 */
public enum OrderType {
    /**
     * 采购订单
     */
    PURCHASE,
    /**
     * 销售订单
     */
    SHIPMENT,
    /**
     * 缺货登记
     */
    SHORTAGE
}
