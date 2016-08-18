package com.graygrass.healthylife.layout;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.graygrass.healthylife.R;

/**
 * Created by 橘沐 on 2016/3/15.
 * 自定义的ProgressDialog
 */
public class ProgressDialogCustom extends Dialog {

    private Context context;
    private static ProgressDialogCustom progress;

    public ProgressDialogCustom(Context context) {
        super(context);
        this.context = context;
    }

    public ProgressDialogCustom(Context context, int theme) {
        super(context, theme);
    }

    public static ProgressDialogCustom createDialog(Context context) {
        progress = new ProgressDialogCustom(context, R.style.CustomProgressDialog);
        progress.setContentView(R.layout.progressdialogcustom);
        progress.getWindow().getAttributes().gravity= Gravity.CENTER;
        return progress;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if(progress ==null) {
            return;
        }
        ImageView img = (ImageView) progress.findViewById(R.id.img_progress_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable)img.getBackground();
        animationDrawable.start();
    }

    public ProgressDialogCustom setTitle(String title) {
        return progress;
    }

    public ProgressDialogCustom setMessage(String message) {
        TextView tv_message = (TextView) progress.findViewById(R.id.tv_progress_loading);
        if(tv_message!=null){
            tv_message.setText(message);
        }
        return progress;
    }
}
