package de.danoeh.antennapod.core.achievements;

import android.database.Cursor;
import android.graphics.Color;

import java.util.Date;

import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.R;
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

    public boolean complete() {
        if(this.date == null) {
            Date date = new Date();
            this.date = date;
            DBWriter.updateAchievement(this);
            return true;
        }
        return false;
    }

    public boolean increment() {
        switch(this.rank){
            case 1:
            case 2:
                if(this.date == null) {
                    this.counter ++;
                    DBWriter.updateAchievement(this);
                    if(this.counter >= goal) {
                        return this.complete();
                    }
                }
                break;
            case 3:
                if(AchievementManager.getInstance().checkCombinations()){
                    if(this.date == null) {
                        this.counter++;
                        if(this.counter >= goal){
                            return this.complete();
                        }
                    }
                }
                break;
        }
        return false;
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

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter(){
        return counter;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Date getDate(){
        return date;
    }

    public void setAllComplete() {
        if(name.equals(AchievementBuilder.ALL_ACHIEVEMENTS_COMPLETE)){
            this.date = new Date();
            this.counter++;
            DBWriter.updateAchievement(this);
        }
    }

    public String getDateText(){
        if(date!=null){
            return date.toString();
        }else return "Achievement  incomplete";
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

    public boolean getIsHidden(){
        return hidden > 0;
    }

    public String getDisplayDescription() {
        if(date==null && getIsHidden()){
            return "? ? ?";
        }
        else return getDescription();
    }

    public String getDisplayName() {
        if(date==null && getIsHidden()){
            return "? ? ?";
        }
        else return getName();
    }

    public int getIconResource(){
        if(date==null){
            return R.drawable.ic_achievement_locked;
        }
        switch(rank){
            case 1:
                return R.drawable.ic_achievement_star_1;
            case 2:
                return R.drawable.ic_achievement_star_2;
            case 3:
                return R.drawable.ic_achievement_star_3;
            default:
                return R.drawable.ic_achievement_star_1;
        }
    }

    public int getBackgroundColor(){
        if(date!=null){
            return Color.GREEN;
        }else return Color.LTGRAY;
    }
}

