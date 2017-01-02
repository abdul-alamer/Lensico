package de.gerogerke.android.lensico.android.recycler;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.activities.ActivityImageView;
import de.gerogerke.android.lensico.glide.GlideUtil;
import de.gerogerke.android.lensico.instagramapi.InstagramAPI;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramMedium;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskCallback;
import de.gerogerke.android.lensico.util.StringUtil;

/**
 * Created by Deutron on 19.03.2016.
 */
public class ImageCardsAdapter extends RecyclerView.Adapter<ImageCardViewHolder> {

    private SortedList<InstagramMedium> mDataset;
    private Activity mContext;

    public ImageCardsAdapter(Activity mContext, List<InstagramMedium> media) {
        this.mContext = mContext;
        this.mDataset = new SortedList<>(InstagramMedium.class, new SortedList.Callback<InstagramMedium>() {
            @Override
            public int compare(InstagramMedium o1, InstagramMedium o2) {
                return o1.compareTo(o2);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(InstagramMedium oldItem, InstagramMedium newItem) {
                return oldItem.mediumId.equalsIgnoreCase(newItem.mediumId);
            }

            @Override
            public boolean areItemsTheSame(InstagramMedium item1, InstagramMedium item2) {
                return item1.mediumId.equalsIgnoreCase(item2.mediumId);
            }
        });
        addAll(media);
    }

    public InstagramMedium get(int position) {
        return mDataset.get(position);
    }

    public int add(InstagramMedium item) {
        return mDataset.add(item);
    }

    public int indexOf(InstagramMedium item) {
        return mDataset.indexOf(item);
    }

    public void updateItemAt(int index, InstagramMedium item) {
        mDataset.updateItemAt(index, item);
    }

    public void addAll(List<InstagramMedium> items) {
        mDataset.beginBatchedUpdates();
        for (InstagramMedium item : items) {
            mDataset.add(item);
        }
        mDataset.endBatchedUpdates();
    }

    public void addAll(InstagramMedium[] items) {
        addAll(Arrays.asList(items));
    }

    public boolean remove(InstagramMedium item) {
        return mDataset.remove(item);
    }

    public InstagramMedium removeItemAt(int index) {
        return mDataset.removeItemAt(index);
    }

    public void clear() {
        mDataset.beginBatchedUpdates();
        while (mDataset.size() > 0) {
            mDataset.removeItemAt(mDataset.size() - 1);
        }
        mDataset.endBatchedUpdates();
    }

    @Override
    public ImageCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
        ImageCardViewHolder vh = new ImageCardViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ImageCardViewHolder holder, int position) {
        final InstagramMedium medium = mDataset.get(position);
        final GestureDetectorCompat detector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                startImageView(medium, holder.mPhotoView);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                toggleLike(holder.like_img, holder.mButtonLike, InstagramAPI.with(mContext).getConnection(), medium, holder.mButtonLikeCount);
                return true;
            }
        });

        //Like button onclicklistener
        holder.mButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(holder.like_img, holder.mButtonLike, InstagramAPI.with(mContext).getConnection(), medium, holder.mButtonLikeCount);
            }
        });

        holder.mPublishTimeView.setText(StringUtil.getTimeAgo(mContext, Long.parseLong(medium.createdTime + "000")));
        GlideUtil.load(mContext, medium.defResUrl, holder.mPhotoView);

        holder.mPhotoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (detector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });

        GlideUtil.loadRound(mContext, medium.userPic, holder.mAvatarView);
        holder.mUsernameView.setText(medium.username);
        holder.mButtonLikeCount.setText("" + medium.likesCount);
        holder.mButtonCommentCount.setText("" + medium.commentCount);
        holder.mButtonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Open Comment Activity/Fragment/BottomSheet", Snackbar.LENGTH_LONG).show();
            }
        });
        holder.mButtonRepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Repost something here!" + medium.mediumId, Snackbar.LENGTH_LONG).show();
            }
        });
        holder.like_img.setImageDrawable(ContextCompat.getDrawable(mContext, medium.userHasLiked ? R.drawable.heart : R.drawable.heart_outline));
        holder.like_img.setTag(medium.userHasLiked);
        if (!medium.captionText.isEmpty()) {
            holder.captionTextView.setText(medium.captionText);
            holder.captionTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void startImageView(InstagramMedium medium, View imageView) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = new Intent(mContext, ActivityImageView.class);
            i.putExtra("medium", medium);

            View sharedView = imageView;
            String transitionName = mContext.getString(R.string.animation_imageview);

            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(mContext, sharedView, transitionName);
            mContext.startActivity(i, transitionActivityOptions.toBundle());
        } else {
            Intent myIntent = new Intent(mContext, ActivityImageView.class);
            myIntent.putExtra("medium", medium);
            mContext.startActivity(myIntent);
        }
    }

    public void toggleLike(ImageView like_img, final ViewGroup mButtonLike, InstagramAPI.InstagramConnection connection, InstagramMedium medium, final TextView mButtonLikeCount) {
        //Set a tag
        boolean active = false;
        if (like_img.getTag() != null) {
            active = (boolean) like_img.getTag();
        }
        //Switch state on click
        active = !active;
        //Disable button until request is finished to prevent spamming
        mButtonLike.setClickable(false);
        if (active) {
            connection.like(medium.mediumId, new TaskCallback() {
                @Override
                public void onSuccess(Object object) {
                    //Increase like count
                    mButtonLikeCount.setText((Integer.parseInt(mButtonLikeCount.getText() + "") + 1) + "");
                }

                @Override
                public void onError() {
                    Toast.makeText(mContext, "Uh oh, something went wrong!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDone() {
                    //Set button clickable again
                    mButtonLike.setClickable(true);
                }
            });
        } else {
            connection.unlike(medium.mediumId, new TaskCallback() {
                @Override
                public void onSuccess(Object object) {
                    //Increase like count
                    mButtonLikeCount.setText((Integer.parseInt(mButtonLikeCount.getText() + "") - 1) + "");
                }

                @Override
                public void onError() {
                    Toast.makeText(mContext, "Uh oh, something went wrong!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDone() {
                    //Set button clickable again
                    mButtonLike.setClickable(true);
                }
            });
        }
        //Switch Image Drawable
        like_img.setImageDrawable(ContextCompat.getDrawable(mContext, active ? R.drawable.heart : R.drawable.heart_outline));
        //Set new tag
        like_img.setTag(active);
    }

}
