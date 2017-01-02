package de.gerogerke.android.lensico.android.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.gerogerke.android.lensico.R;

/**
 * Created by Deutron on 19.03.2016.
 */
public class CommentsViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.comment_view_photo)
    public ImageView commenterPic;

    @Bind(R.id.comment_view_comment_container)
    public ViewGroup commentViewWrapper;

    @Bind(R.id.comment_view_commenter_name)
    public TextView commenter;

    @Bind(R.id.comment_view_commented_time)
    public TextView commentedAt;

    public CommentsViewHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

}