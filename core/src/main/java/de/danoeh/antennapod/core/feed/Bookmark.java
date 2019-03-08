package de.danoeh.antennapod.core.feed;

public class Bookmark {

    private String title;
    private long id;
    private int timestamp;
    private String podcastTitle;
    private String uid;

    public Bookmark(long id, String title, int timestamp, String podcastTitle, String uid){
        this.id = id;
        this.title = title;
        this.timestamp = timestamp;
        this.podcastTitle = podcastTitle;
        this.uid = uid;
    }

    public long getId(){
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getPodcastTitle() {
        return podcastTitle;
    }

    public void setPodcastTitle(String podcastTitle) {
        this.podcastTitle = podcastTitle;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public int getTimestamp(){
        return this.timestamp;
    }

    public void setTimestamp(int timestamp){
        this.timestamp = timestamp;
    }
}