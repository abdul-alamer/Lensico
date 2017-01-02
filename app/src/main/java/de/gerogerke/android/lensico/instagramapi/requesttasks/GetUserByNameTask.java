package de.gerogerke.android.lensico.instagramapi.requesttasks;

import android.os.AsyncTask;

import org.json.JSONObject;

import de.gerogerke.android.lensico.instagramapi.JSONParser;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;

/**
 * Created by Deutron on 28.03.2016.
 */
public class GetUserByNameTask extends AsyncTask<Object, Void, SimpleInstagramUser> {

    private TaskCallback callback;

    @Override
    protected SimpleInstagramUser doInBackground(Object... params) {
        if (params[2] instanceof TaskCallback) {
            callback = (TaskCallback) params[2];
        }
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrlByGet("https://api.instagram.com/v1/users/search?q=" + params[0] + "&access_token=" + params[1]);
            JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
            SimpleInstagramUser simpleUser = new SimpleInstagramUser();
            simpleUser.id = data.getString("id");
            simpleUser.picUri = data.getString("profile_picture");
            simpleUser.uName = data.getString("username");
            return simpleUser;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(SimpleInstagramUser result) {
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
    protected void onProgressUpdate(Void... values) {
    }

}
