package com.xiandian.openstack.cloud.swiftstorage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * 欢迎视图。展示一个图片。
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //渐变展示启动屏
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f,1.0f);
        alphaAnimation.setDuration(1000);
        getRootView().startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {
                startActivity(new Intent(WelcomeActivity.this,
                        LoginActivity.class));
                finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}

        });

    }

    private View getRootView()
    {
        return ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
    }
}
