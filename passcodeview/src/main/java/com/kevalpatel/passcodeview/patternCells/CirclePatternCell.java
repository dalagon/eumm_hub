/*
 * Copyright 2018 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel.passcodeview.patternCells;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.internal.BasePasscodeView;

/**
 * Created by Keval on 06-Apr-17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public final class CirclePatternCell extends PatternCell {

    @NonNull
    private final Builder mBuilder;
    private boolean isDisplayError;

    private float mTouchRadius;

    @NonNull
    private final Paint mCellPaint;             //Empty indicator color

    @NonNull
    private final Paint mErrorPaint;             //Error indicator color

    private CirclePatternCell(@NonNull final CirclePatternCell.Builder builder,
                              @NonNull final Rect bound,
                              @NonNull final PatternPoint point) {
        super(builder, bound, point);
        mBuilder = builder;

        mTouchRadius = mBuilder.mRadius < getContext().getResources().getDimension(R.dimen.lib_min_touch_radius)
                ? mBuilder.mRadius + 20 : mBuilder.mRadius;

        //Set empty dot paint
        mCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCellPaint.setStyle(Paint.Style.STROKE);
        mCellPaint.setColor(builder.mNormalColor);
        mCellPaint.setStrokeWidth(builder.mStrokeWidth);

        //Set filled dot paint
        mErrorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mErrorPaint.setColor(Color.RED);
    }

    /**
     * Draw the indicator.
     *
     * @param canvas Canvas of {@link PinView}.
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(getBound().exactCenterX(),
                getBound().exactCenterY(),
                mBuilder.mRadius,
                isDisplayError ? mErrorPaint : mCellPaint);
    }

    @Override
    public void onAuthFailed() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDisplayError = false;
                getRootView().invalidate();
            }
        }, 400);
        isDisplayError = true;
    }

    @Override
    public void onAuthSuccess() {
        //Do nothing
    }

    @Override
    public boolean isIndicatorTouched(float touchX, float touchY) {
        //Check if the click is between the width bounds
        //noinspection SimplifiableIfStatement
        if (touchX > getBound().exactCenterX() - mTouchRadius
                && touchX < getBound().exactCenterX() + mTouchRadius) {

            //Check if the click is between the height bounds
            return touchY > getBound().exactCenterY() - mTouchRadius
                    && touchY < getBound().exactCenterY() + mTouchRadius;
        }
        return false;
    }

    public static class Builder extends PatternCell.Builder {
        @ColorInt
        private int mNormalColor;              //Empty indicator stroke color
        @Dimension
        private float mRadius;
        @Dimension
        private float mStrokeWidth;

        public Builder(@NonNull final BasePasscodeView basePasscodeView) {
            super(basePasscodeView);
            setDefaults();
        }

        private void setDefaults() {
            mRadius = getContext().getResources().getDimension(R.dimen.lib_indicator_radius);
            mStrokeWidth = getContext().getResources().getDimension(R.dimen.lib_indicator_stroke_width);
            mNormalColor = getContext().getResources().getColor(R.color.lib_indicator_stroke_color);
        }

        @NonNull
        public CirclePatternCell.Builder setNormalColor(@ColorInt final int normalColor) {
            mNormalColor = normalColor;
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setCellColorResource(@ColorRes final int indicatorStrokeColor) {
            mNormalColor = getContext().getResources().getColor(indicatorStrokeColor);
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setRadius(@Dimension final float radius) {
            mRadius = radius;
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setRadius(@DimenRes final int indicatorRadius) {
            mRadius = getContext().getResources().getDimension(indicatorRadius);
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setStrokeWidth(@Dimension final float strokeWidth) {
            mStrokeWidth = strokeWidth;
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setStrokeWidth(@DimenRes final int indicatorStrokeWidth) {
            mStrokeWidth = getContext().getResources().getDimension(indicatorStrokeWidth);
            return this;
        }

        @NonNull
        @Override
        public PatternCell buildInternal(@NonNull Rect bound, @NonNull PatternPoint point) {
            return new CirclePatternCell(this, bound, point);
        }
    }
}
