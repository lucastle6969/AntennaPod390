package de.danoeh.antennapod.core.achievements;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.core.achievements.Achievement;
import de.danoeh.antennapod.core.storage.DBWriter;

public class AchievementBuilder {

    public static void buildAchievements(){
        List<Achievement> achievements = new ArrayList<>();

        //Achievements are hard coded into the app here

        Achievement achievement = new Achievement(
                "Elementary", // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Search for a podcast on the subscription page", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);
        achievement = new Achievement(
                "The First 7", // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                7, // goal: represents the number the counter should reach before unlocking achievement
                2, // rank of the achievement (1: low, 2: medium, 3: high)
                "Visit a new podcast of the day page 7 times", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);
        achievement = new Achievement(
                "POTD", // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Visit the podcast of the day page", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        // add a new achievements here

        for(Achievement achv: achievements){
            Log.d("ACHIEVEMENTS", "started loop");
            DBWriter.setAchievement(achv);
        }

    }

}
