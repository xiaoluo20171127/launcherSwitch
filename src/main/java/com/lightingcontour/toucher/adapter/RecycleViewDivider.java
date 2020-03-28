package com.lightingcontour.toucher.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;




public class RecycleViewDivider extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private Drawable mDivider;
//    private int mOffset;        //距离左右两边距离
    private int leftOrTopOffset;     //距离左边距离
    private int rightOrBottomOffset;
    private int mDividerHeight = 2;//分割线高度，默认为1px
    private int mOrientation = LinearLayoutManager.VERTICAL;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    public static final String DEFAULT_STYLE = "default_style";

    private boolean hideFirst = false;      //隐藏第一个
    private boolean hideLast = false;       //隐藏最后一个

    private int showDivider = SHOW_DIVIDER_MIDDLE;

    public static final int SHOW_DIVIDER_NONE=0;//這個是沒用的
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_MIDDLE =2;
    public static final int SHOW_DIVIDER_END = 4;



    public static class Builder{
        RecycleViewDivider recycleViewDivider;
        private Context mContext;
        public Builder(Context context){
            mContext = context;
            recycleViewDivider = new RecycleViewDivider();
        }
        public Builder dividerColor(@ColorInt int color) {
            if(recycleViewDivider.mPaint==null) {
                recycleViewDivider.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                recycleViewDivider.mPaint.setStyle(Paint.Style.FILL);
            }
            recycleViewDivider.mPaint.setColor(color);
            return this;
        }
        public Builder dividerColor(ColorFilter color) {
            if(recycleViewDivider.mPaint==null) {
                recycleViewDivider.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                recycleViewDivider.mPaint.setStyle(Paint.Style.FILL);
            }
            recycleViewDivider.mPaint.setColorFilter(color);
            return this;
        }
        public Builder dividerColorId(@ColorRes int colorId) {
            return dividerColor(mContext.getResources().getColor(colorId));
        }
        public Builder orientation(int orientation) {
            if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
                throw new IllegalArgumentException("请输入正确的参数！");
            }
            recycleViewDivider.mOrientation = orientation;
            return this;
        }

        public Builder dividerDrawable(Drawable drawable) {
            recycleViewDivider.mDivider = drawable;
            return this;
        }
        public Builder dividerDrawable(@DrawableRes int id) {
            recycleViewDivider.mDivider = ContextCompat.getDrawable(mContext, id);
            return this;
        }

        public Builder dividerHeight(int height) {
            recycleViewDivider.mDividerHeight = height;
            return this;
        }
        public Builder dividerHeightId(@DimenRes int heightId) {
            recycleViewDivider.mDividerHeight = mContext.getResources().getDimensionPixelOffset(heightId);
            return this;
        }

        public Builder hideFirst(boolean b) {
            recycleViewDivider.hideFirst = b;
            return this;
        }

        public Builder offset(int offset) {
            return offset(offset,offset);
        }
        public Builder offset(int leftOrTopOffset,int rightOrBottomOffset) {
            recycleViewDivider.leftOrTopOffset = leftOrTopOffset;
            recycleViewDivider.rightOrBottomOffset = rightOrBottomOffset;
            return this;
        }

        public Builder showDivider(int showDivider) {
            recycleViewDivider.showDivider = showDivider;
            return this;
        }

        public RecycleViewDivider create() {
            if (recycleViewDivider.mDivider==null) {
                final TypedArray a = mContext.obtainStyledAttributes(ATTRS);
                recycleViewDivider.mDivider = a.getDrawable(0);
                a.recycle();
            }
            return recycleViewDivider;
        }



    }

//
//    public RecycleViewDivider(Context context, boolean hideLast) {
//        this(context, RecycleViewDivider.DEFAULT_STYLE);
//        this.hideLast = hideLast;
//        leftOrTopOffset = (int)context.getResources().getDimension(R.dimen.line_offset);
//    }
//
//    public RecycleViewDivider(Context context, boolean hideFirst, boolean hideLast) {
//        this(context, RecycleViewDivider.DEFAULT_STYLE);
//        this.hideLast = hideLast;
//        this.hideFirst = hideFirst;
//        leftOrTopOffset = (int)context.getResources().getDimension(R.dimen.line_offset);
//    }

    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        int childPosition = parent.getChildAdapterPosition(view);
        if((childPosition ==0 && (showDivider&SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING)){
            outRect.set(0, mDividerHeight, 0, 0);
        }
        if( childPosition == (parent.getAdapter().getItemCount()-1)){
            if ((showDivider&SHOW_DIVIDER_END)== SHOW_DIVIDER_END) {
                outRect.set(0, 0, 0, mDividerHeight);
            }
        } else {
            if ((showDivider&SHOW_DIVIDER_MIDDLE)== SHOW_DIVIDER_MIDDLE) {
                outRect.set(0, 0, 0, mDividerHeight);
            }
        }
//        outRect.set(0, 0, 0, mDividerHeight);
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation != LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    //绘制横向 item 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft()  + leftOrTopOffset;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight() - rightOrBottomOffset;
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    //绘制纵向 item 分割线
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop()+ leftOrTopOffset;
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom()- rightOrBottomOffset;
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
