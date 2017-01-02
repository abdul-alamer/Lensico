package de.gerogerke.android.lensico.android.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.android.UserTextView;

/**
 * Created by Deutron on 19.03.2016.
 */
public class ImageCardViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.post_card_photo)
    public ImageView mPhotoView;

    @Bind(R.id.post_card_avatar)
    public ImageView mAvatarView;

    @Bind(R.id.post_card_username)
    public UserTextView mUsernameView;

    @Bind(R.id.post_card_time)
    public TextView mPublishTimeView;

    @Bind(R.id.post_card_heart)
    public ViewGroup mButtonLike;

    @Bind(R.id.post_card_likes_num)
    public TextView mButtonLikeCount;

    @Bind(R.id.post_card_comment_button)
    public ViewGroup mButtonComment;

    @Bind(R.id.post_card_comments_num)
    public TextView mButtonCommentCount;

    @Bind(R.id.post_card_repost)
    public ImageView mButtonRepost;

    @Bind(R.id.post_card_caption_text)
    public TextView captionTextView;

    @Bind(R.id.post_card_heart_img)
    public ImageView like_img;

    public View inflatedView;

    public ImageCardViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
        this.inflatedView = v;
    }

}

