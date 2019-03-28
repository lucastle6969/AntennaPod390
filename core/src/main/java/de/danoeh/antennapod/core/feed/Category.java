package de.danoeh.antennapod.core.feed;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.core.storage.PodDBAdapter;

public class Category {

    private long id;
    private String name;
    private List<Long> feedIds;

    public Category(long id, String name){
        this.id = id;
        this.name = name;
        feedIds = new ArrayList<>();
    }

    public static Category fromCursor(Cursor cursor) {
        int indexId = cursor.getColumnIndex(PodDBAdapter.KEY_CATEGORY_ID);
        int indexName = cursor.getColumnIndex(PodDBAdapter.KEY_CATEGORY_NAME);

        return new Category(
            cursor.getLong(indexId),
            cursor.getString(indexName)
        );
    }

    public void addFeedId(long id) {
        if (!feedIds.contains(id)) {
            feedIds.add(id);
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
