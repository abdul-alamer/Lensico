package de.gerogerke.android.lensico.instagramapi.requesttasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import de.gerogerke.android.lensico.instagramapi.JSONParser;
import de.gerogerke.android.lensico.instagramapi.wrapper.RelationshipStatus;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;
import de.gerogerke.android.lensico.util.StringUtil;

/**
 * Created by Deutron on 28.03.2016.
 */
public class GetRelationshipStatusTask extends AsyncTask<Object, Void, RelationshipStatus> {

    private TaskCallback callback;

    @Override
    protected RelationshipStatus doInBackground(Object... params) {
        if (params[2] instanceof TaskCallback) {
            callback = (TaskCallback) params[2];
        }
        try {
            String url = "https://api.instagram.com/v1/users/" + params[0] + "/relationship?access_token=" + params[1];
            StringUtil.log(url);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = jsonParser.getJSONFromUrlByGet(url);
            JSONObject data;
            try {
                data = jsonObject.getJSONObject("data");
                RelationshipStatus status = new RelationshipStatus();
                status.incoming = data.getString("incoming_status");
                status.outgoing = data.getString("outgoing_status");
                return status;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(RelationshipStatus status) {
        if (callback != null) {
            if (status != null) {
                callback.onSuccess(status);
            } else {
                callback.onError();
            }
            callback.onDone();
        }
    }
}