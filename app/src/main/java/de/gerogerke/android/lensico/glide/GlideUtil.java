package de.gerogerke.android.lensico.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by Deutron on 29.12.2015.
 */
public class GlideUtil {

    public static void load(Context context, String url, final ImageView imageView) {
        if(context != null) {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            imageView.setImageDrawable(new BitmapDrawable(bitmap));
                        }
                    });
        }
    }

    public static void loadRound(Context context, String url, final ImageView imageView) {
        if(context != null) {
            Glide.with(context).load(url).transform(new CircleTransform(context)).into(imageView);
        }
    }

    public static void loadResRound(Context context, int resID, final ImageView imageView) {
        if(context != null) {
            Glide.with(context).load(resID).transform(new CircleTransform(context)).into(imageView);
        }
    }
}
