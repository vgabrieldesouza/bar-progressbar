package com.bkdn.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

import java.text.NumberFormat;

/**
 * Copyright (c) 2015  Victor Gabriel de Souza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A round progress bar rendered with bars.
 * Created by Victor Gabriel de Souza (vicgabriels@gmail.com) on 27/05/2015.
 */
public class BarProgressBar extends ProgressBar {

    /** Default size, in dp */
    private static final int DEFAULT_SIZE = 50;

    /** Indeterminate arc degrees */
    private static final int INDETERMINATE_ARC_DEGREES = 270;

    /** Default number of bars to draw */
    private static final int DEFAULT_BAR_COUNT = 40;

    /** Default spacing between bars, in dp */
    private static final int DEFAULT_SPACING = 2;

    /** Delay between animation frames to achieve ~60 fps */
    private static final int DELAY_MILLISECONDS = (int) (1000f / 60f);

    /** Full circle degress */
    private static final float CIRCLE_DEGREES = 360f;

    /** Maximum progress */
    private static final float MAX_PROGRESS = 100f;

    /** Starting angle for defining a progress arc */
    private static final int START_ANGLE = -90;

    /** Default indeterminate animation duration */
    private static final int DEFAULT_ANIMATION_DURATION = 1000;

    /** Device density */
    private float mDensity;

    /** Progress bar path */
    private Path mPath;

    /** Path used to carve a hole in the path center */
    private Path mHolePath;

    /** General paint */
    private Paint mPaint;

    /** Rotate animation */
    private Animation mAnimation;

    /** Progress sweep gradient */
    private SweepGradient mSweepGrad;

    /** Progressbar boundaries */
    private RectF mBoundaries;

    /** Number of bars to draw */
    private int mBarCount = DEFAULT_BAR_COUNT;

    /** Spacing between bars */
    private int mSpacing;

    /** Current angle, used on indeterminate mode */
    private int mAngle;

    /** Progres color */
    private int mProgressColor = Color.GREEN;

    /** Progress background color */
    private int mProgressBgColor = Color.DKGRAY;

    /** Whether to draw progress value in the center */
    private boolean mShowProgress = true;

    /** The progress value font height */
    private int mFontHeight;

    /** Number formatter */
    private NumberFormat mNbrFormat = NumberFormat.getIntegerInstance();

    /**
     * Default java constructor.
     *
     * @param context Activity context.
     */
    public BarProgressBar(Context context) {
        super(context);
        init(null);
    }

    /**
     * XML constructor.
     *
     * @param context The base context.
     * @param attrs Attribute set.
     */
    public BarProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Initializes view attributes.
     *
     * @param attr Optional {@link android.util.AttributeSet}. If null is provided,
     * default values will be used.
     */
    private void init(AttributeSet attr) {
        mDensity = getResources().getDisplayMetrics().density;
        mBarCount = DEFAULT_BAR_COUNT;
        mSpacing = (int) (DEFAULT_SPACING * mDensity);

        if (attr != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attr,
                    R.styleable.BarProgressBar, 0, 0);

            try {
                mSpacing = typedArray.getDimensionPixelOffset
                        (R.styleable.BarProgressBar_spacing, mSpacing);
                mBarCount = typedArray.getInt(R.styleable.BarProgressBar_barCount, mBarCount);
                mProgressBgColor = typedArray.getColor(R.styleable.BarProgressBar_progressBgColor,
                        mProgressBgColor);
                mProgressColor = typedArray.getColor(R.styleable.BarProgressBar_progressColor,
                        mProgressColor);
                mShowProgress = typedArray.getBoolean(R.styleable.BarProgressBar_showProgress,
                        mShowProgress);
            } finally {
                typedArray.recycle();
            }
        }

        mPath = new Path();
        mHolePath = new Path();
        mBoundaries = new RectF();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mProgressBgColor);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mAnimation = new RotateAnimation(0, CIRCLE_DEGREES);
        mAnimation.setInterpolator(getInterpolator());
        mAnimation.setDuration(DEFAULT_ANIMATION_DURATION);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.startNow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = (int) (DEFAULT_SIZE * mDensity);

        final int width = resolveSize(size, widthMeasureSpec);
        final int height = resolveSize(size, heightMeasureSpec);

        final int biggestSide = width > height ? width : height;
        setMeasuredDimension(biggestSide, biggestSide);

        mBoundaries.set(0, 0, biggestSide * .9f, biggestSide * .9f);
        mBoundaries.offsetTo((biggestSide - mBoundaries.width()) / 2,
                (biggestSide - mBoundaries.height()) / 2);

        final int sweepAngle = (360 / mBarCount) - mSpacing;
        int angle = 0;

        mPath.reset();

        mHolePath.reset();
        mHolePath.addCircle(mBoundaries.centerX(), mBoundaries.centerY(),
                mBoundaries.width() * 0.4f, Path.Direction.CW);

        for (int i = 0; i < mBarCount; i++, angle += sweepAngle + mSpacing) {
            mPath.addArc(mBoundaries, angle, sweepAngle);
            mPath.lineTo(mBoundaries.centerX(), mBoundaries.centerY());
            mPath.close();
        }

        mPath.op(mHolePath, Path.Op.DIFFERENCE);

        final int progressColorMaxAlpha = mProgressColor & 0x00FFFFFF;
        mSweepGrad = new SweepGradient(mBoundaries.centerX(), mBoundaries.centerY(),
                new int[]{mProgressColor, progressColorMaxAlpha}, new float[]{0f, .75f});

        mPaint.setTextSize(mBoundaries.width() / 4);

        final Rect tmpRect = new Rect();
        mPaint.getTextBounds("0", 0, 1, tmpRect);
        mFontHeight = tmpRect.height();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mShowProgress) {
            drawProgressValue(canvas);
        }

        canvas.clipPath(mPath);

        if (isIndeterminate()) {
            drawIndeterminate(canvas);
        } else {
            drawProgress(canvas);
        }
    }

    /**
     * Draws the progress numerical value
     *
     * @param canvas The canvas provided on {@link #onDraw(android.graphics.Canvas)}.
     */
    private void drawProgressValue(final Canvas canvas) {
        if (!isIndeterminate()) {
            mPaint.setColor(mProgressBgColor);
            final String text = mNbrFormat.format(getProgress()) + "%";
            canvas.drawText(text, mBoundaries.centerX(),
                    mBoundaries.centerY() + (mFontHeight) / 2, mPaint);
        }
    }

    /**
     * Draws the progress bars.
     *
     * @param canvas The canvas provided on {@link #onDraw(android.graphics.Canvas)}.
     */
    private void drawProgress(final Canvas canvas) {
        final float sweep = getProgress() * CIRCLE_DEGREES / MAX_PROGRESS;
        mPaint.setColor(mProgressBgColor);
        canvas.drawPath(mPath, mPaint);
        mPaint.setColor(mProgressColor);
        canvas.drawArc(mBoundaries, START_ANGLE, sweep, true, mPaint);
    }

    /**
     * Draws the animated indeterminate progress.
     *
     * @param canvas The canvas provided on {@link #onDraw(android.graphics.Canvas)}.
     */
    private void drawIndeterminate(final Canvas canvas) {
        canvas.save();
        canvas.rotate(mAngle, mBoundaries.centerX(), mBoundaries.centerY());
        mPaint.setShader(mSweepGrad);
//        canvas.drawCircle(mBoundaries.centerX(), mBoundaries.centerY(), mBoundaries.width() / 2, mPaint);
        canvas.drawArc(mBoundaries, 0, INDETERMINATE_ARC_DEGREES, true, mPaint);
        mPaint.setShader(null);
        canvas.restore();

        mAngle = (int) (CIRCLE_DEGREES * getInterpolation(mAnimation));
        postInvalidateDelayed(DELAY_MILLISECONDS);
    }

    /**
     * Gets the interpolation value for the indeterminate animation.
     *
     * @param animation The animation.
     * @return The interpolation value, generally a number between 0 and 1. For overshoot
     * interpolator it might return a number bigger than 1.
     */
    private float getInterpolation(final Animation animation) {
        final long animTime = (AnimationUtils.currentAnimationTimeMillis()
                - animation.getStartTime()) % animation.getDuration();
        return animation.getInterpolator().getInterpolation((float) animTime
                / (float) animation.getDuration());
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        super.setInterpolator(interpolator);
        if (mAnimation != null) {
            mAnimation.setInterpolator(getInterpolator());
        }
    }

    /**
     * Sets the indeterminate animation duration.
     *
     * @param duration The animation duration, in milliseconds.
     */
    public void setIndeterminateDuration(final long duration) {
        mAnimation.setDuration(duration);
        mAnimation.reset();
        mAnimation.startNow();
    }

    /**
     * Sets the progress bar color.
     *
     * @param progressColor The progress color.
     */
    public void setProgressColor(final int progressColor) {
        this.mProgressColor = progressColor;
    }

    /**
     * Sets the progress bar background color.
     *
     * @param progressBgColor The progress background color.
     */
    public void setProgressBgColor(final int progressBgColor) {
        this.mProgressBgColor = progressBgColor;
    }

    /**
     * <b>Work in progress</b>
     * Sets the progress.
     *
     * @param progress The progress to set.
     * @param animate Whether to animate the change.
     */
    public synchronized void setProgress(final int progress, final boolean animate) {
        removeCallbacks(null);
        setIndeterminate(false);

        if (!animate) {
            super.setProgress(progress);
        } else {
            final int startProgress = getProgress();

            final Animation anim = new RotateAnimation(0, 100);
            anim.setInterpolator(getInterpolator());
            anim.setDuration(DEFAULT_ANIMATION_DURATION);
            anim.setFillAfter(true);
            anim.start();

            final Transformation t = new Transformation();

            post(new Runnable() {
                int currProgress;

                @Override
                public void run() {
                    currProgress = (int) ((startProgress - progress) * getInterpolation(anim));
                    anim.getTransformation(AnimationUtils.currentAnimationTimeMillis(), t);

                    if (!anim.hasEnded()) {
                        setProgress(startProgress - currProgress);
                        invalidate();
                        post(this);
                    }
                }
            });
        }
    }
}
