package de.gerogerke.android.lensico.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.gerogerke.android.lensico.ApplicationData;
import de.gerogerke.android.lensico.R;

/**
 * Created by Deutron on 27.03.2016.
 */
public class StringUtil {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(Context context, long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getString(R.string.rel_time_just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getString(R.string.rel_time_a_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return String.format(context.getString(R.string.rel_time_x_minutes_ago), diff / MINUTE_MILLIS);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getString(R.string.rel_time_an_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return String.format(context.getString(R.string.rel_time_x_hours_ago), diff / HOUR_MILLIS);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getString(R.string.rel_time_yesterday);
        } else {
            return String.format(context.getString(R.string.rel_time_x_days_ago), diff / DAY_MILLIS);
        }
    }

    /**
     * Send a log message. Respects LOG_ACTIVE, set in ApplicationData
     *
     * @param message Message to log
     */
    public static void log(String message) {
        if(ApplicationData.LOG_ACTIVE) {
            Log.d("InstagramAPI", message);
        }
    }

    /**
     * Method to convert stream to string
     *
     * @author Lorensius W. L. T <lorenz@londatiga.net>
     */
    public static String streamToString(InputStream is) throws IOException {
        String str  = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader 	= new BufferedReader(new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

}
