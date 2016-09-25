package com.mevv.carousel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by VV on 2016/9/24.
 */

public class CarouselView extends RelativeLayout {

    private final int DEFAULT_AUTO_CAROUSEL_TIMEMS = 2000;//ms
    private final int DEFAULT_HEIGHT = 175;//dp
    private final int DEFAULT_INDICATOR_SPACE_PADDING = 5;//dp
    private final int DEFAULT_INDICATOR_MARGIN_RIGHT = 20;//dp
    private final int DEFAULT_BOTTOM_BAR_HEIGHT = 30;//dp
    private final int DEFAULT_PIC_DESC_MARGIN_LEFT = 15;//dp
    private final int DEFAULT_BOTTOM_BAR_MARGIN_BOTTOM = 0;//dp
    private final int DEFAULT_PIC_DESC_TEXT_SIZE = 8;//sp
    private final int DEFAULT_PIC_DESC_TEXT_COLOR = Color.WHITE;
    private final int DEFAULT_BOTTOM_BAR_BG_COLOR = Color.parseColor("#77000000");

    private int mAutoTime = DEFAULT_AUTO_CAROUSEL_TIMEMS;
    private int mIndicatorSpace = DEFAULT_INDICATOR_SPACE_PADDING;
    private int mIndicatorMarginRight = DEFAULT_INDICATOR_MARGIN_RIGHT;
    private int mBottomBarHeight = DEFAULT_BOTTOM_BAR_HEIGHT;
    private int mPicDescMarginLeft = DEFAULT_PIC_DESC_MARGIN_LEFT;
    private int mBottomBarMarginBottom = DEFAULT_BOTTOM_BAR_MARGIN_BOTTOM;
    private int mPicDescTextSize = DEFAULT_PIC_DESC_TEXT_SIZE;
    private int mPicDescTextColor = DEFAULT_PIC_DESC_TEXT_COLOR;
    private int mBottomBarBGColor = DEFAULT_BOTTOM_BAR_BG_COLOR;


    private List<BannerInfo> mInfoList;
    private ViewPager.OnPageChangeListener mCarouselPagerListener;
    private OnCarouselClickListener mCarouselClickListener;
    private PicInitListener mPicInitListener;
    private int mCurrentIndex;
    private CarouselTask mCarouselTask;
    private CarouselAdapter mCarouselAdapter;
    private ViewPager mCarouselViewPager;
    private LinearLayout mIndicatorContainer;
    private RelativeLayout mBottombarContainer;
    private TextView mPicDescTextView;


    private Context mContext;


    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CarouselView);
        mAutoTime = a.getInt(R.styleable.CarouselView_auto_carousel_timeMS, DEFAULT_AUTO_CAROUSEL_TIMEMS);
        mIndicatorSpace = a.getDimensionPixelSize(R.styleable.CarouselView_indicator_space, dp2px(DEFAULT_INDICATOR_SPACE_PADDING));
        mIndicatorMarginRight = a.getDimensionPixelSize(R.styleable.CarouselView_indicator_margin_right, dp2px(DEFAULT_INDICATOR_MARGIN_RIGHT));
        mBottomBarHeight = a.getDimensionPixelSize(R.styleable.CarouselView_bottom_bar_height, dp2px(DEFAULT_BOTTOM_BAR_HEIGHT));
        mPicDescMarginLeft = a.getDimensionPixelSize(R.styleable.CarouselView_pic_desc_margin_left, dp2px(DEFAULT_PIC_DESC_MARGIN_LEFT));
        mBottomBarMarginBottom = a.getDimensionPixelSize(R.styleable.CarouselView_bottom_bar_margin_bottom, dp2px(DEFAULT_BOTTOM_BAR_MARGIN_BOTTOM));
        mPicDescTextSize = a.getDimensionPixelSize(R.styleable.CarouselView_pic_desc_text_size, sp2px(DEFAULT_PIC_DESC_TEXT_SIZE));
        mPicDescTextColor = a.getColor(R.styleable.CarouselView_pic_desc_text_color, DEFAULT_PIC_DESC_TEXT_COLOR);
        mBottomBarBGColor = a.getColor(R.styleable.CarouselView_bottom_bar_background_color, DEFAULT_BOTTOM_BAR_BG_COLOR);
        a.recycle();
        initUI();
    }

    private void initUI() {
        //添加viewpager
        mCarouselViewPager = new ViewPager(mContext);
        LayoutParams vpLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        mCarouselViewPager.setLayoutParams(vpLp);

        LayoutParams rlLp = new LayoutParams(LayoutParams.MATCH_PARENT
                , mBottomBarHeight);
        rlLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        //添加Bottombar
        mBottombarContainer = new RelativeLayout(mContext);
        mBottombarContainer.setBackgroundColor(mBottomBarBGColor);
        mBottombarContainer.setGravity(Gravity.BOTTOM);
        rlLp.setMargins(0, 0, 0, mBottomBarMarginBottom);
        mBottombarContainer.setLayoutParams(rlLp);


        LayoutParams indicatorLp = new LayoutParams(LayoutParams.WRAP_CONTENT
                , LayoutParams.MATCH_PARENT);


        LayoutParams textViewLP = new LayoutParams(LayoutParams.WRAP_CONTENT
                , LayoutParams.MATCH_PARENT);



        textViewLP.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        textViewLP.setMargins(mPicDescMarginLeft, 0, 0, 0);

        indicatorLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        indicatorLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        indicatorLp.setMargins(0, 0, mIndicatorMarginRight, 0);


        //添加indicator容器
        mIndicatorContainer = new LinearLayout(mContext);
        mIndicatorContainer.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mBottombarContainer.addView(mIndicatorContainer, indicatorLp);


        //图片描述
        mPicDescTextView = new TextView(mContext);
        mPicDescTextView.setTextSize(mPicDescTextSize);
        mPicDescTextView.setTextColor(mPicDescTextColor);
        mPicDescTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        mPicDescTextView.setPadding(5, 5, 5, 5);
        mBottombarContainer.addView(mPicDescTextView,textViewLP);

        this.addView(mCarouselViewPager);
        this.addView(mBottombarContainer);

    }

    public void setCarouselData(List<BannerInfo> carouselData) {
        long time = System.currentTimeMillis();
        this.mInfoList = carouselData;
        if (mInfoList == null || mInfoList.size() == 0)
            return;

        //初始化轮播任务
        initCarouselTask();
        //初始化ViewPager
        initViewpager();
        //初始化指示器
        initIndicator();
        //初始化轮播图的显示
        setCarouselDesAndIndicator(0);
        //跳转到第一个
        mCarouselViewPager.setCurrentItem(0, true);
        //开始轮播任务
        mCarouselTask.start();
        Log.i("hate","耗费时间:"+(System.currentTimeMillis() - time )+"s");
    }

    private void initViewpager() {
        mCurrentIndex = 0;//初始化index
        mCarouselTask.stop();//停止任务
        mCarouselAdapter = new CarouselAdapter();
        mCarouselViewPager.setAdapter(mCarouselAdapter);
        mCarouselAdapter.notifyDataSetChanged();


        if (mCarouselPagerListener == null) {
            mCarouselPagerListener = new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    mCurrentIndex = position;
                    setCarouselDesAndIndicator(mCurrentIndex);
                }

                @Override
                public void onPageScrolled(int position, float positionOffset,
                                           int positionOffsetPixels) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
            //给轮播图添加页面切换事件
            mCarouselViewPager.addOnPageChangeListener(mCarouselPagerListener);
        }

    }

    private void initIndicator() {
        //清空以前存在的点
        mIndicatorContainer.removeAllViews();
        //轮播图有几张 加几个点
        for (int i = 0; i < mInfoList.size(); i++) {
            View v_point = new View(mContext);
            //设置点的背景选择器
            v_point.setBackgroundResource(R.drawable.carousel_indicator_seletor);
            v_point.setEnabled(false);//默认是默认的灰色点

            //设置点的大小
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(8), dp2px(8));
            //设置点与点直接的间距
            params.leftMargin = mIndicatorSpace;

            //设置参数
            v_point.setLayoutParams(params);
            mIndicatorContainer.addView(v_point);
        }
    }

    private void initPicDescAndIndicator() {

    }

    private void initCarouselTask() {
        if (mCarouselTask == null)//初始化自动轮播任务
            mCarouselTask = new CarouselTask();
    }


    private void setCarouselDesAndIndicator(int position) {
        if (position < 0 || position > mInfoList.size() - 1)
            return;
        //设置描述信息
        mPicDescTextView.setText(mInfoList.get(position).getPicDesc());
        //设置点是否是选中
        for (int i = 0; i < mInfoList.size(); i++) {
            mIndicatorContainer.getChildAt(i).setEnabled(i == position);
        }
    }


    private class CarouselTask extends Handler implements Runnable {

        public void stop() {
            //移除当前所有的任务
            removeCallbacksAndMessages(null);
        }

        public void start() {
            stop();
            if (mInfoList.size() <= 1)//只有一张不启动自动轮播任务
                return;
            postDelayed(this, mAutoTime);
        }

        @Override
        public void run() {
            //控制轮播图的显示
            mCarouselViewPager.setCurrentItem((mCarouselViewPager.getCurrentItem() + 1)
                    % mCarouselViewPager.getAdapter().getCount(), true);
            postDelayed(this, mAutoTime);
        }
    }


    private class CarouselAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView ivCarouselPic = new ImageView(mContext);
            ivCarouselPic.setScaleType(ImageView.ScaleType.FIT_XY);

            //设备默认的图片,网络缓慢
//            ivCarouselPic.setImageResource(R.drawable.home_scroll_default);

            //异步加载图片，并且显示到组件中
            if (mPicInitListener != null)
                mPicInitListener.onPicInit(ivCarouselPic, mInfoList.get(position).getImgUrl(), position);
//            Glide.with(mContext).load(sBannerList.get(position).img).crossFade().into(ivCarouselPic);

            //给图片添加触摸事件
            ivCarouselPic.setOnTouchListener(new OnTouchListener() {

                private float downX;
                private float downY;
                private long downTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN://按下停止轮播
                            downX = event.getX();
                            downY = event.getY();
                            downTime = System.currentTimeMillis();
                            mCarouselTask.stop();
                            break;
                        case MotionEvent.ACTION_CANCEL://事件取消
                            mCarouselTask.start();
                            break;
                        case MotionEvent.ACTION_UP://松开
                            float upX = event.getX();
                            float upY = event.getY();

                            if (upX == downX && upY == downY) {
                                long upTime = System.currentTimeMillis();
                                if (upTime - downTime < 500) {
                                    //点击
                                    if (mCarouselClickListener != null)
                                        mCarouselClickListener.onCarouselClick(mInfoList.get(position), position);
                                }
                            }
                            mCarouselTask.start();//开始轮播
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            container.addView(ivCarouselPic);
            return ivCarouselPic;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mInfoList == null ? 0 : mInfoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


    public ViewPager getCarouselViewPager() {
        return mCarouselViewPager;
    }

    public PagerAdapter getCarouselAdapter() {
        return mCarouselAdapter;
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public void reset(List<BannerInfo> infoList) {
        setCarouselData(infoList);
    }


    public void setPicInitListener(PicInitListener listener) {
        this.mPicInitListener = listener;
    }

    public void setOnCarouselClickListener(OnCarouselClickListener carouselClickListener) {
        this.mCarouselClickListener = carouselClickListener;
    }


    public interface PicInitListener {
        void onPicInit(ImageView imageView, String url, int position);
    }

    public interface OnCarouselClickListener {
        void onCarouselClick(BannerInfo bannerInfo, int position);
    }


    public static class BannerInfo {
        private String imgUrl;//图片地址
        private String jumpUrl;//跳转地址
        private String picDesc;//名称

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getJuUrl() {
            return jumpUrl;
        }

        public void setJumpUrl(String juUrl) {
            this.jumpUrl = juUrl;
        }

        public String getPicDesc() {
            return picDesc;
        }

        public void setPicDesc(String picDesc) {
            this.picDesc = picDesc;
        }

        @Override
        public String toString() {
            return "BannerInfo{" +
                    "imgUrl='" + imgUrl + '\'' +
                    ", juUrl='" + jumpUrl + '\'' +
                    ", picDesc='" + picDesc + '\'' +
                    '}';
        }
    }


    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContext.getResources().getDisplayMetrics());
    }


    /**
     * sp转px
     *
     * @param spVal
     * @return
     */
    private int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, mContext.getResources().getDisplayMetrics());
    }

}
