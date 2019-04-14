package de.danoeh.antennapod.core.achievements;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.core.storage.DBWriter;

public class AchievementBuilder {

    // Achievement names
    public static final String SEARCH_CAT_ACHIEVEMENT = "Elementary";
    public static final String POTD_ACHIEVEMENT = "POTD";
    public static final String POTD_7_ACHIEVEMENT = "The First 7";
    public static final String BKMK_ACHIEVEMENT = "Book Keeper";
    public static final String BKMK_10_ACHIEVEMENT = "The Librarian";
    public static final String CAT_ACHIEVEMENT = "Keep It Tidy";
    public static final String CAT_7_ACHIEVEMENT = "O.C.D.";
    public static final String SUBSCRIBE_ACHIEVEMENT = "Sign Me Up";
    public static final String SUBSCRIBE_10_ACHIEVEMENT = "The Observer";
    public static final String MOD_CAT_ACHIEVEMENT = "Second Thoughts";
    public static final String MOD_BKMK_ACHIEVEMENT = "Getting Specific";
    public static final String SEARCH_ITUNES_ACHIEVEMENT = "Gimme What I Want";
    public static final String SEARCH_GPOD_ACHIEVEMENT = "Turn a New Leaf Over";
    public static final String SEARCH_FYYD_ACHIEVEMENT = "Into The Abyss";
    public static final String SEARCH_BY_URL_ACHIEVEMENT = "Enter the Matrix";
    public static final String SEARCH_ACHIEVEMENT = "Sleuth";
    public static final String CREATE_ACHIEVEMENT = "The Creator";
    public static final String MODIFY_ACHIEVEMENT = "Director's Cut";
    public static final String ALL_ACHIEVEMENTS_COMPLETE = "Over Achiever";

    // define the parameters for each achievement once and write them to the db
    public static void buildAchievements(){

        List<Achievement> achievements = new ArrayList<>();

        Achievement achievement = new Achievement(
                SEARCH_CAT_ACHIEVEMENT, // Achievement name  (static final defined above).
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Search for a podcast on the subscription page", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                POTD_ACHIEVEMENT,
                1,
                1,
                "Visit the podcast of the day page",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                POTD_7_ACHIEVEMENT,
                7,
                2,
                "Visit a new podcast of the day page 7 times",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                BKMK_ACHIEVEMENT,
                1,
                1,
                "Add a bookmark to a podcast episode",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                BKMK_10_ACHIEVEMENT,
                10,
                2,
                "Add 10 bookmarks",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                CAT_ACHIEVEMENT,
                1,
                1,
                "Create a category in subscriptions",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                CAT_7_ACHIEVEMENT,
                7,
                2,
                "Create 7 categories",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SUBSCRIBE_ACHIEVEMENT,
                1,
                1,
                "Subscribe to a podcast",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SUBSCRIBE_10_ACHIEVEMENT,
                10,
                2,
                "Subscribe to 10 podcasts",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                CREATE_ACHIEVEMENT,
                1,
                3,
                "Create a bookmark and a category",
                1
        );
        achievements.add(achievement);

        achievement = new Achievement(
                MODIFY_ACHIEVEMENT,
                1,
                3,
                "Modify a bookmark and a category",
                1
        );
        achievements.add(achievement);

        achievement = new Achievement(
                MOD_BKMK_ACHIEVEMENT,
                1,
                2,
                "Modify a bookmark",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                MOD_CAT_ACHIEVEMENT,
                1,
                2,
                "Modify a category",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SEARCH_ITUNES_ACHIEVEMENT,
                1,
                1,
                "Search for a podcast using iTunes provider",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SEARCH_GPOD_ACHIEVEMENT,
                1,
                1,
                "Search for a podcast using gpodder provider",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SEARCH_FYYD_ACHIEVEMENT,
                1,
                1,
                "Search for a podcast using fyyd provider",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SEARCH_BY_URL_ACHIEVEMENT,
                1,
                2,
                "Search for a podcast by URL",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                SEARCH_ACHIEVEMENT,
                1,
                3,
                "Unlock all search achievements",
                1
        );
        achievements.add(achievement);

        // add a new achievements here

        achievement = new Achievement(
                ALL_ACHIEVEMENTS_COMPLETE,
                1,
                3,
                "Unlock all achievements",
                0
        );
        achievements.add(achievement);

        for(Achievement achv: achievements){
            DBWriter.setAchievement(achv);
        }

    }

    public static void buildAchievementsForTesting(){
        List<Achievement> achievements = new ArrayList<>();

        Achievement achievement = new Achievement(
                SEARCH_CAT_ACHIEVEMENT, // Achievement name  (static final defined above).
                1, // goal: represents the number the counter should reach before unlocking achievement
                1, // rank of the achievement (1: low, 2: medium, 3: high)
                "Search for a podcast on the subscription page", // short description of the achievement
                0 // if the achievement should remain hidden until unlocked (1: hidden , 0: not hidden)
        );
        achievements.add(achievement);

        achievement = new Achievement(
                CAT_ACHIEVEMENT,
                1,
                1,
                "Create a category in subscriptions",
                0
        );
        achievements.add(achievement);

        achievement = new Achievement(
                ALL_ACHIEVEMENTS_COMPLETE,
                1,
                3,
                "Unlock all achievements",
                0
        );
        achievements.add(achievement);

        for(Achievement achv: achievements){
            DBWriter.setAchievement(achv);
        }

    }

}
