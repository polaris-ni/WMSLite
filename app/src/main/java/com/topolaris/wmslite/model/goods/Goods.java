package com.topolaris.wmslite.model.goods;

import android.os.Parcel;
import android.os.Parcelable;

import com.topolaris.wmslite.model.base.BaseEntity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Liangyong Ni
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
    private long index = 0;
    private String name = "Goods Name";
    private String location = "Goods Location";
    private long inventory = 0;
    private String manufacturer = "Goods Manufacturer";
    private long sold = 0;

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
        dest.writeLong(sold);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getSold() {
        return sold;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }

    @Override
    public void convertFromMap(HashMap<String, String> map) {
        index = Long.parseLong(Objects.requireNonNull(map.getOrDefault("index", "0")));
        name = map.getOrDefault("name", "");
        location = map.getOrDefault("location", "");
        inventory = Long.parseLong(Objects.requireNonNull(map.getOrDefault("inventory", "0")));
        sold = Long.parseLong(Objects.requireNonNull(map.getOrDefault("sold", "0")));
        manufacturer = map.getOrDefault("manufacturer", "");
    }

    @Override
    public String getSql() {
        return "(" + index + ", \"" + name + "\", \"" + location + "\", " + inventory + ", \"" + manufacturer + "\")";
    }

    public long getIndex() {
        return index;
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @NotNull
    @Override
    public String toString() {
        return "Goods{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", inventory=" + inventory +
                ", manufacturer='" + manufacturer + '\'' +
                ", sold=" + sold +
                '}';
    }
}
