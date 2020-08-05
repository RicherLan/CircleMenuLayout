package com.example.circlemenulayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * author: lanweihua
 * created on: 2020/8/5 3:26 PM
 * description:
 */
public class CircleMenuLayout extends ViewGroup {

  //整个layout的直径
  private int mRadius;
  //该容器内child item的默认尺寸
  private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
  // 菜单的中心child的默认尺寸
  private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
  //该容器的内边距,无视padding属性，如需边距请用该变量
  private static final float RADIO_PADDING_LAYOUT = 1 / 12f;

  // 当每秒移动角度达到该值时，认为是快速移动
  private static final int FLINGABLE_VALUE = 300;
  // 如果移动角度达到该值，则屏蔽点击
  private static final int NOCLICK_VALUE = 3;

  //当每秒移动角度达到该值时，认为是快速移动
  private int mFlingableValue = FLINGABLE_VALUE;

  //该容器的内边距,无视padding属性，如需边距请用该变量
  private float mPadding;

  // 布局时的开始角度
  private double mStartAngle = 0;

  //检测按下到抬起时旋转的角度
  private float mTmpAngle;
  //检测按下到抬起时使用的时间
  private long mDownTime;
  //判断是否正在自动滚动
  private boolean isFling;

  //上次点击的位置
  private float mLastX;
  private float mLastY;

  //自动滚动
  private AutoFlingRunnable mAutoFlingRunnable;

  // 菜单项的文本
  private String[] mItemTexts;
  // 菜单项的图标
  private int[] mItemImgs;
  // 菜单的个数
  private int mMenuItemCount;

  private OnMenuItemClickListener mOnMenuItemClickListener;


  public CircleMenuLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    // 无视padding
    setPadding(0, 0, 0, 0);
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    int resWidth = 0;
    int resHeight = 0;

    int width = MeasureSpec.getSize(widthMeasureSpec);
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);

    //精确值
    if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
      resWidth = resHeight = Math.min(width, height);
    } else {
      //如果背景图不为null  那么就取背景图的大小
      resWidth = getSuggestedMinimumWidth();
      //背景图为null  取屏幕宽
      resWidth = resWidth == 0 ? getDefaultWindowSize() : resWidth;

      resHeight = getSuggestedMinimumHeight();
      resHeight = resHeight == 0 ? getDefaultWindowSize() : resHeight;
    }

    setMeasuredDimension(resWidth, resHeight);
    mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());
    // menu item数量
    final int count = getChildCount();
    // menu item尺寸
    int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
    // menu item测量模式
    int childMode = MeasureSpec.EXACTLY;

// 迭代测量
    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);

      if (child.getVisibility() == GONE) {
        continue;
      }

      // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
      int makeMeasureSpec = -1;

      if (child.getId() == R.id.id_circle_menu_item_center) {
        makeMeasureSpec = MeasureSpec.makeMeasureSpec(
            (int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION),
            childMode);
      } else {
        makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
            childMode);
      }
      child.measure(makeMeasureSpec, makeMeasureSpec);
    }

    mPadding = RADIO_PADDING_LAYOUT * mRadius;

  }

  //取屏幕宽高的最小值
  private int getDefaultWindowSize() {
    WindowManager windowManager =
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
    return Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    int layoutRadius = mRadius;

    // Laying out the child views
    final int childCount = getChildCount();

    int left, top;
    // menu item 的尺寸
    int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);

    // 根据menu item的个数，计算角度
    float angleDelay = 360 / (getChildCount() - 1);

    // 遍历去设置menuitem的位置
    for (int i = 0; i < childCount; i++) {
      final View child = getChildAt(i);

      if (child.getId() == R.id.id_circle_menu_item_center) {
        continue;
      }

      if (child.getVisibility() == GONE) {
        continue;
      }

      mStartAngle %= 360;

      // 计算，中心点到menu item中心的距离
      float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;

      // tmp cosa 即menu item中心点的横坐标
      left = layoutRadius
          / 2
          + (int) Math.round(tmp
          * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
          * cWidth);
      // tmp sina 即menu item的纵坐标
      top = layoutRadius
          / 2
          + (int) Math.round(tmp
          * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f
          * cWidth);

      child.layout(left, top, left + cWidth, top + cWidth);
      // 叠加尺寸
      mStartAngle += angleDelay;
    }

    // 找到中心的view，如果存在设置onclick事件
    View cView = findViewById(R.id.id_circle_menu_item_center);
    if (cView != null) {
      cView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {

          if (mOnMenuItemClickListener != null) {
            mOnMenuItemClickListener.itemCenterClick(v);
          }
        }
      });
      // 设置center item位置
      int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
      int cr = cl + cView.getMeasuredWidth();
      cView.layout(cl, cl, cr, cr);
    }


  }

  public void setMenuItemIconsAndTexts(int[] resIds, String[] texts) {
    mItemImgs = resIds;
    mItemTexts = texts;

    // 参数检查
    if (resIds == null && texts == null) {
      throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
    }

    // 初始化mMenuCount
    mMenuItemCount = resIds == null ? texts.length : resIds.length;

    if (resIds != null && texts != null) {
      mMenuItemCount = Math.min(resIds.length, texts.length);
    }

    addMenuItems();

  }

  /**
   * 添加菜单项
   */
  private void addMenuItems() {
    LayoutInflater mInflater = LayoutInflater.from(getContext());

    /**
     * 根据用户设置的参数，初始化view
     */
    for (int i = 0; i < mMenuItemCount; i++) {
      final int j = i;
      View view = mInflater.inflate(R.layout.circle_menu_item, this,
          false);
      ImageView iv = (ImageView) view
          .findViewById(R.id.id_circle_menu_item_image);
      TextView tv = (TextView) view
          .findViewById(R.id.id_circle_menu_item_text);

      if (iv != null) {
        iv.setVisibility(View.VISIBLE);
        iv.setImageResource(mItemImgs[i]);
        iv.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {

            if (mOnMenuItemClickListener != null) {
              mOnMenuItemClickListener.itemClick(v, j);
            }
          }
        });
      }
      if (tv != null) {
        tv.setVisibility(View.VISIBLE);
        tv.setText(mItemTexts[i]);
      }

      // 添加view到容器中
      addView(view);
    }
  }


  //菜单栏item点击事件
  public interface OnMenuItemClickListener {
    void itemClick(View view, int pos);

    void itemCenterClick(View view);
  }

  public void setOnMenuItemClickListener(
      OnMenuItemClickListener mOnMenuItemClickListener) {
    this.mOnMenuItemClickListener = mOnMenuItemClickListener;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {

    float x = event.getX();
    float y = event.getY();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mLastX = x;
        mLastY = y;
        mDownTime = System.currentTimeMillis();
        mTmpAngle = 0;

        // 如果当前已经在快速滚动   那么直接停止滚动
        //return true 不向子view传递事件
        if (isFling) {
          // 移除快速滚动的回调
          removeCallbacks(mAutoFlingRunnable);
          isFling = false;
          return true;
        }
        //如果没有正在滚动  那么事件向子view传递
        break;

      case MotionEvent.ACTION_MOVE:

        float start = getAngle(mLastX, mLastY);
        float end = getAngle(x, y);

        // 如果是一、四象限，顺时针滑动时end-start算出是正值， 逆时针滑动时end-start算出是负值
        //如果下面的逻辑写反的话  那么用户顺时针滑动时界面会是逆时针滚动，逆时针滑动时。。。。。。
        if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
          mStartAngle += end - start;
          mTmpAngle += end - start;
        } else {
          mStartAngle += start - end;
          mTmpAngle += start - end;
        }
        // 重新布局
        requestLayout();

        mLastX = x;
        mLastY = y;

        break;

      case MotionEvent.ACTION_UP:
        // 计算，每秒移动的角度
        float anglePerSecond = mTmpAngle * 1000
            / (System.currentTimeMillis() - mDownTime);

        // 如果达到该值认为是快速移动
        if (Math.abs(anglePerSecond) > mFlingableValue && !isFling) {
          // post一个任务，去自动滚动
          post(mAutoFlingRunnable = new AutoFlingRunnable(anglePerSecond));

          return true;
        }

        // 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
        if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
          return true;
        }

        break;
    }

    return super.dispatchTouchEvent(event);
  }

  //根据坐标获得角度
  private float getAngle(float xTouch, float yTouch) {
    double x = xTouch - (mRadius / 2d);
    double y = yTouch - (mRadius / 2d);

    return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
  }

  //根据坐标计算象限
  private int getQuadrant(float x, float y) {

    int tmpX = (int) (x - mRadius / 2);
    int tmpY = (int) (y - mRadius / 2);
    if (tmpX >= 0) {
      return tmpY >= 0 ? 4 : 1;
    }

    return tmpY >= 0 ? 3 : 2;
  }

  private class AutoFlingRunnable implements Runnable {

    //滚动速度
    private float mAngelPerSecond;

    public AutoFlingRunnable(float angelPerSecond) {
      this.mAngelPerSecond = angelPerSecond;
    }

    @Override
    public void run() {
      //转速小于阙值   停止转动
      if ((int) Math.abs(mAngelPerSecond) < 20) {
        isFling = false;
        return;
      }
      isFling = true;

      //每30mills执行一次滚动
      mStartAngle += (mAngelPerSecond / 30);

      //减速
      mAngelPerSecond /= 1.0666F;
      postDelayed(this, 30);
      requestLayout();

    }
  }


}
