package de.gerogerke.android.lensico.instagramapi.requesttasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.gerogerke.android.lensico.instagramapi.JSONParser;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;
import de.gerogerke.android.lensico.util.StringUtil;

/**
 * Created by Deutron on 28.03.2016.
 */
public class GetLikesTask extends AsyncTask<Object, Void, List<SimpleInstagramUser>> {

    private TaskCallback callback;

    @Override
    protected List<SimpleInstagramUser> doInBackground(Object... params) {
        if (params[2] instanceof TaskCallback) {
            callback = (TaskCallback) params[2];
        }
        List<SimpleInstagramUser> users = new ArrayList<>();
        try {
            String url = "https://api.instagram.com/v1/media/" + params[0] + "/likes?access_token=" + params[1];
            StringUtil.log(url);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrlByGet(url);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int data_i = 0; data_i < data.length(); data_i++) {
                JSONObject data_obj = data.getJSONObject(data_i);
                SimpleInstagramUser user = new SimpleInstagramUser();
                user.id = data_obj.getString("id");
                user.uName = data_obj.getString("username");
                user.picUri = null;
                users.add(user);
            }
            return users;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<SimpleInstagramUser> simpleInstagramUsers) {
        if (callback != null) {
            if (simpleInstagramUsers != null) {
                callback.onSuccess(simpleInstagramUsers);
            } else {
                callback.onError();
            }
            callback.onDone();
        }
    }
}