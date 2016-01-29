package com.graygrass.healthylife;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by 橘沐 on 2016/1/18.
 */
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String loginType = getIntent().getStringExtra("loginType");
        System.out.println("loginType:　　＞"+loginType);
    }
}
