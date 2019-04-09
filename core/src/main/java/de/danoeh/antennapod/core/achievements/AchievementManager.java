package de.danoeh.antennapod.core.achievements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.core.R;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;

public class AchievementManager {
    private static AchievementManager achievementManager;
    private ConcurrentHashMap<String, Achievement> achievements;
    private AchievementUnlocked animator;
    private List<AchievementData> animatorDateQueue;


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

    public boolean increment(List<String> achievementNames, Context context) {
        animatorDateQueue = new ArrayList<>();
        for(String achievementName: achievementNames){
            Achievement achievement = getAchievement(achievementName);
            if((achievement != null) && achievement.increment()){
                animatorDateQueue.add(prepareAnimator(achievement, context));
            }
        }
        if(checkAllAchievementsComplete()){
            animatorDateQueue.add(prepareAnimator(getAchievement(AchievementBuilder.ALL_ACHIEVEMENTS_COMPLETE), context));
        }
        if(!animatorDateQueue.isEmpty()){
            for(AchievementData data: animatorDateQueue){
                animator.show(data);
            }
            return true;
        }
        return false;
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
        data.setPopUpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

    private boolean checkCreator(){
        if(achievements.get(AchievementBuilder.CAT_ACHIEVEMENT).getDate()!= null && achievements.get(AchievementBuilder.BKMK_ACHIEVEMENT).getDate()!= null)
            return true;
        return false;
    }

}
