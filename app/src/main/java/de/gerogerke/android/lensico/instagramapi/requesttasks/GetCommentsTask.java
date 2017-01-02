package de.gerogerke.android.lensico.instagramapi.requesttasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.gerogerke.android.lensico.instagramapi.JSONParser;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramComment;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskUpdateCallback;

/**
 * Created by Deutron on 28.03.2016.
 */
public class GetCommentsTask extends AsyncTask<Object, InstagramComment, List<InstagramComment>> {

    private TaskUpdateCallback callback;

    @Override
    protected List<InstagramComment> doInBackground(Object... params) {
        if (params[2] instanceof TaskUpdateCallback) {
            callback = (TaskUpdateCallback) params[2];
        }
        List<InstagramComment> comments = new ArrayList<>();
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrlByGet("https://api.instagram.com/v1/media/" + params[0] + "/comments?access_token=" + params[1]);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int data_i = 0; data_i < data.length(); data_i++) {
                InstagramComment comment = new InstagramComment();
                JSONObject data_obj = data.getJSONObject(data_i);
                comment.commentedAt = data_obj.getInt("created_time");
                comment.commentText = data_obj.getString("text");
                SimpleInstagramUser simpleInstagramUser = new SimpleInstagramUser();
                simpleInstagramUser.id = data_obj.getJSONObject("from").getString("id");
                simpleInstagramUser.uName = data_obj.getJSONObject("from").getString("username");
                simpleInstagramUser.picUri = data_obj.getJSONObject("from").getString("profile_picture");
                comment.commenter = simpleInstagramUser;

                publishProgress(comment);
                comments.add(comment);
            }
            return comments;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<InstagramComment> result) {
        if (callback != null) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onError();
            }
            callback.onDone();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(InstagramComment... values) {
        if(callback != null) {
            for(InstagramComment medium : values) {
                callback.onUpdate(medium);
            }
        }
    }
}