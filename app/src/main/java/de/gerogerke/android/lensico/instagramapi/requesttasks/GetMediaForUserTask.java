package de.gerogerke.android.lensico.instagramapi.requesttasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.gerogerke.android.lensico.instagramapi.JSONParser;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramMedium;
import de.gerogerke.android.lensico.instagramapi.wrapper.PositionPair;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskUpdateCallback;
import de.gerogerke.android.lensico.util.StringUtil;

/**
 * Created by Deutron on 28.03.2016.
 */
public class GetMediaForUserTask extends AsyncTask<Object, InstagramMedium, List<InstagramMedium>> {

    private TaskUpdateCallback callback;

    @Override
    protected List<InstagramMedium> doInBackground(Object... params) {
        if (params[2] instanceof TaskUpdateCallback) {
            callback = (TaskUpdateCallback) params[2];
        }
        List<InstagramMedium> media = new ArrayList<>();
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrlByGet("https://api.instagram.com/v1/users/" + params[0] + "/media/recent/" + "?access_token=" + params[1]);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int data_i = 0; data_i < data.length(); data_i++) {
                InstagramMedium medium = new InstagramMedium();
                JSONObject data_obj = data.getJSONObject(data_i);
                medium.commentCount = data_obj.getJSONObject("comments").getInt("count");
                JSONArray usersInPhoto = data_obj.getJSONArray("users_in_photo");
                for (int users_i = 0; users_i < usersInPhoto.length(); users_i++) {
                    PositionPair pair = new PositionPair();
                    pair.x = usersInPhoto.getJSONObject(users_i).getJSONObject("position").getDouble("x");
                    pair.y = usersInPhoto.getJSONObject(users_i).getJSONObject("position").getDouble("y");
                    medium.usersInPhoto.put(usersInPhoto.getJSONObject(users_i).getJSONObject("user").getString("id"), pair);
                    StringUtil.log("Found " + medium.usersInPhoto.size() + " users in photo");
                }
                JSONObject images_obj = data_obj.getJSONObject("images");
                medium.lowResUrl = images_obj.getJSONObject("low_resolution").getString("url");
                medium.thumbnailUrl = images_obj.getJSONObject("thumbnail").getString("url");
                medium.defResUrl = images_obj.getJSONObject("standard_resolution").getString("url");
                medium.filter = data_obj.getString("filter");
                medium.likesCount = data_obj.getJSONObject("likes").getInt("count");
                medium.createdTime = Integer.parseInt(data_obj.getString("created_time"));
                medium.mediumId = data_obj.getString("id");
                medium.link = data_obj.getString("link");
                if (!data_obj.isNull("caption")) {
                    medium.captionText = data_obj.getJSONObject("caption").getString("text");
                }
                medium.userHasLiked = data_obj.getBoolean("user_has_liked");
                medium.userID = data_obj.getJSONObject("user").getString("id");
                medium.username = data_obj.getJSONObject("user").getString("username");
                medium.userPic = data_obj.getJSONObject("user").getString("profile_picture");
                publishProgress(medium);
                media.add(medium);
            }
            return media;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<InstagramMedium> result) {
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
    protected void onProgressUpdate(InstagramMedium... values) {
        if(callback != null) {
            for (InstagramMedium medium : values) {
                callback.onUpdate(medium);
            }
        }
    }
}