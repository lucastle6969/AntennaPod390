package de.danoeh.antennapod.core.feed;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.danoeh.antennapod.core.storage.DBWriter;

public class AchievementBuilder {

    public static void buildAchievements(){
        List<Achievement> achievements = new ArrayList<>();

        //Achievements are hard coded into the app here

        Achievement achievement1 = new Achievement(
                "The First Achievement", // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                0, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "bla bla bla", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement1);

        Achievement achievement2 = new Achievement(
                "The Second Achievement", // Achievement name
                new Date(1979,12,11,2,20), // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                0, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "blo blo blo", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement2);

        Achievement achievement3 = new Achievement(
                "The Third Achievement", // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                0, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "blu blu blu", // short description of the achievement
                1 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement3);

        // add a new achievement here

        for(Achievement achievement: achievements){
            DBWriter.setAchievement(achievement);
        }

    }

}
