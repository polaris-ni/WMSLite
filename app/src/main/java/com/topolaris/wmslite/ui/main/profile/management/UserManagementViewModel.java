package com.topolaris.wmslite.ui.main.profile.management;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.repository.local.Cache;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;

import java.util.List;

/**
 * @author Liangyong Ni
 * description 用户管理界面的ViewModel
 * @date 2021/6/8 14:36
 */
public class UserManagementViewModel extends ViewModel {
    private MutableLiveData<List<User>> users;

    public LiveData<List<User>> getUsers() {
        if (users ==null ){
            users = new MutableLiveData<>();
        }
        users.setValue(Cache.getUsersCache());
        return users;
    }

    public void refresh(){
        ThreadPool.EXECUTOR.execute(() -> users.postValue(DatabaseUtil.executeSqlWithResult("select * from wmsusers", User.class)));
    }
}