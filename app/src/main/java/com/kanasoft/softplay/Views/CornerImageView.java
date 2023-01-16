package com.kanasoft.softplay.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;

import com.kanasoft.softplay.R;

import pl.droidsonroids.gif.GifImageView;

public class CornerImageView extends GifImageView {

    private Path path;
    private int borderRadius;

    public CornerImageView(Context context) {
        super(context);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @SuppressLint("CustomViewStyleable")
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomAttribute);
            borderRadius = styledAttrs.getInt(R.styleable.CustomAttribute_borderRadius, 0);

            styledAttrs.recycle();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(path);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float borderRadius = (float) this.borderRadius;
        RectF r = new RectF(0, 0, w, h);
        path = new Path();
        path.addRoundRect(r, borderRadius, borderRadius, Path.Direction.CW);
        path.close();
    }
}
