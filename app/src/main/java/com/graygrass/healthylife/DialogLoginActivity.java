package com.graygrass.healthylife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by 橘沐 on 2016/1/18.
 */
public class DialogLoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
    }

    public void qq_login(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("loginType", "qq");
        startActivity(intent);
    }

    public void weibo_login(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("loginType", "weibo");
        startActivity(intent);
    }
}
