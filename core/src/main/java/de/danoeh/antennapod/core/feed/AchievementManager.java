package de.danoeh.antennapod.core.feed;

import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.core.storage.DBReader;

public class AchievementManager {
    private static AchievementManager achievementManager;
    private ConcurrentHashMap<String, Achievement> achievements;

    private AchievementManager(){

        achievements = DBReader.getAchievements();
        if(achievements.isEmpty()){
            // we need to load the achievements into the db for the first time
            AchievementBuilder.buildAchievements();
        }
    }

}
