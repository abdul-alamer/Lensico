package de.gerogerke.android.lensico.instagramapi.requesttasks;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.URL;

import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;
import de.gerogerke.android.lensico.util.StringUtil;

/**
 * Created by Deutron on 28.03.2016.
 */
public class GetUserInfoTask extends AsyncTask<Object, Void, InstagramUser> {

    private TaskCallback callback;

    @Override
    protected InstagramUser doInBackground(Object... params) {
        if (params[2] != null && params[2] instanceof TaskCallback) {
            callback = (TaskCallback) params[2];
        }
        try {
            URL url = new URL("https://api.instagram.com/v1/users/" + params[0] + "/?access_token=" + params[1]);
            StringUtil.log("Opening URL " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            String response = StringUtil.streamToString(urlConnection.getInputStream());
            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONObject data_obj = jsonObj.getJSONObject("data");
            String tagID = data_obj.getString("id");
            String profilePic = data_obj.getString("profile_picture");
            String username = data_obj.getString("username");
            String bio = data_obj.getString("bio");
            String website = data_obj.getString("website");
            String fullName = data_obj.getString("full_name");

            JSONObject counts_obj = data_obj.getJSONObject("counts");
            String numFollows = counts_obj.getString("follows");
            String numFollowers = counts_obj.getString("followed_by");
            String numMedia = counts_obj.getString("media");

            return new InstagramUser(tagID, profilePic, username, bio, website, fullName, numFollows, numFollowers, numMedia);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(InstagramUser result) {
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
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
}