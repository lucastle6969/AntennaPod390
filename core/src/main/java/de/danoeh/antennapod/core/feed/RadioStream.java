package de.danoeh.antennapod.core.feed;

import android.database.Cursor;

import de.danoeh.antennapod.core.storage.PodDBAdapter;

public class RadioStream {

    private long id;
    private String title;
    private String url;

    public RadioStream(long id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public static RadioStream fromCursor(Cursor cursor) {
        int indexId = cursor.getColumnIndex(PodDBAdapter.KEY_ID);
        int indexTitle = cursor.getColumnIndex(PodDBAdapter.KEY_RADIO_TITLE);
        int indexUrl = cursor.getColumnIndex(PodDBAdapter.KEY_RADIO_URL);

        return new RadioStream(
                cursor.getLong(indexId),
                cursor.getString(indexTitle),
                cursor.getString(indexUrl)
        );
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
