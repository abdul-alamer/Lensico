package de.gerogerke.android.lensico.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Deutron on 07.01.2016.
 */
public class FullWidthImageView extends ImageView {

    public FullWidthImageView(Context context) {
        super(context);
    }

    public FullWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullWidthImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }

}
