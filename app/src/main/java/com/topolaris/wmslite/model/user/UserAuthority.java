package com.topolaris.wmslite.model.user;

/**
 * @author toPolaris
 * description 用户权限声明
 * @date 2021/5/19 13:58
 */
public class UserAuthority {
    /**
     * 管理员权限，伪最高权限，能够查看货物数据，注册账号需要得到管理员同意
     */
    public static final int ADMINISTRATOR = 5;
    /**
     * 采购员权限，可以查看货物数据，并添加货物的量
     */
    public static final int PURCHASER = 4;
    /**
     * 销售员权限，可以查看货物数据，并向外销售货物
     */
    public static final int SHIPMENT = 3;
    /**
     * 审核员权限，可以查看货物数据，并对订单进行审核
     */
    public static final int CHECKER = 2;
    /**
     * 普通权限，仅可以查看货物数据
     */
    public static final int COMMON = 1;
}
