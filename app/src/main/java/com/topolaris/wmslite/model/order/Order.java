package com.topolaris.wmslite.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.topolaris.wmslite.model.base.BaseEntity;
import com.topolaris.wmslite.model.goods.Goods;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Liangyong Ni
 * description 进货单实体类
 * @date 2021/6/2 14:09
 */
public class Order extends BaseEntity implements Parcelable {
    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    private long id;
    private long goodsId;
    private long number;
    private boolean executed;
    private String date;
    private boolean revoked;
    /**
     * 表示订单状态，通过setState方法自动设置
     * 状态：0 -- 完成
     * 1 -- 等待
     * 2 -- 撤销
     * 3 -- 缺货
     */
    private int state = 1;

    public Order() {
    }

    protected Order(Parcel in) {
        id = in.readLong();
        goodsId = in.readLong();
        number = in.readLong();
        executed = in.readByte() != 0;
        date = in.readString();
        revoked = in.readByte() != 0;
        state = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(goodsId);
        dest.writeLong(number);
        dest.writeByte((byte) (executed ? 1 : 0));
        dest.writeString(date);
        dest.writeByte((byte) (revoked ? 1 : 0));
        dest.writeInt(state);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setState(OrderType type, Goods goods) {
        if (goods == null) {
            state = 1;
            return;
        }
        if (isExecuted()) {
            // 订单已经被审核员处理
            if (isRevoked()) {
                // 订单被审核员撤销——撤销状态
                state = 2;
            } else {
                // 订单被审核员确认——完成状态
                state = 0;
            }
        } else {
            // 订单未被审核员处理
            switch (type) {
                case SHIPMENT:
                    // 出货单
                    if (number > goods.getInventory()) {
                        // 出货数量大于库存——缺货状态
                        state = 3;
                    } else {
                        // 出货数量不大于库存——等待状态
                        state = 1;
                    }
                    break;
                case PURCHASE:
                case SHORTAGE:
                    // 进货单和缺货登记——等待状态
                    state = 1;
                    break;
                default:
                    break;
            }
        }
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

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public boolean isExecuted() {
        return executed;
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

    @NotNull
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", number=" + number +
                ", executed=" + executed +
                ", date='" + date + '\'' +
                ", revoked=" + revoked +
                '}';
    }

    public int getState() {
        return state;
    }

    public String getStateString() {
        String[] states = {"完成", "等待", "撤销", "缺货"};
        return states[state];
    }
}
