package de.gerogerke.android.lensico.instagramapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import de.gerogerke.android.lensico.ApplicationData;
import de.gerogerke.android.lensico.instagramapi.dialog.InstagramAuthorizationDialog;
import de.gerogerke.android.lensico.instagramapi.requesttasks.GetCommentsTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.GetFollowsTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.GetLikesTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.GetMediaForUserTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.GetMediumTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.GetRelationshipStatusTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.GetUserByNameTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.GetUserInfoTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.SendCommentTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.SendLikeTask;
import de.gerogerke.android.lensico.instagramapi.requesttasks.SendUnlikeTask;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramMedium;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskUpdateCallback;

/**
 * Created by Deutron on 24.12.2015.
 */
public class InstagramAPI {

    private static final InstagramAPI mAPIObject = new InstagramAPI();

    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";

    String clientId, callbackUrl, mAccessToken;
    InstagramSession mSession;

    private Context context;

    public static InstagramAPI with(Context context) {
        mAPIObject.context = context;
        mAPIObject.clientId = ApplicationData.CLIENT_ID;
        mAPIObject.callbackUrl = ApplicationData.CALLBACK_URL;
        return mAPIObject;
    }

    public InstagramAPI connect(final OAuthDialogListener mListener) {
        //Create new session from context
        this.mSession = new InstagramSession(context);

        //Check if we are authed...
        if(mSession.getAccessToken() == null) {
            String mAuthUrl = AUTH_URL
                    + "?client_id="
                    + clientId
                    + "&redirect_uri="
                    + callbackUrl
                    + "&response_type=token&scope=basic+public_content+likes+comments+follower_list";

            //Start Dialog and wait for token response
            new InstagramAuthorizationDialog(mAPIObject.context, mAuthUrl, mListener, new InstagramAuthorizationDialog.DialogSuccessListener() {
                @Override
                public void onSucceed(String token) {
                    log("Token: " + token);
                    //Assign Token we just got
                    mAccessToken = token;

                    //Store token to disk to avoid reauthing next time
                    mSession.storeAccessToken(mAccessToken);

                    //Pass on token as listener result
                    if(mListener != null) {
                        mListener.onComplete(mAccessToken);
                    }
                }

                @Override
                public void onFail() {
                    //Return that we failed //TODO: Try again, check reason, no internet MSG mby?
                    if(mListener != null) {
                        mListener.onError(null);
                    }
                }
            }).show();
        } else {
            //Load access token from disk if we already got one!
            //TODO: Make sure we dont assume that the token never expires?!?!
            mAccessToken = mSession.getAccessToken();
            log("Restored Session and Token, no need to reauth!");
            if(mListener != null) {
                mListener.onComplete(mAccessToken);
            }
        }
        return this;
    }

    public InstagramConnection getConnection() {
        if(mAccessToken == null) {
            log("mAccessToken is null. Something might have gone wrong when the user was supposed to log in!");
        }
        return new InstagramConnection(mAccessToken);
    }

    public class InstagramConnection {

        private String mAccessToken;

        public InstagramConnection(String mAccessToken) {
            this.mAccessToken = mAccessToken;
        }

        public void fetchUser(String id, TaskCallback callback) {
            new GetUserInfoTask().execute(id, mAccessToken, callback);
        }

        public void fetchSimpleUser(String name, TaskCallback callback) {
            new GetUserByNameTask().execute(name, mAccessToken, callback);
        }

        public void getAllMediaImages(String userID, TaskUpdateCallback callback) {
            new GetMediaForUserTask().execute(userID, mAccessToken, callback);
        }

        public void getFollows(String id, TaskCallback callback) {
            new GetFollowsTask().execute(id, mAccessToken, callback);
        }

        public void getComments(InstagramMedium medium, TaskUpdateCallback callback) {
            new GetCommentsTask().execute(medium.mediumId, mAccessToken, callback);
        }

        public void getRelationship(SimpleInstagramUser user, TaskCallback callback) {
            new GetRelationshipStatusTask().execute(user.id, mAccessToken, callback);
        }

        public void getMedium(String mediumId, TaskCallback callback) {
            new GetMediumTask().execute(mediumId, mAccessToken, callback);
        }

        public void getLikes(InstagramMedium medium, TaskCallback callback) {
            new GetLikesTask().execute(medium.mediumId, mAccessToken, callback);
        }

        public void like(String mediaId, TaskCallback callback) {
            new SendLikeTask().execute(mediaId, mAccessToken, callback);
        }

        public void unlike(String mediaId, TaskCallback callback) {
            new SendUnlikeTask().execute(mediaId, mAccessToken, callback);
        }

        public void comment(String mediumId, TaskCallback callback, String commentText) {
            new SendCommentTask().execute(mediumId, mAccessToken, callback, commentText);
        }
    }

    class InstagramSession {

        private SharedPreferences sharedPref;
        private SharedPreferences.Editor editor;

        private final String SHARED = "Instagram_Preferences";
        private final String API_ACCESS_TOKEN = "access_token";

        public InstagramSession(Context context) {
            sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }

        public void storeAccessToken(String accessToken) {
            editor.putString(API_ACCESS_TOKEN, accessToken);
            editor.commit();
        }

        public void resetAccessToken() {
            editor.putString(API_ACCESS_TOKEN, null);
            editor.commit();
        }
        public String getAccessToken() {
            return sharedPref.getString(API_ACCESS_TOKEN, null);
        }
    }

    public interface OAuthDialogListener {
        void onComplete(String accessToken);
        void onError(String error);
    }

    private void log(String message) {
        if(ApplicationData.LOG_ACTIVE) {
            Log.d("InstagramAPI", message);
        }
    }

    public void resetAccessToken() {
        if (mAccessToken != null) {
            mSession.resetAccessToken();
            mAccessToken = null;
        }
    }

    public boolean isAuthorized() {
        return mAccessToken != null;
    }

    public String getToken() {
        return mAccessToken;
    }

}
