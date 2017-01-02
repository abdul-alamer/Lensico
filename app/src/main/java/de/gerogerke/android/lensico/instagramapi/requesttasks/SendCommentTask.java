package de.gerogerke.android.lensico.instagramapi.requesttasks;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.gerogerke.android.lensico.instagramapi.JSONParser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;

/**
 * Created by Deutron on 28.03.2016.
 */
public class SendCommentTask extends AsyncTask<Object, Void, Integer> {

    private TaskCallback callback;

    @Override
    protected Integer doInBackground(Object... params) {
        if(params[2] != null) {
            callback = (TaskCallback) params[2];
        }
        JSONParser jsonParser = new JSONParser();
        try {
            return jsonParser.sendPostRequest("https://api.instagram.com/v1/media/" + params[0] + "/comments?access_token=" + params[1] + "&text=" + URLEncoder.encode((String) params[3], "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (callback != null) {
            if (integer != null) {
                callback.onSuccess(integer);
            } else {
                callback.onError();
            }
            callback.onDone();
        }
    }

}