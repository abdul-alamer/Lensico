package de.gerogerke.android.lensico.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.android.FullWidthImageView;
import de.gerogerke.android.lensico.android.recycler.CommentsAdapter;
import de.gerogerke.android.lensico.bottomsheet.Bottomsheet;
import de.gerogerke.android.lensico.glide.GlideUtil;
import de.gerogerke.android.lensico.helper.CustomTabManager;
import de.gerogerke.android.lensico.instagramapi.InstagramAPI;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramComment;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramMedium;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskUpdateCallback;
import de.gerogerke.android.lensico.util.StringUtil;

/**
 * Created by Deutron on 27.12.2015.
 */
public class ActivityImageView extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_image_view_photo_view)
    FullWidthImageView photoView;

    @Bind(R.id.activity_text_view_username)
    TextView username;

    @Bind(R.id.activity_image_view_time)
    TextView timeTv;

    @Bind(R.id.comments_recycler)
    RecyclerView mCommentsRecycler;

    @Bind(R.id.loading_notice)
    ViewGroup mLoadingNotice;

    @Bind(R.id.no_comments_notice)
    TextView mNoCommentsNotice;

    @Bind(R.id.comment_text)
    EditText commentTextField;

    @Bind(R.id.comment_send_button)
    ImageView commentSendButton;

    @Bind(R.id.comment_send_progress1)
    View commentSpinner;

    @Bind(R.id.activity_image_view_likers_names)
    TextView likersNames;

    @Bind(R.id.activity_image_view_toggle_like_btn)
    Button mToggleLinkButton;

    InstagramMedium medium;
    InstagramAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view2);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        medium = (InstagramMedium) intent.getSerializableExtra("medium");

        GlideUtil.load(this, medium.defResUrl, photoView);

        username.setText(medium.username);
        timeTv.setText(StringUtil.getTimeAgo(this, Long.parseLong(medium.createdTime + "000")));

        api = InstagramAPI
                .with(this);

        if (api.getToken() == null) {
            api.connect(null);
        }

        api.getConnection().getMedium(medium.mediumId, new TaskCallback() {
            @Override
            public void onSuccess(Object object) {
                medium = (InstagramMedium) object;
            }
        });

        mToggleLinkButton.setText(medium.userHasLiked ? "Unlike" : "Like");
        mToggleLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Sync like state
                if (medium.userHasLiked) {
                    //Block Button to prevent networkspam while ongoing request is being processed
                    mToggleLinkButton.setEnabled(false);
                    api.getConnection().unlike(medium.mediumId, new TaskCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            mToggleLinkButton.setText("Like");
                            updateLikes();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(ActivityImageView.this, "Error unliking image!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDone() {
                            //Stop blocking button since all networking is done
                            mToggleLinkButton.setEnabled(true);
                        }
                    });
                } else {
                    mToggleLinkButton.setEnabled(false);
                    api.getConnection().like(medium.mediumId, new TaskCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            mToggleLinkButton.setText("Unlike");
                            updateLikes();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(ActivityImageView.this, "Error liking image!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDone() {
                            //Stop blocking button since all networking is done
                            mToggleLinkButton.setEnabled(true);
                        }
                    });
                }
                updateLikes();
            }
        });

        updateLikes();

        commentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Check if EditText is empty; if it is, do nothing
                if (!TextUtils.isEmpty(commentTextField.getText().toString())) {

                    //Get user-entered comment text
                    final String commentText = commentTextField.getText().toString();

                    //TODO: Some form of validation here!
                    //Create a comment on a media object with the following rules:
                    // * The total length of the comment cannot exceed 300 characters
                    // * The comment cannot contain more than 4 hashtags.
                    // * The comment cannot contain more than 1 URL.
                    // * The comment cannot consist of all capital letters.

                    //Clear the EditText to prevent comment spam
                    commentTextField.setText("");

                    //Update ui to indicate work...
                    commentSpinner.setVisibility(View.VISIBLE);
                    commentSendButton.setVisibility(View.GONE);

                    //Post the comment to the instagram api
                    api.getConnection().comment(medium.mediumId, new TaskCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            int responseCode = (Integer) object;
                            Toast.makeText(ActivityImageView.this, "Resp: " + responseCode, Toast.LENGTH_SHORT).show();
                            Snackbar.make(v, "Posted Comment", Snackbar.LENGTH_SHORT).show();
                            //Reload comment list to show your comment
                            updateRecycler();
                        }

                        @Override
                        public void onError() {
                            Snackbar.make(v, "There was a problem posting this comment!", Snackbar.LENGTH_LONG).show();
                            //Put the text back for the users convenience
                            commentTextField.setText(commentText);
                        }

                        @Override
                        public void onDone() {
                            //Reset UI in either case...
                            commentSpinner.setVisibility(View.GONE);
                            commentSendButton.setVisibility(View.VISIBLE);
                        }
                    }, commentText);
                }
            }
        });

        //Load comments initially
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mCommentsRecycler.setLayoutManager(mLayoutManager);
        updateRecycler();
    }

    public void updateLikes() {
        //Confirm this is working
        api.getConnection().getLikes(medium, new TaskCallback() {
            //TODO: //TUDO: Make Names clickable, own name as "You" first place?!
            @Override
            public void onSuccess(Object object) {
                List<SimpleInstagramUser> likers = (List<SimpleInstagramUser>) object;
                StringBuilder builder = new StringBuilder();
                int timesAdded = 0;
                String delim = "";
                for (SimpleInstagramUser user : likers) {
                    builder.append(delim + user.uName);
                    delim = ", ";
                    if (timesAdded == 3) {
                        break;
                    }
                    timesAdded++;
                }
                if (likers.size() > 3) {
                    builder.append(" and " + (likers.size() - 3) + " more");
                }
                if (likers.size() > 0) {
                    builder.append(" " + (likers.size() == 1 ? "likes" : "like") + " this!");
                    likersNames.setText(builder.toString());
                }
            }

            @Override
            public void onError() {
                Snackbar.make(toolbar, "There was a problem loading likes for this image!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void updateRecycler() {
        //Show loading notice
        mLoadingNotice.setVisibility(View.VISIBLE);

        //Check if We have a adapter and if we can cast
        if (mCommentsRecycler.getAdapter() != null && mCommentsRecycler.getAdapter() instanceof CommentsAdapter) {
            //We can safely clear the items
            ((CommentsAdapter) mCommentsRecycler.getAdapter()).clear();
        }

        //Load new comments
        api.getConnection().getComments(medium, new TaskUpdateCallback() {
            @Override
            public void onSuccess(Object object) {
                List<InstagramComment> comments = (List<InstagramComment>) object;
                //Check if comments available
                if (comments.size() > 0) {
                    mCommentsRecycler.setAdapter(new CommentsAdapter(ActivityImageView.this, comments));
                    mCommentsRecycler.invalidate();
                } else {
                    mNoCommentsNotice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {
                Snackbar.make(toolbar, "There was a error loading comments for this image!", Snackbar.LENGTH_LONG).show();
                mLoadingNotice.setVisibility(View.GONE);
            }

            @Override
            public void onDone() {
                //Set the loading spinner gone either case
                mLoadingNotice.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image_view, menu);

        Drawable drawable_view_in_web = menu.findItem(R.id.menu_view_in_web).getIcon();
        drawable_view_in_web = DrawableCompat.wrap(drawable_view_in_web);
        DrawableCompat.setTint(drawable_view_in_web, ContextCompat.getColor(this, android.R.color.white));

        Drawable drawable_reload = menu.findItem(R.id.menu_img_view_reload).getIcon();
        drawable_reload = DrawableCompat.wrap(drawable_reload);
        DrawableCompat.setTint(drawable_reload, ContextCompat.getColor(this, android.R.color.white));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_view_in_web:
                CustomTabManager.openInCustomTab(this, medium.link);
                return true;
            case R.id.menu_info:
                Bottomsheet.openInfoBottomSheet(this, medium);
                return true;
            case R.id.menu_img_view_reload:
                updateLikes();
                updateRecycler();
                return true;
            case R.id.menu_save_img:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {
        //TODO: Use some YT Android threading shenangigans to prevent lag while saving :*
        try {
            photoView.setDrawingCacheEnabled(true);
            photoView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            Bitmap bitmap = photoView.getDrawingCache();

            String s = "lensico_" + medium.mediumId + ".png";

            File f = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath(), s);

            FileOutputStream fos = null;
            fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            bitmap.recycle();
            Toast.makeText(this, "Saved Image at " + f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

}
