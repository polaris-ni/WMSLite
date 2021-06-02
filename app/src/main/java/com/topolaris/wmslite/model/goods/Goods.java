package com.topolaris.wmslite.model.goods;

import android.os.Parcel;
import android.os.Parcelable;

import com.topolaris.wmslite.model.base.BaseEntity;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author toPolaris
 * description 商品类
 * @date 2021/5/22 17:48
 */
public class Goods extends BaseEntity implements Parcelable {
    public static final Creator<Goods> CREATOR = new Creator<Goods>() {
        @Override
        public Goods createFromParcel(Parcel in) {
            return new Goods(in);
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };
    private long index;
    private String name;
    private String location;
    private long inventory;
    private String manufacturer;
    private int sold;

    public Goods() {
    }

    public Goods(long index, String name, String location, int inventory, String manufacturer) {
        this.index = index;
        this.name = name;
        this.location = location;
        this.inventory = inventory;
        this.manufacturer = manufacturer;
    }

    protected Goods(Parcel in) {
        index = in.readLong();
        name = in.readString();
        location = in.readString();
        inventory = in.readInt();
        manufacturer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(index);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeLong(inventory);
        dest.writeString(manufacturer);
        dest.writeInt(sold);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    @Override
    public void convertFromMap(HashMap<String, String> map) {
        index = Long.parseLong(Objects.requireNonNull(map.getOrDefault("index", "0")));
        name = map.getOrDefault("name", "");
        location = map.getOrDefault("location", "");
        inventory = Long.parseLong(Objects.requireNonNull(map.getOrDefault("inventory", "0")));
        manufacturer = map.getOrDefault("manufacturer", "");
    }

    @Override
    public String getSql() {
        return "(" + index + ", \"" + name + "\", \"" + location + "\", " + inventory + ", \"" + manufacturer + "\")";
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getInventory() {
        return inventory;
    }

    public void setInventory(long inventory) {
        this.inventory = inventory;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
