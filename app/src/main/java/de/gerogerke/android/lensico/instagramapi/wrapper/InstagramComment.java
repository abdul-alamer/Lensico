package de.gerogerke.android.lensico.instagramapi.wrapper;

/**
 * Created by Deutron on 27.03.2016.
 */
public class InstagramComment implements Comparable<InstagramComment> {

    public SimpleInstagramUser commenter;
    public String commentText;
    public int commentedAt;

    @Override
    public int compareTo(InstagramComment another) {
        return commentedAt - another.commentedAt;
    }
}
