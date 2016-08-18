package com.graygrass.healthylife.layout;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.graygrass.healthylife.R;
import com.graygrass.healthylife.adapter.CarouselAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 橘沐 on 2016/3/8.
 * 图片轮播
 */
public class ImageCarousel extends FrameLayout implements OnPageChangeListener {
    private Context context;
    private ArrayList<ImageView> imgViewList;
    private String[] imgUrls;
    private List<ImageView> dotViewList;
    private ViewPager viewPager;
    private int[] imgs;

    private ImageHandler handler = new ImageHandler(new WeakReference<ImageCarousel>(this));

    public ImageCarousel(Context context) {
        this(context, null);
    }

    public ImageCarousel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageCarousel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        //测试：本地图片
        imgs = new int[]{R.drawable.food1, R.drawable.food2,R.drawable.food3,R.drawable.food4};
        LayoutInflater.from(context).inflate(R.layout.layout_imagecarousel,this,true);
        LinearLayout dotLayout = (LinearLayout) findViewById(R.id.layout_dot);
        imgViewList = new ArrayList<>();
        dotViewList = new ArrayList<>();
        for (int i=0;i<imgs.length;i++) {
            //轮播图片
            ImageView imageView = new ImageView(context);
//            imageView.setTag(imgUrls[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);  //图片横向拉伸
            imageView.setTag(i);
            imageView.setImageResource(imgs[i]);
            imgViewList.add(imageView);
            //点
            ImageView dotView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 4;
            params.leftMargin = 4;
            dotLayout.addView(dotView,params);
            dotView.setImageResource(R.drawable.ad_point_normal);
            dotViewList.add(dotView);
        }
        viewPager = (ViewPager) findViewById(R.id.viewPager_carousel);
        viewPager.setAdapter(new CarouselAdapter(imgViewList));
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2);//默认在中间，使用户看不到边界
        //开始轮播效果
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, position, 0));
        //设置小圆点指示器
        position %= imgViewList.size();
        if (position < 0) {
            position = imgViewList.size() + position;
        }
        ImageView img = imgViewList.get(position);
        int tag = (int) img.getTag();  //其实position和tag相等
        for (int i = 0; i < dotViewList.size(); i++) {
            if (tag == i) {
                (dotViewList.get(tag)).setBackgroundResource(R.drawable.ad_point_pressed);
            } else {
                (dotViewList.get(i)).setBackgroundResource(R.drawable.ad_point_normal);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                break;
        }

    }



    private static class ImageHandler extends Handler {

        /**
         * 请求更新显示的View
         */
        protected static final int MSG_UPDATE_IMAGE = 1;

        /**
         * 请求暂停轮播
         */
        protected static final int MSG_KEEP_SILENT = 2;

        /**
         * 请求恢复轮播
         */
        protected static final int MSG_BREAK_SILENT = 3;
        /**
         * 记录最新的页号
         */
        //记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
        //例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
        //应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
        protected static final int MSG_PAGE_CHANGED = 4;

        /**
         * 轮播间隔时间
         */
        protected static final long MSG_DELAY = 3000;

        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<ImageCarousel> weakReference;
        private int currentItem = 0;

        public ImageHandler(WeakReference<ImageCarousel> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Log.d("handler", "receive message " + msg.what);
            ImageCarousel ic = weakReference.get();
            if (ic == null)
                return;//如果activity已经回收，则无需再处理UI了
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题
            if (ic.handler.hasMessages(MSG_UPDATE_IMAGE)&&currentItem!=0) {
                ic.handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    ic.viewPager.setCurrentItem(currentItem);
                    //准备下次播放
                    ic.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    //只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    //请求恢复轮播
                    ic.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确
                    currentItem = msg.arg1;
                    Log.d("currentItem",currentItem+"");
                    break;
            }
        }
    }
}
