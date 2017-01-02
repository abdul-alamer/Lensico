package de.gerogerke.android.lensico.android.recycler;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.android.UserTextView;
import de.gerogerke.android.lensico.glide.GlideUtil;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramComment;
import de.gerogerke.android.lensico.util.StringUtil;

/**
 * Created by Deutron on 19.03.2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsViewHolder> {

    private SortedList<InstagramComment> mDataset;
    private Context mContext;

    public CommentsAdapter(Context mContext, List<InstagramComment> comments) {
        this.mContext = mContext;
        this.mDataset = new SortedList<>(InstagramComment.class, new SortedList.Callback<InstagramComment>() {
            @Override
            public int compare(InstagramComment o1, InstagramComment o2) {
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
            public boolean areContentsTheSame(InstagramComment oldItem, InstagramComment newItem) {
                return oldItem.commentText.equalsIgnoreCase(newItem.commentText);
            }

            @Override
            public boolean areItemsTheSame(InstagramComment oldItem, InstagramComment newItem) {
                return oldItem.commentedAt == newItem.commentedAt && oldItem.commenter.id == newItem.commenter.id && oldItem.commentText.equalsIgnoreCase(newItem.commentText);
            }
        });
        addAll(comments);
    }

    public InstagramComment get(int position) {
        return mDataset.get(position);
    }

    public int add(InstagramComment item) {
        return mDataset.add(item);
    }

    public int indexOf(InstagramComment item) {
        return mDataset.indexOf(item);
    }

    public void updateItemAt(int index, InstagramComment item) {
        mDataset.updateItemAt(index, item);
    }

    public void addAll(List<InstagramComment> items) {
        mDataset.beginBatchedUpdates();
        for (InstagramComment item : items) {
            mDataset.add(item);
        }
        mDataset.endBatchedUpdates();
    }

    public void addAll(InstagramComment[] items) {
        addAll(Arrays.asList(items));
    }

    public boolean remove(InstagramComment item) {
        return mDataset.remove(item);
    }

    public InstagramComment removeItemAt(int index) {
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
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view, parent, false);
        CommentsViewHolder vh = new CommentsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CommentsViewHolder holder, int position) {
        final InstagramComment comment = mDataset.get(position);
        GlideUtil.loadRound(mContext, comment.commenter.picUri, holder.commenterPic);
        String[] words = comment.commentText.split("(?= )");
        for(String word : words) {
            if(word.startsWith("@")) {
                //We found a @username
                UserTextView utv = new UserTextView(mContext);
                utv.setText(word);
                holder.commentViewWrapper.addView(utv);
            } else if(word.startsWith("#")) {
                TextView tv = new TextView(mContext);
                tv.setText(word);
                tv.setTextColor(Color.BLUE);
                holder.commentViewWrapper.addView(tv);
            } else if (isWordUrl(word)) {
                TextView tv = new TextView(mContext);
                tv.setText(word);
                tv.setTextColor(Color.RED);
                holder.commentViewWrapper.addView(tv);
            } else {
                TextView tv = new TextView(mContext);
                tv.setText(word);
                holder.commentViewWrapper.addView(tv);
            }
        }
        holder.commentedAt.setText(StringUtil.getTimeAgo(mContext, Long.parseLong(comment.commentedAt + "000")));
        holder.commenter.setText(comment.commenter.uName);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public boolean isWordUrl(String word) {
        try {
            Pattern regex = Pattern.compile("\\b(?:(https?|ftp|file)://|www\\.)?[-A-Z0-9+&#/%?=~_|$!:,.;]*[A-Z0-9+&@#/%=~_|$]\\.[-A-Z0-9+&@#/%?=~_|$!:,.;]*[A-Z0-9+&@#/%=~_|$]", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(word);
            return regexMatcher.matches();
        } catch (PatternSyntaxException ex) {
            return false;
        }

    }

}
