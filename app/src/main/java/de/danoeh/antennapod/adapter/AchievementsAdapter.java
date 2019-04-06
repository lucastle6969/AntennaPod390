package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Achievement;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private Activity context;

    private List<Achievement> achievementList;
    private AchievementViewHolder view;

    public class AchievementViewHolder extends RecyclerView.ViewHolder {
        private ImageView achievementIcon;
        private TextView achievementName;
        private TextView achievementDescription;

        public AchievementViewHolder(View view){
            super(view);
            achievementName = view.findViewById(R.id.txtvAchievementName);
            achievementDescription = view.findViewById(R.id.txtvAchievementDescription);
            achievementIcon = view.findViewById(R.id.ivAchievementIcon);
        }

    }

    public void setContext(Activity context) {context = context;}

    public AchievementsAdapter(List<Achievement> achievementList){
        this.achievementList = achievementList;
    }

    @Override
    public AchievementViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View achievementView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievement_container, parent, false);

        view = new AchievementViewHolder(achievementView);

        return view;
    }

    public void onBindViewHolder(AchievementViewHolder holder, int position){
        Achievement achievement = achievementList.get(position);
        holder.achievementName.setText(achievement.getName());
        holder.achievementDescription.setText(achievement.getDescription());
        holder.achievementIcon.setImageResource(achievement.getIconResource());
    }

    @Override
    public int getItemCount(){return achievementList.size();}


}
