package de.gerogerke.android.lensico.android;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.bottomsheet.Bottomsheet;

/**
 * Created by Deutron on 29.12.2015.
 */
public class UserTextView extends TextView {

    public UserTextView(Context context) {
        super(context);
        setOnClickListener(null);
        setupStyle(context);
    }

    public UserTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(null);
        setupStyle(context);
    }

    public UserTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(null);
        setupStyle(context);
    }

    private void setupStyle(Context context) {
        setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        setTypeface(null, Typeface.BOLD);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bottomsheet bs = new Bottomsheet();
                bs.openProfileBottomSheet(v.getContext(), getText() + "", null);
            }
        });
    }
}
