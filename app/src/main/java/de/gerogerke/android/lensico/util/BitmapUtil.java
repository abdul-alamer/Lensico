package de.gerogerke.android.lensico.util;

import android.graphics.Bitmap;

/**
 * Created by Deutron on 27.12.2015.
 */
public class BitmapUtil {

    public static Bitmap cropBorderFromBitmap(Bitmap bmp) {
        //Convenience variables
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int[] pixels = new int[height * width];

        //Load the pixel data into the pixels array
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        int length = pixels.length;

        int borderColor = pixels[0];

        //Locate the start of the border
        int borderStart = 0;
        for(int i = 0; i < length; i ++) {

            // 1. Compare the color of two pixels whether they differ
            // 2. Check whether the difference is significant
            if(pixels[i] != borderColor && !sameColor(borderColor, pixels[i])) {
                borderStart = i;
                break;
            }
        }

        //Locate the end of the border
        int borderEnd = 0;
        for(int i = length - 1; i >= 0; i --) {
            if(pixels[i] != borderColor && !sameColor(borderColor, pixels[i])) {
                borderEnd = length - i;
                break;
            }
        }

        //Calculate the margins
        int leftMargin = borderStart % width;
        int rightMargin = borderEnd % width;
        int topMargin = borderStart / width;
        int bottomMargin = borderEnd / width;

        //Create the new, cropped version of the Bitmap
        bmp = Bitmap.createBitmap(bmp, leftMargin, topMargin, width - leftMargin - rightMargin, height - topMargin - bottomMargin);
        return bmp;
    }

    private static boolean sameColor(int color1, int color2){
        // Split colors into RGB values
        long r1 = (color1)&0xFF;
        long g1 = (color1 >>8)&0xFF;
        long b1 = (color1 >>16)&0xFF;

        long r2 = (color2)&0xFF;
        long g2 = (color2 >>8)&0xFF;
        long b2 = (color2 >>16)&0xFF;

        long dist = (r2 - r1) * (r2 - r1) + (g2 - g1) * (g2 - g1) + (b2 - b1) *(b2 - b1);

        // Check vs. threshold
        return dist < 200;
    }

}
