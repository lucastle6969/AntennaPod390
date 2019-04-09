package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.achievements.Achievement;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private Activity context;

    private List<Achievement> achievementList;
    private AchievementViewHolder view;

    public class AchievementViewHolder extends RecyclerView.ViewHolder {
        private ImageView achievementIcon;
        private TextView achievementName;
        private TextView achievementDescription;
        private TextView achievementDate;


        public AchievementViewHolder(View view){
            super(view);
            achievementName = view.findViewById(R.id.achievement_name );
            achievementDescription = view.findViewById(R.id.achievement_description);
            achievementDate = view.findViewById(R.id.achievement_date);
            achievementIcon = view.findViewById(R.id.achievement_rank_image);
        }

    }

    public void setContext(Activity context) {context = context;}

    public AchievementsAdapter(List<Achievement> achievementList, Activity context){
        this.achievementList = achievementList;
        this.context = context;
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
        int backgroundColor = achievement.getBackgroundColor();
        holder.achievementName.setText(achievement.getDisplayName());
        holder.achievementName.setBackgroundColor(backgroundColor);
        holder.achievementDescription.setText(achievement.getDisplayDescription());
        holder.achievementDescription.setBackgroundColor(backgroundColor);
        holder.achievementIcon.setBackgroundColor(backgroundColor);
        holder.achievementIcon.setImageResource(achievement.getIconResource());
        holder.achievementDate.setText(achievement.getDateText());
        holder.achievementDate.setBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount(){return achievementList.size();}


}
