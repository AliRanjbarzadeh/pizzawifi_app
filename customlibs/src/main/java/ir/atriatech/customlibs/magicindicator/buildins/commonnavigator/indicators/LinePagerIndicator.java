package ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.Arrays;
import java.util.List;

import ir.atriatech.customlibs.magicindicator.FragmentContainerHelper;
import ir.atriatech.customlibs.magicindicator.buildins.ArgbEvaluatorHolder;
import ir.atriatech.customlibs.magicindicator.buildins.UIUtil;
import ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import ir.atriatech.customlibs.magicindicator.buildins.commonnavigator.model.PositionData;


public class LinePagerIndicator extends View implements IPagerIndicator {
    public static final int MODE_MATCH_EDGE = 0;   // 直线宽度 == title宽度 - 2 * mXOffset
    public static final int MODE_WRAP_CONTENT = 1;    // 直线宽度 == title内容宽度 - 2 * mXOffset
    public static final int MODE_EXACTLY = 2;  // 直线宽度 == mLineWidth

    private int mMode;  //

    //
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new LinearInterpolator();

    private float mYOffset;   //
    private float mLineHeight;
    private float mXOffset;
    private float mLineWidth;
    private float mRoundRadius;

    private Paint mPaint;
    private List<PositionData> mPositionDataList;
    private List<Integer> mColors;

    private RectF mLineRect = new RectF();

    public LinePagerIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mLineHeight = UIUtil.dip2px(context, 3);
        mLineWidth = UIUtil.dip2px(context, 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mLineRect, mRoundRadius, mRoundRadius, mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        //
        if (mColors != null && mColors.size() > 0) {
            int currentColor = mColors.get(Math.abs(position) % mColors.size());
            int nextColor = mColors.get(Math.abs(position + 1) % mColors.size());
            int color = ArgbEvaluatorHolder.eval(positionOffset, currentColor, nextColor);
            mPaint.setColor(color);
        }

        //
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);

        float leftX;
        float nextLeftX;
        float rightX;
        float nextRightX;
        if (mMode == MODE_MATCH_EDGE) {
            leftX = current.mLeft + mXOffset;
            nextLeftX = next.mLeft + mXOffset;
            rightX = current.mRight - mXOffset;
            nextRightX = next.mRight - mXOffset;
        } else if (mMode == MODE_WRAP_CONTENT) {
            leftX = current.mContentLeft + mXOffset;
            nextLeftX = next.mContentLeft + mXOffset;
            rightX = current.mContentRight - mXOffset;
            nextRightX = next.mContentRight - mXOffset;
        } else {    // MODE_EXACTLY
            leftX = current.mLeft + (current.width() - mLineWidth) / 2;
            nextLeftX = next.mLeft + (next.width() - mLineWidth) / 2;
            rightX = current.mLeft + (current.width() + mLineWidth) / 2;
            nextRightX = next.mLeft + (next.width() + mLineWidth) / 2;
        }

        mLineRect.left = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
        mLineRect.right = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset);
        mLineRect.top = getHeight() - mLineHeight - mYOffset;
        mLineRect.bottom = getHeight() - mYOffset;

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPositionDataProvide(List<PositionData> dataList) {
        mPositionDataList = dataList;
    }

    public float getYOffset() {
        return mYOffset;
    }

    public void setYOffset(float yOffset) {
        mYOffset = yOffset;
    }

    public float getXOffset() {
        return mXOffset;
    }

    public void setXOffset(float xOffset) {
        mXOffset = xOffset;
    }

    public float getLineHeight() {
        return mLineHeight;
    }

    public void setLineHeight(float lineHeight) {
        mLineHeight = lineHeight;
    }

    public float getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

    public float getRoundRadius() {
        return mRoundRadius;
    }

    public void setRoundRadius(float roundRadius) {
        mRoundRadius = roundRadius;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        if (mode == MODE_EXACTLY || mode == MODE_MATCH_EDGE || mode == MODE_WRAP_CONTENT) {
            mMode = mode;
        } else {
            throw new IllegalArgumentException("mode " + mode + " not supported.");
        }
    }

    public Paint getPaint() {
        return mPaint;
    }

    public List<Integer> getColors() {
        return mColors;
    }

    public void setColors(Integer... colors) {
        mColors = Arrays.asList(colors);
    }

    public Interpolator getStartInterpolator() {
        return mStartInterpolator;
    }

    public void setStartInterpolator(Interpolator startInterpolator) {
        mStartInterpolator = startInterpolator;
        if (mStartInterpolator == null) {
            mStartInterpolator = new LinearInterpolator();
        }
    }

    public Interpolator getEndInterpolator() {
        return mEndInterpolator;
    }

    public void setEndInterpolator(Interpolator endInterpolator) {
        mEndInterpolator = endInterpolator;
        if (mEndInterpolator == null) {
            mEndInterpolator = new LinearInterpolator();
        }
    }
}
