package de.danoeh.antennapod.core.feed;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

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
        // TODO create category objects using the cursor returned from the db
        return null;
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
