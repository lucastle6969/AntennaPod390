package de.danoeh.antennapod.core.achievements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;

public class AchievementManager {
    private static AchievementManager achievementManager;
    private ConcurrentHashMap<String, Achievement> achievements;
    private AchievementUnlocked animator;

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
        Log.d("A", achievementManager.achievements.get("Elementary").getName());
        return achievementManager;
    }

    public Achievement getAchievement(String achievementName) {
        return achievements.get(achievementName);
    }

    public boolean complete(String achievementName, Context context) {
        Achievement achv = getInstance(new AchievementUnlocked(context)).getAchievement(achievementName);
        if((achv != null) && achv.complete()) {
            AchievementData data = prepareAnimator(achv, context);
            Log.d("A", "about to animate");
            animator.show(data);
            return true;
        }
        return false;
    }

    public boolean increment(String achievementName, Context context, int counter) {
        Achievement achv = getInstance(new AchievementUnlocked(context)).getAchievement(achievementName);
        if((achv != null) && achv.increment(counter)) {
            AchievementData data = prepareAnimator(achv, context);
            Log.d("A", "about to animate");
            animator.show(data);
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
                iv.setImageResource(android.R.drawable.btn_star);
                return iv.getDrawable();
            case 2:
                iv.setImageResource(android.R.drawable.ic_delete);
                return iv.getDrawable();
            case 3:
                iv.setImageResource(android.R.drawable.ic_menu_edit);
                return iv.getDrawable();
            default:
                return null;

        }
    }

}
