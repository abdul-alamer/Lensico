package de.gerogerke.android.lensico.instagramapi.wrapper;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Deutron on 27.12.2015.
 */
public class InstagramMedium implements Serializable, Comparable<InstagramMedium> {
    public int commentCount;
    public HashMap<String, PositionPair> usersInPhoto = new HashMap<>();
    public String lowResUrl;
    public String thumbnailUrl;
    public String defResUrl;
    public String filter;
    public int likesCount;
    public int createdTime;
    public String link;
    public String captionText = "";
    public boolean userHasLiked;
    public String userID;
    public String username;
    public String userPic;
    public String mediumId;

    @Override
    public int compareTo(InstagramMedium another) {
        return createdTime + another.createdTime;
    }
}