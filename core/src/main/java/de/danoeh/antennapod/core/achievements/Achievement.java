package de.danoeh.antennapod.core.achievements;

import android.database.Cursor;

import java.util.Date;

import de.danoeh.antennapod.core.storage.PodDBAdapter;

public class Achievement {

    private long id;
    private String name;
    private Date date;
    private int counter;
    private int goal;
    private int rank;
    private String description;
    private int hidden;

    public Achievement(long id, String name, Date date, int counter, int goal, int rank, String description, int hidden){
        this.id = id;
        this.name = name;
        this.date = date;
        this.counter = counter;
        this.goal = goal;
        this.rank = rank;
        this.description = description;
        this.hidden = hidden;
    }

    public Achievement(String name, Date date, int counter, int goal, int rank, String description, int hidden){
        this.name = name;
        this.date = date;
        this.counter = counter;
        this.goal = goal;
        this.rank = rank;
        this.description = description;
        this.hidden = hidden;
    }


    public static Achievement fromCursor(Cursor cursor) {
        int indexId = cursor.getColumnIndex(PodDBAdapter.KEY_ID);
        int indexAchievementName = cursor.getColumnIndex(PodDBAdapter.KEY_ACHIEVEMENT_NAME);
        int indexAchievementDate = cursor.getColumnIndex(PodDBAdapter.KEY_ACHIEVEMENT_DATE);
        int indexAchievementCounter = cursor.getColumnIndex(PodDBAdapter.KEY_ACHIEVEMENT_COUNTER);
        int indexAchievementGoal = cursor.getColumnIndex(PodDBAdapter.KEY_ACHIEVEMENT_GOAL);
        int indexAchievementRank = cursor.getColumnIndex(PodDBAdapter.KEY_ACHIEVEMENT_RANK);
        int indexAchievementDescription = cursor.getColumnIndex(PodDBAdapter.KEY_ACHIEVEMENT_DESCRIPTION);
        int indexAchievementHidden = cursor.getColumnIndex(PodDBAdapter.KEY_ACHIEVEMENT_HIDDEN);

        long dateAsMillis = cursor.getLong(indexAchievementDate);
        Date date;
        if(dateAsMillis == 0){
            date = null;
        }else{
            date = new Date(dateAsMillis);
        }

        return new Achievement(
                cursor.getLong(indexId),
                cursor.getString(indexAchievementName),
                date,
                cursor.getInt(indexAchievementCounter),
                cursor.getInt(indexAchievementGoal),
                cursor.getInt(indexAchievementRank),
                cursor.getString(indexAchievementDescription),
                cursor.getInt(indexAchievementHidden)
        );
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getCounter(){
        return counter;
    }

    public Date getDate(){
        return date;
    }

    public long getDateAsMilliSeconds(){
        if(date != null) {
            return date.getTime();
        }
        return 0;
    }

    public int getGoal(){
        return goal;
    }

    public int getRank(){
        return rank;
    }

    public String getDescription(){
        return description;
    }

    public int getHidden(){
        return hidden;
    }
}

