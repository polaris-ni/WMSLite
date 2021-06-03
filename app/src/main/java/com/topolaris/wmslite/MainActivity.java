package com.topolaris.wmslite;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.topolaris.wmslite.databinding.ActivityMainBinding;
import com.topolaris.wmslite.model.event.MessageEvent;
import com.topolaris.wmslite.model.event.MessageType;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;
import com.topolaris.wmslite.utils.WMSLiteApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author toPolaris
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        com.topolaris.wmslite.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.GRAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        // 状态设置为下线
        User account = WMSLiteApplication.getAccount();
        if (account != null) {
            String sql = "update wmsusers set online = 0 where uName = \"" + account.getName() + "\" and uPassword = \"" + account.getPassword() + "\";";
            ThreadPool.executor.execute(() -> DatabaseUtil.executeSqlWithoutResult(sql));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainMessageHandler(MessageEvent messageEvent) {
        if (messageEvent.getMessageType() == MessageType.MAIN_TOAST_MAKER) {
            Toast.makeText(WMSLiteApplication.context, messageEvent.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

}