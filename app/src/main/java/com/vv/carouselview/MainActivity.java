package com.vv.carouselview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vv.carousel.CarouselView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] imageUrls = {"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
            "http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
            "http://pic18.nipic.com/20111215/577405_080531548148_2.jpg",
            "http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
            "http://pic.58pic.com/58pic/12/64/27/55U58PICrdX.jpg"};

    private String[] des = {"哇哈哈", "我不是你", "从你眼中发现", "我是孤独的", "一切都是虚假"};
    private List<CarouselView.BannerInfo> beans;

    private CarouselView mCarouselView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCarouselView = (CarouselView) findViewById(R.id.carouselView);

        mCarouselView.setPicInitListener(new CarouselView.PicInitListener() {
            @Override
            public void onPicInit(final ImageView imageView, final String url, final int position) {
                //使用自己的图片加载方式加载图片
                Glide.with(MainActivity.this).load(url).crossFade().into(imageView);
            }
        });

        mCarouselView.setOnCarouselClickListener(new CarouselView.OnCarouselClickListener() {
            @Override
            public void onCarouselClick(CarouselView.BannerInfo bannerInfo, int position) {
                Toast.makeText(MainActivity.this, "点击了--->" + bannerInfo.getPicDesc() + "位置:" + position, Toast.LENGTH_SHORT).show();
            }
        });
        initCarouselDatas(5);
        mCarouselView.setCarouselData(beans);
        //支持动态改变内容,只需要调用mCarouselView.reset(List<BannerInfo> infos);方法即可.
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initCarouselDatas(5);
                mCarouselView.reset(beans);
            }
        }, 3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initCarouselDatas(3);
                mCarouselView.reset(beans);
            }
        }, 8000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initCarouselDatas(1);
                mCarouselView.reset(beans);
            }
        }, 15000);*/


    }

    private void initCarouselDatas(int count) {
        CarouselView.BannerInfo bannerInfo;
        beans = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            bannerInfo = new CarouselView.BannerInfo();
            bannerInfo.setImgUrl(imageUrls[i]);
            bannerInfo.setPicDesc(des[i]);
            bannerInfo.setJumpUrl(i + "");
            beans.add(bannerInfo);
        }
    }
}
