package com.windriver.pcgate.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.windriver.pcgate.R;

public class SegmentedLoadingView extends View
    {

    private final int segments = 7;
    private int currentSegments = 0;
    private Paint backgroundPaint;
    private Drawable icon;
    private Path clipPath;
    private RectF ovalRect;

    public SegmentedLoadingView(Context context)
        {
        super(context);
        init();
        }

    public SegmentedLoadingView(Context context, @Nullable AttributeSet attrs)
        {
        super(context, attrs);
        init();
        }

    public SegmentedLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
        {
        super(context, attrs, defStyleAttr);
        init();
        }

    private void init()
        {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(
                ContextCompat.getColor(getContext(), R.color.primary)); // Or any other color
        backgroundPaint.setStyle(Paint.Style.FILL);

        icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_gemini_chat);
        if (icon != null)
        {
            icon.setTint(ContextCompat.getColor(getContext(), R.color.on_primary));
        }

        clipPath = new Path();
        ovalRect = new RectF();
        }

    public void setProgress(int segmentsLoaded)
        {
        currentSegments = Math.max(0, Math.min(segments, segmentsLoaded));
        updateClipPath();
        invalidate();
        }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
        super.onSizeChanged(w, h, oldw, oldh);
        float size = Math.min(w, h);
        float centerX = w / 2f;
        float centerY = h / 2f;
        ovalRect.set(centerX - size / 2, centerY - size / 2, centerX + size / 2,
                centerY + size / 2);
        updateClipPath();
        }

    private void updateClipPath()
        {
        clipPath.reset();
        if (currentSegments > 0)
        {
            float angle = 360f / segments;
            clipPath.moveTo(ovalRect.centerX(), ovalRect.centerY());
            clipPath.arcTo(ovalRect, -90, angle * currentSegments, false);
            clipPath.close();
        }
        }

    @Override
    protected void onDraw(@NonNull Canvas canvas)
        {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f;
        canvas.drawCircle(width / 2f, height / 2f, radius, backgroundPaint);
        if (icon != null && currentSegments > 0)
        {
            canvas.save();
            canvas.clipPath(clipPath);

            int iconSize = (int) (radius * 1.5f);
            int iconLeft = (width - iconSize) / 2;
            int iconTop = (height - iconSize) / 2;
            icon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize);
            icon.draw(canvas);

            canvas.restore();
        }
        }
    }

