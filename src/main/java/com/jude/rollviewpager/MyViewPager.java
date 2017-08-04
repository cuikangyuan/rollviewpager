package com.jude.rollviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.reflect.Field;

/**
 * Created by cuikangyuan on 2017/8/3.
 */

public class MyViewPager extends ViewPager {

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        /**
         * 坑，解决在RecyclerView中使用的bug
         * 设ViewPager中有3张照片
         * 当ViewPager滑动一遍之后，向下滑动RecyclerView列表
         * 直到完全隐藏此ViewPager，并执行了onDetachedFromWindow
         * 再回来时，将会出现bug，第一次滑动时没有动画效果，并且，经常出现view没有加载的情况
         */
        try {
            Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
            mFirstLayout.setAccessible(true);
            mFirstLayout.set(this, false);

            setCurrentItem(getCurrentItem());
            //setClipChildren(false);
            int currentItem = getCurrentItem();

            //setCurrentItem(currentItem + 1);
            //View childAt = getChildAt(currentItem);
            //childAt.scrollBy(10, 0);
            //getAdapter().notifyDataSetChanged();
            /*
            Method[] methods = ViewPager.class.getMethods();
            Method populate = ViewPager.class.getMethod("populate", int.class);
            populate.invoke(this, getCurrentItem());
            */
            //measureChildren(MeasureSpec.AT_MOST, MeasureSpec.AT_MOST);
            //forceLayout();
            //requestLayout();
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ZoomOutPageTransformer mPageTransformer = new ZoomOutPageTransformer();
                    final int scrollX = getScrollX();
                    final int childCount = getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        final View child = getChildAt(i);
                        final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                        if (lp.isDecor) continue;
                        final float transformPos = (float) (child.getLeft() - scrollX) / (getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
                        mPageTransformer.transformPage(child, transformPos);

                    }
                }
            });
            //addOnScrollChangedListener也可以

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
