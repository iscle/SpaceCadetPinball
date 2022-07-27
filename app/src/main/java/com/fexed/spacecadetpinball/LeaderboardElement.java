package com.fexed.spacecadetpinball;

import java.util.Date;

public class LeaderboardElement {
    public String username;
    public String uid;
    public Date lastUploaded;
    public int normalScore;
    public int cheatScore;

    public LeaderboardElement(String username, String uid, Date lastUploaded, int normalScore, int cheatScore) {
        this.username = username;
        this.uid = uid;
        this.lastUploaded = lastUploaded;
        this.normalScore = normalScore;
        this.cheatScore = cheatScore;
    }

    @Override
    public String toString() {
        return "LeaderboardElement{" +
                "username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                ", lastUploaded=" + lastUploaded +
                ", normalScore=" + normalScore +
                ", cheatScore=" + cheatScore +
                '}';
    }
}
