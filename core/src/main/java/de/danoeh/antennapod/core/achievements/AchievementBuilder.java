package de.danoeh.antennapod.core.achievements;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.core.achievements.Achievement;
import de.danoeh.antennapod.core.storage.DBWriter;

public class AchievementBuilder {

    public static final String SEARCH_ACHIEVEMENT = "Elementary";
    public static final String POTD_ACHIEVEMENT = "POTD";
    public static final String POTD_7_ACHIEVEMENT = "The First 7";
    public static final String BKMK_ACHIEVEMENT = "Book Keeper";
    public static final String BKMK_10_ACHIEVEMENT = "The Librarian";
    public static final String CAT_ACHIEVEMENT = "Keep It Tidy";
    public static final String CAT_7_ACHIEVEMENT = "O.C.D.";
    public static final String SUBSCRIBE_ACHIEVEMENT = "Sign Me Up";
    public static final String SUBSCRIBE_10_ACHIEVEMENT = "The Observer";
    public static final String CREATE_ACHIEVEMENT = "The Creator";
    public static final String ALL_ACHIEVEMENTS_COMPLETE = "Over Achiever";

    public static void buildAchievements(){
        List<Achievement> achievements = new ArrayList<>();

        //Achievements are hard coded into the app here

        Achievement achievement = new Achievement(
                SEARCH_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Search for a podcast on the subscription page", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                POTD_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Visit the podcast of the day page", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                POTD_7_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                7, // goal: represents the number the counter should reach before unlocking achievement
                2, // rank of the achievement (1: low, 2: medium, 3: high)
                "Visit a new podcast of the day page 7 times", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                BKMK_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Add a bookmark to a podcast episode", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                BKMK_10_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                10, // goal: represents the number the counter should reach before unlocking achievement
                2, // rank of the achievement (1: low, 2: medium, 3: high)
                "Add 10 bookmarks", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                CAT_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Create a category in subscriptions", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                CAT_7_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                7, // goal: represents the number the counter should reach before unlocking achievement
                2, // rank of the achievement (1: low, 2: medium, 3: high)
                "Create 7 categories", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SUBSCRIBE_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Subscribe to a podcast", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SUBSCRIBE_10_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                10, // goal: represents the number the counter should reach before unlocking achievement
                2, // rank of the achievement (1: low, 2: medium, 3: high)
                "Subscribe to 10 podcasts", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                CREATE_ACHIEVEMENT, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                3, // rank of the achievement (1: low, 2: medium, 3: high)
                "Create a bookmark and a category", // short description of the achievement
                1 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        // add a new achievements here

        achievement = new Achievement(
                ALL_ACHIEVEMENTS_COMPLETE, // Achievement name
                null, // date should be null until the achievement is unlocked
                0, // counter should be 0 at initialization
                1, // goal: represents the number the counter should reach before unlocking achievement
                3, // rank of the achievement (1: low, 2: medium, 3: high)
                "Unlock all achievements", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        for(Achievement achv: achievements){
            DBWriter.setAchievement(achv);
        }

    }

}
