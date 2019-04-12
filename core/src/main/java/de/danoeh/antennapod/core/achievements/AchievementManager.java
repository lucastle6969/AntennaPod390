package de.danoeh.antennapod.core.achievements;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.core.R;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.preferences.UserPreferences;

public class AchievementManager {
    private static AchievementManager achievementManager;
    private ConcurrentHashMap<String, Achievement> achievements;
    private AchievementUnlocked animator;
    private List<AchievementData> animatorDateQueue;

    public enum PlayerRank {
        LVL_ONE("Podcast Rookie"),
        LVL_TWO("Podcast Joe"),
        LVL_THREE("Podcast Addict"),
        LVL_FOUR("Podcast Hero"),
        LVL_FIVE("Podcast Guru");

        private String description;

        PlayerRank(String s) {
            description = s;
        }

        public String getDescription() {
            return description;
        }

    }

    private AchievementManager(AchievementUnlocked animator){
        achievements = DBReader.getAchievements();
        if(achievements.isEmpty()){
            // we need to load the achievements into the db for the first time
            AchievementBuilder.buildAchievements();
        }
    }

    public static AchievementManager getInstance(AchievementUnlocked animator) {
        if(achievementManager == null) {
            achievementManager = new AchievementManager(animator);
        }
        achievementManager.animator = animator;
        return achievementManager;
    }

    public static AchievementManager getInstance() {
        return achievementManager;
    }

    public Achievement getAchievement(String achievementName) {
        return achievements.get(achievementName);
    }

    public ConcurrentHashMap<String, Achievement> getAchievements() {
        return achievements;
    }

    public boolean increment(List<String> achievementNames, Context context) {
        if(UserPreferences.getAchievementsToggle()) {
            animatorDateQueue = new ArrayList<>();
            for (String achievementName : achievementNames) {
                Achievement achievement = getAchievement(achievementName);
                if ((achievement != null) && achievement.increment()) {
                    animatorDateQueue.add(prepareAnimator(achievement, context));
                }
            }
            if (checkAllAchievementsComplete()) {
                animatorDateQueue.add(prepareAnimator(getAchievement(AchievementBuilder.ALL_ACHIEVEMENTS_COMPLETE), context));
            }
            if (!animatorDateQueue.isEmpty()) {
                for (AchievementData data : animatorDateQueue) {
                    animator.show(data);
                    playUnlockTune(context);
                }
                return true;
            }
        }
        return false;
    }

    public void playUnlockTune(Context context) {

        try {
            MediaPlayer m = new MediaPlayer();
            AssetFileDescriptor descriptor = context.getAssets().openFd("zelda_treasure_chest.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.setLooping(false);
            m.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCombinations(){
        if(checkCreator())
            return true;
        return false;
    }

    public boolean checkAllAchievementsComplete() {
        int count = 0;
        for(Achievement achv: achievements.values()){
            if(achv.getDate()==null)
                break;
            count++;
        }
        if(count >= achievements.size()-1){
            Achievement allAchv = achievements.get(AchievementBuilder.ALL_ACHIEVEMENTS_COMPLETE);
            allAchv.setAllComplete();
            return true;
        }
        return false;
    }

    private AchievementData prepareAnimator(Achievement achv, Context context) {
        AchievementData data = new AchievementData();
        data.setTitle(achv.getName());
        data.setSubtitle(achv.getDescription());
        data.setIcon(iconFactory(achv.getRank(), context));
        data.setTextColor(Color.BLACK);
        data.setIconBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        data.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        animator.setRounded(false).setLarge(true).setTopAligned(true).setDismissible(true);
        return data;
    }

    private Drawable iconFactory(int rank, Context context) {
        ImageView iv = new ImageView(context);
        switch (rank) {
            case 1:
                iv.setImageResource(R.drawable.ic_achievement_star_1);
                return iv.getDrawable();
            case 2:
                iv.setImageResource(R.drawable.ic_achievement_star_2);
                return iv.getDrawable();
            case 3:
                iv.setImageResource(R.drawable.ic_achievement_star_3);
                return iv.getDrawable();
            default:
                return null;
        }
    }

    public int getTotalRank() {
        int totalRank = 0;
        for(Achievement achv: achievements.values()) {
            if(achv.getDate() != null) {
                totalRank += achv.getRank();
            }
        }
        return totalRank;
    }

    public String getRankTitle(int userRank) {
        int totalRank = 0;
        for(Achievement achv: achievements.values()) {
            totalRank += achv.getRank();
        }
        double ratio = ((double)userRank / (double)totalRank) * 100;
        if (ratio <= 20) {
            return PlayerRank.LVL_ONE.getDescription();
        } else if (ratio <= 40) {
            return PlayerRank.LVL_TWO.getDescription();
        } else if (ratio <= 60) {
            return PlayerRank.LVL_THREE.getDescription();
        } else if (ratio <= 80) {
            return PlayerRank.LVL_FOUR.getDescription();
        } else if (ratio <= 100) {
            return PlayerRank.LVL_FIVE.getDescription();
        } else {
            return "ERROR";
        }
    }

    public void resetAchievements() {
        DBWriter.resetAchievements();
        for(Achievement achv: achievements.values()) {
            achv.setDate(null);
            achv.setCounter(0);
        }
    }

    private boolean checkCreator(){
        if(achievements.get(AchievementBuilder.CAT_ACHIEVEMENT).getDate()!= null && achievements.get(AchievementBuilder.BKMK_ACHIEVEMENT).getDate()!= null)
            return true;
        return false;
    }

}
