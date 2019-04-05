package de.danoeh.antennapod.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Achievement;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private ConcurrentHashMap<String,Achievement> achievementHashMap;
    private AchievementViewHolder view;

    public class AchievementViewHolder extends RecyclerView.ViewHolder {
        private ImageView achievementIcon;
        private TextView achievementName;
        private TextView achievementDescription;

        public AchievementViewHolder(View view){
            super(view);
        }

    }

    public AchievementsAdapter(ConcurrentHashMap<String, Achievement> achievementHashMap){
        this.achievementHashMap = achievementHashMap;
    }

    @Override
    public AchievementViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View achievementView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievement_container, parent, false);

        view = new AchievementViewHolder((achievementView));

        return view;
    }

    @Override
    public void onBindViewHolder(AchievementViewHolder holder, int position){

    }

    @Override
    public int getItemCount(){return achievementHashMap.size();}
}
