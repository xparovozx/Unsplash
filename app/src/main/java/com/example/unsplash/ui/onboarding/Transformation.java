package com.example.unsplash.ui.onboarding;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

public class Transformation implements ViewPager2.PageTransformer{
    @Override
    public void transformPage(View page, float position) {
        if (position < -1){
            page.setAlpha(0);
        }
        else if (position <= 0){
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
        }
        else if (position <= 1){
            page.setTranslationX(-position*page.getWidth());
            page.setAlpha(1-Math.abs(position));
            page.setScaleX(1-Math.abs(position));
            page.setScaleY(1-Math.abs(position));
        }
        else {
            page.setAlpha(0);

        }
    }
}