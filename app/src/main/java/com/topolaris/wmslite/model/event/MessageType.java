package com.topolaris.wmslite.model.event;

/**
 * @author toPolaris
 * description TODO
 * @date 2021/5/22 18:21
 */
public enum MessageType {
    /**
     * 消息类型，由“处理者+处理动作”构成 HANDLER_ACTION
     * 例如，MAIN_TOAST_MAKER表示交由MainActivity处理弹出Toast请求
     */
    /**
     * 弹出一个Toast
     */
    MAIN_TOAST_MAKER,
    /**
     * 修改侧边栏账户显示
     */
    MAIN_ACCOUNT_VIEW_MODIFIER,
    /**
     * 从Login导航悔Goods界面
     */
    LOGIN_NAVIGATE_BACK,
    /**
     * 商品界面子项被点击
     */
    GOODS_ITEM_CLICK,
    /**
     * 数据刷新完毕
     */
    GOODS_DATA_UPDATED
}
