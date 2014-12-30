package com.aadisriram.quicktact.DataClasses;

/**
 * Created by aadisriram on 12/29/14.
 */
public class SocialDataManager {

    private int userId;
    private String facebookId;

    private String twitterId;
    private String facebookName;

    private String googlePlusId;
    private String googlePlusUsername;

    public SocialDataManager() {
    }

    public SocialDataManager(int userId, String facebookId,
                             String twitterId, String facebookName,
                             String googlePlusId, String googlePlusUsername) {
        this.twitterId = twitterId;
        this.userId = userId;
        this.facebookId = facebookId;
        this.facebookName = facebookName;
        this.googlePlusId = googlePlusId;
        this.googlePlusUsername = googlePlusUsername;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public String getFacebookName() {
        return facebookName;
    }

    public void setFacebookName(String facebookName) {
        this.facebookName = facebookName;
    }

    public String getGooglePlusId() {
        return googlePlusId;
    }

    public void setGooglePlusId(String googlePlusId) {
        this.googlePlusId = googlePlusId;
    }

    public String getGooglePlusUsername() {
        return googlePlusUsername;
    }

    public void setGooglePlusUsername(String googlePlusUsername) {
        this.googlePlusUsername = googlePlusUsername;
    }
}
