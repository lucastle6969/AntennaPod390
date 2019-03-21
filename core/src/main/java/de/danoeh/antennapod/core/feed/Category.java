package de.danoeh.antennapod.core.feed;

import android.database.Cursor;

public class Category {

    private long id;
    private String name;

    public Category(long id, String name){
        this.id = id;
        this.name = name;
    }

    public static Category fromCursor(Cursor cursor) {
        // TODO create category objects using the cursor returned from the db
        return null;
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
