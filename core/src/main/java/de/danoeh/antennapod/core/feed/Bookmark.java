package de.danoeh.antennapod.core.feed;

import android.database.Cursor;

import de.danoeh.antennapod.core.storage.PodDBAdapter;


public class Bookmark {

    private long id;                // Unique identifier set by the db
    private String title;           // Description of bookmark
    private int timestamp;          // Bookmark timestamp
    private String podcastTitle;    // Name of podcast
    private String uid;             // Unique identifier for episode

    public Bookmark(long id, String title, int timestamp, String podcastTitle, String uid){
        this.id = id;
        this.title = title;
        this.timestamp = timestamp;
        this.podcastTitle = podcastTitle;
        this.uid = uid;
    }

    public static Bookmark fromCursor(Cursor cursor) {
        int indexId = cursor.getColumnIndex(PodDBAdapter.KEY_ID);
        int indexBookmarkTitle = cursor.getColumnIndex(PodDBAdapter.KEY_BOOKMARK_TITLE);
        int indexBookmarkTimestamp = cursor.getColumnIndex(PodDBAdapter.KEY_BOOKMARK_TIMESTAMP);
        int indexBookmarkPodcast = cursor.getColumnIndex(PodDBAdapter.KEY_BOOKMARK_PODCAST);
        int indexBookmarkUid = cursor.getColumnIndex(PodDBAdapter.KEY_BOOKMARK_UID);

        return new Bookmark(
            cursor.getLong(indexId),
            cursor.getString(indexBookmarkTitle),
            cursor.getInt(indexBookmarkTimestamp),
            cursor.getString(indexBookmarkPodcast),
            cursor.getString(indexBookmarkUid)
        );
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