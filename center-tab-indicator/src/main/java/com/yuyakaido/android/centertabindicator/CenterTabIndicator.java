package com.yuyakaido.android.centertabindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CenterTabIndicator extends HorizontalScrollView {

    public static final int MAX_SIZE = 300;

    private static final int TAB_TEXT_VIEW_PADDING_DIPS = 15;
    private static final int TAB_TEXT_VIEW_TEXT_SIZE_SP = 12;

    private ViewPager mViewPager;
    private CenterTabStrip mTabStrip;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private ScrollDirection mScrollDirection = ScrollDirection.STOP;
    private int mSelectedPosition;
    private int mDisplayWidth;
    private int mDisplayLeftX;

    public CenterTabIndicator(Context context) {
        this(context, null);
    }

    public CenterTabIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterTabIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDisplayWidth = Utils.getDisplayWidth(getContext());

        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);

        mTabStrip = new CenterTabStrip(getContext());
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);

        mScrollDirection = Utils.getScrollDirection(x, oldX);
        mDisplayLeftX = x;

        if (mTabStrip != null) {
            mTabStrip.invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int displayCenterX = getScrollX() + (mDisplayWidth / 2);
        View centerView = mTabStrip.getChildAt(mSelectedPosition);
        if (centerView == null) {
            return super.onTouchEvent(event);
        }
        if (centerView.getLeft() <= displayCenterX && displayCenterX <= centerView.getRight()) {
            // Do nothing
        } else {
            if (mScrollDirection == ScrollDirection.LEFT) {
                mSelectedPosition++;
            } else if (mScrollDirection == ScrollDirection.RIGHT) {
                mSelectedPosition--;
            }
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                scrollToTab(mSelectedPosition, 0);
                mTabStrip.onViewPagerPageChanged(mSelectedPosition, 0f);
                mViewPager.setCurrentItem(mSelectedPosition);
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setCenterTabColor(int color) {
        mTabStrip.setCenterTabColor(color);
    }

    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (mViewPager != null) {
            mViewPager.setCurrentItem(CenterTabIndicator.MAX_SIZE / 2);
            mViewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
            scrollToTab(mViewPager.getCurrentItem(), 0);
            mSelectedPosition = MAX_SIZE / 2;
            ((TextView) mTabStrip.getChildAt(mSelectedPosition)).setTextColor(Color.WHITE);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    private void resetTextColor() {
        TextView textView = null;
        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            textView = (TextView) mTabStrip.getChildAt(i);
            textView.setTextColor(Color.DKGRAY);
        }
    }

    private TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_TEXT_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(Color.DKGRAY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            textView.setAllCaps(true);
        }

        int padding = (int) (TAB_TEXT_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    private void populateTabStrip() {
        PagerAdapter adapter = mViewPager.getAdapter();
        OnClickListener tabClickListener = new TabClickListener();
        for (int i = 0; i < adapter.getCount(); i++) {
            TextView textView = createDefaultTabView(getContext());
            textView.setText(adapter.getPageTitle(i));
            textView.setOnClickListener(tabClickListener);
            mTabStrip.addView(textView);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                targetScrollX -= (mDisplayWidth - selectedChild.getWidth()) / 2;
            }

            smoothScrollTo(targetScrollX, 0);

            resetTextColor();
            ((TextView) selectedChild).setTextColor(Color.WHITE);
        }
    }

    private class CenterTabStrip extends LinearLayout {

        private Paint mCenterTabIndicatorPaint = new Paint();
        private Paint mTextPaint = new Paint();

        private int mSelectedPosition;
        private float mPositionOffset;

        public CenterTabStrip(Context context) {
            this(context, null);
        }

        public CenterTabStrip(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setWillNotDraw(false);
            setGravity(Gravity.CENTER_VERTICAL);
            mDisplayWidth = Utils.getDisplayWidth(getContext());
            mCenterTabIndicatorPaint.setColor(Color.GREEN);
            mCenterTabIndicatorPaint.setAntiAlias(true);
        }

        public void onViewPagerPageChanged(int position, float positionOffset) {
            mSelectedPosition = position;
            mPositionOffset = positionOffset;
            invalidate();
        }

        public void setCenterTabColor(int color) {
            mCenterTabIndicatorPaint.setColor(color);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int height = getHeight();
            int childCount = getChildCount();

            if (childCount > 0) {
                TextView selectedTitle = (TextView) getChildAt(mSelectedPosition);
                int left = getTextLeft(selectedTitle);
                int right = getTextRight(selectedTitle);

                int c1 = 0;
                int c2 = 0;

                if (mPositionOffset > 0f && mSelectedPosition < (childCount - 1)) {
                    c1 = blendColors(Color.DKGRAY, Color.WHITE, mPositionOffset);
                    c2 = blendColors(Color.WHITE, Color.DKGRAY, mPositionOffset);

                    TextView nextTitle = (TextView) getChildAt(mSelectedPosition + 1);
                    left = (int) (mPositionOffset * getTextLeft(nextTitle) +
                            (1.0f - mPositionOffset) * left);
                    right = (int) (mPositionOffset * getTextRight(nextTitle) +
                            (1.0f - mPositionOffset) * right);

                    nextTitle.setTextColor(c2);
                }

                int displayCenterX = mDisplayLeftX + (mDisplayWidth / 2);
                int width = right - left;

                int centeringLeft = displayCenterX - (width / 3);
                int centeringRight = displayCenterX + (width / 3);

                if (c1 != 0) {
                    selectedTitle.setTextColor(c1);
                }

                mTextPaint.setTextSize(selectedTitle.getTextSize());
                int textHeight = (int) mTextPaint.getFontMetrics(null);
                int heightPadding = (int) ((height - textHeight) / 2.8);

                canvas.drawRect(centeringLeft, heightPadding, centeringRight, height - heightPadding, mCenterTabIndicatorPaint);

                RectF rightRectF = new RectF(centeringRight - heightPadding, heightPadding, centeringRight + heightPadding, height - heightPadding);
                RectF leftRectF = new RectF(centeringLeft - heightPadding, heightPadding, centeringLeft + heightPadding, height - heightPadding);
                canvas.drawArc(rightRectF, 0, 360, true, mCenterTabIndicatorPaint);
                canvas.drawArc(leftRectF, 0, 360, true, mCenterTabIndicatorPaint);
            }
        }

        private int getTextLeft(TextView textView) {
            int textViewLeft = textView.getLeft();
            int textViewWidth = textView.getWidth();
            int textWidth = getTextWidth(textView);
            int leftPadding = (textViewWidth - textWidth) / 2;
            return textViewLeft + leftPadding;
        }

        private int getTextRight(TextView textView) {
            int textViewRight = textView.getRight();
            int textViewWidth = textView.getWidth();
            int textWidth = textView.getWidth();
            int rightPadding = (textViewWidth - textWidth) / 2;
            return textViewRight - rightPadding;
        }

        private int getTextWidth(TextView textView) {
            Paint paint = new Paint();
            paint.setTextSize(textView.getTextSize());
            return (int) paint.measureText(textView.getText().toString());
        }

        private int blendColors(int color1, int color2, float ratio) {
            final float inverseRation = 1f - ratio;
            float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
            float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
            float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
            return Color.rgb((int) r, (int) g, (int) b);
        }

    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        public int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            View nextSelectTitle = mTabStrip.getChildAt(position + 1);
            if (selectedTitle != null && nextSelectTitle != null) {
                int extraOffset = (int) (positionOffset *
                        ((selectedTitle.getWidth() + nextSelectTitle.getWidth()) / 2));
                scrollToTab(position, extraOffset);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }

            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }

            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }
        }

    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

    private enum ScrollDirection {
        LEFT, STOP, RIGHT
    }

    private static class Utils {

        public static int getDisplayWidth(Context context) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            return display.getWidth();
        }

        public static ScrollDirection getScrollDirection(int x, int oldX) {
            if (oldX < x) {
                return ScrollDirection.LEFT;
            } else if (x < oldX) {
                return ScrollDirection.RIGHT;
            } else {
                return ScrollDirection.STOP;
            }
        }

    }

}
