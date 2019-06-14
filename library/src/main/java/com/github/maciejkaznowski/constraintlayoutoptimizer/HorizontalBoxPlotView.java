package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HorizontalBoxPlotView extends View {

    private float minValue;
    private float maxValue;
    @NonNull private final List<Box> boxes = new ArrayList<>();
    private final int boxHeightPx;
    @NonNull private final Paint rectPaint;
    @NonNull private final Paint pointPaint;
    @NonNull private final Paint textPaint;

    public HorizontalBoxPlotView(Context context) {
        this(context, null);
    }

    public HorizontalBoxPlotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalBoxPlotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        boxHeightPx = getResources().getDimensionPixelOffset(R.dimen.box_height);

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.box_plot_point_width));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.argb((int) (0.87f * 255), 0, 0, 0));
        textPaint.setTextSize(boxHeightPx);
    }

    public void setBoxes(@NonNull List<Box> boxes) {
        this.boxes.clear();
        this.boxes.addAll(boxes);
        this.minValue = calculateMinValue();
        this.maxValue = calculateMaxValue();
        Collections.sort(this.boxes, (o1, o2) -> Float.compare(o1.mean, o2.mean));
        requestLayout();
    }

    private float calculateMinValue() {
        float min = Float.MAX_VALUE;
        for (Box box : boxes) {
            if (box.min < min) {
                min = box.min;
            }
        }

        return min;
    }

    private float calculateMaxValue() {
        float max = Float.MIN_VALUE;
        for (Box box : boxes) {
            if (box.max > max) {
                max = box.max;
            }
        }

        return max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = boxes.size() * boxHeightPx;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < boxes.size(); i++) {
            Box box = boxes.get(i);
            float left = (box.min - minValue) / (maxValue - minValue) * getWidth();
            float right = (box.max - minValue) / (maxValue - minValue) * getWidth();
            float top = i * boxHeightPx;
            float bottom = (i + 1) * boxHeightPx;

            //draw the box
            rectPaint.setColor(box.color);
            canvas.drawRect(left, top, right, bottom, rectPaint);

            //draw the median point
            float pointX = (box.mean - minValue) / (maxValue - minValue) * getWidth();
            float pointY = (top + bottom) / 2f;
            pointPaint.setColor(box.pointColor);
            canvas.drawPoint(pointX, pointY, pointPaint);

            //draw the text
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(box.text, 0, box.text.length(), getWidth(), bottom, textPaint);
        }
    }

    static class Box {

        private final float min;
        private final float mean;
        private final float max;
        private int pointColor;
        private int color;
        private CharSequence text;

        Box(float min, float mean, float max) {
            this.min = min;
            this.mean = mean;
            this.max = max;
        }

        @Override
        public String toString() {
            return "Box{" +
                    "min=" + min +
                    ", mean=" + mean +
                    ", max=" + max +
                    '}';
        }

        public void setColor(int color) {
            this.color = color;

            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[1] = Math.min(0, hsv[1] - 0.3f); //decrease saturation by 30%
            this.pointColor = Color.HSVToColor(hsv);
        }

        public void setText(CharSequence text) {
            this.text = text;
        }
    }
}
