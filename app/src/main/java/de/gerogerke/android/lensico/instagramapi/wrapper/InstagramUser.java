package de.gerogerke.android.lensico.instagramapi.wrapper;

/**
 * Created by Deutron on 27.12.2015.
 */
public class InstagramUser {
    String tagID;
    String profilePictureUrl;
    String username;
    String bio;
    String website;
    String fullName;

    String follows;
    String followedBy;
    String media;

    public InstagramUser(String tagID, String profilePictureUrl, String username, String bio, String website, String fullName, String follows, String followedBy, String media) {
        this.tagID = tagID;
        this.profilePictureUrl = profilePictureUrl;
        this.username = username;
        this.bio = bio;
        this.website = website;
        this.fullName = fullName;
        this.follows = follows;
        this.followedBy = followedBy;
        this.media = media;
    }

    public String getTagID() {
        return tagID;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite() {
        return website;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFollows() {
        return follows;
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public String getMedia() {
        return media;
    }

    @Override
    public String toString() {
        return "USER: " + username
                + " | BIO" + bio
                + " | FULLN: " + fullName;
    }

    public SimpleInstagramUser toSimpleUser() {
        SimpleInstagramUser simpleUser = new SimpleInstagramUser();
        simpleUser.id = this.tagID;
        simpleUser.picUri = this.profilePictureUrl;
        simpleUser.uName = this.username;

        return simpleUser;
    }
}