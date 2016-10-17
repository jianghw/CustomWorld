package com.jianghw.pullscrollviewlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Created by jhwei on 2016/10/17.
 * <p>
 * Describe:
 */

public class PullScrollView extends ScrollView implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnGlobalFocusChangeListener, ViewTreeObserver.OnPreDrawListener, ViewTreeObserver.OnScrollChangedListener, ViewTreeObserver.OnTouchModeChangeListener {
    public PullScrollView(Context context) {
        this(context, null);
    }

    public PullScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 是一个注册监听视图树的观察者(observer)，在视图树种全局事件改变时得到通知
     * ViewTreeObserver不能够被应用程序实例化，因为它是由视图提供getViewTreeObserver()
     * getViewTreeObserver().addOnGlobalLayoutListener()来获得宽度或者高度
     */
    public PullScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (null != viewTreeObserver) {
            viewTreeObserver.addOnGlobalLayoutListener(this);
            viewTreeObserver.addOnGlobalFocusChangeListener(this);
            viewTreeObserver.addOnPreDrawListener(this);
            viewTreeObserver.addOnScrollChangedListener(this);
            viewTreeObserver.addOnTouchModeChangeListener(this);
        }
    }

    /**
     * 当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，
     * 所要调用的回调函数的接口类
     */
    @Override
    public void onGlobalLayout() {

    }

    /**
     * 当在一个视图树中的焦点状态发生改变时，所要调用的回调函数的接口类
     */
    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {

    }

    /**
     * 当一个视图树将要绘制时，所要调用的回调函数的接口类
     */
    @Override
    public boolean onPreDraw() {
        return false;
    }

    /**
     * 当一个视图树中的一些组件发生滚动时，所要调用的回调函数的接口类
     */
    @Override
    public void onScrollChanged() {

    }

    /**
     * 当一个视图树的触摸模式发生改变时，所要调用的回调函数的接口类，用于监听 touch 和 非touch的转换
     */
    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {

    }
}
