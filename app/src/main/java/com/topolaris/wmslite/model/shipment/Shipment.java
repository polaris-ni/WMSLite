package com.topolaris.wmslite.model.shipment;

import com.topolaris.wmslite.model.base.BaseEntity;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author toPolaris
 * description 销售记录实体类
 * @date 2021/6/2 13:51
 */
public class Shipment extends BaseEntity {
    private long id;
    private long goodsId;
    private long number;
    private boolean executed;
    private String date;
    private boolean revoked;

    public Shipment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    @Override
    public void convertFromMap(HashMap<String, String> map) {
        id = Long.parseLong(Objects.requireNonNull(map.getOrDefault("id", "0")));
        goodsId = Long.parseLong(Objects.requireNonNull(map.getOrDefault("goods_id", "0")));
        number = Long.parseLong(Objects.requireNonNull(map.getOrDefault("number", "0")));
        executed = Integer.parseInt(Objects.requireNonNull(map.getOrDefault("executed", "0"))) == 1;
        date = Objects.requireNonNull(map.getOrDefault("date", "1970-01-01"));
        revoked = Integer.parseInt(Objects.requireNonNull(map.getOrDefault("revoked", "0"))) == 1;
    }

    @Override
    public String getSql() {
        return "(" + id + ", " + goodsId + ", " + number + ", " + (executed ? 1 : 0) + ", \"" + date + "\", " + (revoked ? 1 : 0) + ")";
    }
}
