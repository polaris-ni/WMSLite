package com.topolaris.wmslite.model.user;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.topolaris.wmslite.model.base.BaseEntity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Liangyong Ni
 * description 用户实体类
 * @date 2021/5/19 13:58
 */
public class User extends BaseEntity {
    /**
     * 用户名
     */
    private String name = "";
    /**
     * 用户密码
     */
    private String password = "";
    /**
     * 用户ID，36位字符串，用户的唯一标识，找回密码的唯一方式
     */
    private String uid = "";
    /**
     * 用户所属等级
     */
    private int authority = UserAuthority.COMMON;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, String uid) {
        this.name = name;
        this.password = password;
        this.uid = uid;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    @NotNull
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", uid='" + uid + '\'' +
                ", authority=" + authority +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return name.equals(user.name) && (password.equals(user.password) || password.equals(user.uid));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void convertFromMap(@NonNull HashMap<String, String> map) {
        name = map.getOrDefault("uName", "");
        password = map.getOrDefault("uPassword", "");
        uid = map.getOrDefault("uid", "");
        authority = Integer.parseInt(Objects.requireNonNull(map.getOrDefault("authority", "2")));
    }

    @Override
    public String getSql() {
        return "(\"" + name + "\", \"" + password + "\", \"" + uid + "\", " + authority + ")";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthorityString() {
        switch (authority) {
            case UserAuthority.ADMINISTRATOR:
                return "管理员，你好！";
            case UserAuthority.PURCHASER:
                return "采购员，你好！";
            case UserAuthority.SHIPMENT:
                return "销售员，你好！";
            case UserAuthority.CHECKER:
                return "审核员，你好！";
            default:
                return "陌生人，你好！";
        }
    }
}
