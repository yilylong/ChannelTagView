package com.zhl.channeltagview.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 描述：浮动广告管理
 * Created by zhaohl on 2016-11-3.
 */
public class FloatItemViewManager {
    private static ObjectAnimator alphaAnim;
    public static FloatItemView floatItemView;
    public static Point lastPoint = null;
    /**
     * 小悬浮窗View的参数
     */
    private static WindowManager.LayoutParams smallWindowParams;
    private static Context ctx;

    /**
     * 显示一个floatview
     * @param context
     * @param title
     * @param bgResid
     * @param point
     */
    public static void showFloatADwindow(Context context, View targetView, String title, int bgResid, Point point) {
        lastPoint = point;
        ctx = context;
        if(floatItemView!=null&&alphaAnim!=null&&alphaAnim.isRunning()){
            alphaAnim.cancel();
            removeFloawAdView(context);
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        int []screenSize = MeasureUtil.getScreenSize(context);
//        int screenWidth = screenSize[0];
//        int screenHeight = screenSize[1];
        if (floatItemView == null) {
            floatItemView = new FloatItemView(context);
            floatItemView.getFloatView().setText(title);
            if(bgResid!=-1){
                floatItemView.setFloatViewBg(bgResid);
            }
            // TODO 这里未添加频道的textsize 比已添加的textsize 要大1 还没找到原因 使用同样的item布局
            floatItemView.getFloatView().setTextSize(TypedValue.COMPLEX_UNIT_PX,((TextView)targetView).getTextSize()-1);
            floatItemView.getFloatView().setTextColor(((TextView)targetView).getCurrentTextColor());

            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
                // API level 19 之后 TYPE_TOAST 可以接受事件且不需要申请权限
                // TYPE_APPLICATION 只能配合Activity在当前APP使用
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    smallWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                } else {
                    smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = targetView.getWidth();
                smallWindowParams.height = targetView.getHeight();
                smallWindowParams.x = point.x;
                smallWindowParams.y = point.y;
            }
            floatItemView.setParams(smallWindowParams);
            windowManager.addView(floatItemView, smallWindowParams);
        }
// else{
//            updateFloatViewPosition(point);
//            floatItemView.setFloatTitle(title);
//            if(bgResid!=-1){
//                floatItemView.setFloatViewBg(bgResid);
//            }
//            floatItemView.setAlpha(1);
//        }
    }
    /**
     * 描述：更新位置
     * @param point
     */
    public static void updateFloatViewPosition(Point point){
        smallWindowParams.x = point.x;
        smallWindowParams.y = point.y;
        floatItemView.setParams(smallWindowParams);
        floatItemView.updatePosition();
    }
    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeFloawAdView(Context context) {
        if (floatItemView != null) {
            if(context!=null){
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                windowManager.removeView(floatItemView);
                floatItemView = null;
                ctx = null;
            }
        }
    }

    public static void hideFloatView(){
        if(alphaAnim==null){
            alphaAnim = ObjectAnimator.ofFloat(floatItemView,"alpha",1,0);
            alphaAnim.setDuration(200);
            alphaAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    removeFloawAdView(ctx);
                }
                @Override
                public void onAnimationCancel(Animator animation) {

                }
                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        alphaAnim.start();
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return floatItemView != null;
    }
}
