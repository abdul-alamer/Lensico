package de.gerogerke.android.lensico.bottomsheet;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.activities.ActivityStreamView;
import de.gerogerke.android.lensico.glide.GlideUtil;
import de.gerogerke.android.lensico.instagramapi.InstagramAPI;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramMedium;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;

/**
 * Created by Deutron on 29.12.2015.
 */
public class Bottomsheet {

    public static void openProfileBottomSheet(final Context mContext, String mUsername, @Nullable final InstagramUser mPassedUser) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_bottomsheet2, null);

        final TextView realName = (TextView) view.findViewById(R.id.bottomsheet_realname_text);
        final TextView userName = (TextView) view.findViewById(R.id.bottomsheet_username_text);
        final TextView bio = (TextView) view.findViewById(R.id.bottomsheet_bio_text);
        final ImageView profilePic = (ImageView) view.findViewById(R.id.bottomsheet_profile_pic);
        final TextView followerCount = (TextView) view.findViewById(R.id.bottomsheet_follower_count_text);
        final TextView followsCount = (TextView) view.findViewById(R.id.bottomsheet_follows_count_text);
        final ViewGroup seeSubmissionsButton = (ViewGroup) view.findViewById(R.id.see_submissions);
        GlideUtil.loadResRound(mContext, R.drawable.profile_pic_placeholder, profilePic);

        final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        if (mPassedUser == null) {
            final InstagramAPI api = InstagramAPI
                    .with(mContext);

            //Get User ID from Username
            api.getConnection().fetchSimpleUser(mUsername, new TaskCallback() {
                @Override
                public void onSuccess(Object object) {
                    SimpleInstagramUser simpleUser = (SimpleInstagramUser) object;
                    if(simpleUser != null) {

                        //Get User Details with User ID
                        api.getConnection().fetchUser(simpleUser.id, new TaskCallback() {
                            @Override
                            public void onSuccess(Object object) {
                                InstagramUser user = (InstagramUser) object;
                                if (user != null) {
                                    setup(user, mContext, dialog, realName, userName, bio, followsCount, followerCount, profilePic, seeSubmissionsButton);
                                }
                            }

                            @Override
                            public void onError() {
                                error(mContext, dialog);
                            }
                        });
                    }
                }

                @Override
                public void onError() {
                    error(mContext, dialog);
                }
            });
        } else {
            setup(mPassedUser, mContext, dialog, realName, userName, bio, followsCount, followerCount, profilePic, seeSubmissionsButton);
        }

        //TODO: (un)Follow User
    }

    private static void error(Context mContext, BottomSheetDialog dialog) {
        Toast.makeText(mContext, "Uh oh, something went wrong loading the user data!", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    private static void setup(final InstagramUser passedUser, final Context mContext, final BottomSheetDialog dialog, TextView realName, TextView userName, TextView bio, TextView followsCount, TextView followerCount, ImageView profilePic, ViewGroup seeSubmissionsButton) {
        if (passedUser.getUsername().equals("herr_mr_g")) {
            realName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pro, 0);
        }
        realName.setText(passedUser.getFullName());
        userName.setText("@" + passedUser.getUsername());
        if (!passedUser.getBio().isEmpty()) {
            bio.setText(passedUser.getBio());
        } else {
            bio.setVisibility(View.GONE);
        }
        followsCount.setText(passedUser.getFollows());
        followerCount.setText(passedUser.getFollowedBy());
        GlideUtil.loadRound(mContext, passedUser.getProfilePictureUrl(), profilePic);
        seeSubmissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent myIntent = new Intent(mContext, ActivityStreamView.class);
                myIntent.putExtra("user", passedUser.toSimpleUser());
                mContext.startActivity(myIntent);
            }
        });
    }

    public static void openInfoBottomSheet(Context context, InstagramMedium medium) {
        TextView tv = new TextView(context);
        tv.setText(Html.fromHtml("<b>Filter: </b>" + medium.filter + "<br>" +
                "<b>Link: </b>" + medium.link + "<br>" +
                "<b>Persons in Image: </b>" + medium.usersInPhoto.size()));
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        dialog.setContentView(tv);
        dialog.show();
    }

}
