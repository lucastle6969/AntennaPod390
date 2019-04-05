package de.danoeh.antennapod.core.feed;

import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.core.storage.DBReader;

public class AchievementManager {
    private static AchievementManager achievementManager;
    private ConcurrentHashMap<String, Achievement> achievements;

    private AchievementManager(){
        achievements = DBReader.getAchievements();
    }

    public static AchievementManager getInstance(){
        if(achievementManager == null){
            achievementManager = new AchievementManager();
        }
        return achievementManager;
    }

    public String getStatus(){
        if(achievements.isEmpty()){
            AchievementBuilder.buildAchievements();
            achievements = DBReader.getAchievements();
            return "Achievements Empty";
        }

        return "Achievements Not Empty " + achievements.get("The First Achievement").getDescription();
    }

}
