package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.AchievementsAdapter;
import de.danoeh.antennapod.core.achievements.Achievement;
import de.danoeh.antennapod.core.achievements.AchievementManager;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.dialog.AchievementResetDialog;

public class AchievementsFragment extends Fragment {

    public static final String TAG = "AchievementFragment";

    private View root;

    private RecyclerView recyclerView;
    private AchievementsAdapter achievementsAdapter;
    private ConcurrentHashMap<String, Achievement> achievementMap;
    private List<Achievement> achievementList;
    private TextView page_title_rank;
    private TextView total_rank_icon;

    public AchievementsFragment (){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // So, we certainly *don't* have an options menu,
        // but unless we say we do, old options menus sometimes
        // persist.  mfietz thinks this causes the ActionBar to be invalidated
        setHasOptionsMenu(true);
        achievementMap = AchievementManager.getInstance(null).getAchievements();
        achievementList = toList(achievementMap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = inflater.inflate(R.layout.achievement_fragment, container, false);
        recyclerView = root.findViewById(R.id.achievementList);

        achievementsAdapter = new AchievementsAdapter(achievementList, getActivity());
        achievementsAdapter.setContext(this.getActivity());
        page_title_rank = root.findViewById(R.id.achievement_page_title_rank);
        total_rank_icon = root.findViewById(R.id.achievement_total_rank_icon);
        total_rank_icon.setBackgroundResource(de.danoeh.antennapod.core.R.drawable.ic_achievement_star_1);
        Integer userRank = AchievementManager.getInstance(null).getTotalRank();
        String playerTitle = AchievementManager.getInstance(null).getRankTitle(userRank);
        total_rank_icon.setText(userRank.toString());
        page_title_rank.setText("Your Rank: " + playerTitle );

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(achievementsAdapter);

        return root;
    }

    public static List<Achievement> toList(ConcurrentHashMap<String, Achievement> achievementMap){
        List<Achievement> list = new ArrayList<>();
        for(Achievement achv: achievementMap.values()){
            list.add(achv);
        }
        return list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.achievements);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isAdded()) {
            return;
        }
        getActivity().getMenuInflater().inflate(R.menu.achievements_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem enableAchievements = menu.findItem(R.id.enableAchievements);
        if(UserPreferences.getAchievementsToggle()) {
            enableAchievements.setIcon(R.drawable.ic_check_box_grey600_24dp);
        } else {
            enableAchievements.setIcon(R.drawable.ic_check_box_outline_blank_grey600_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MenuItem enableAchievements = item;
        switch(id) {
            case R.id.enableAchievements:
                UserPreferences.setAchievementsToggle(!UserPreferences.getAchievementsToggle());
                if(UserPreferences.getAchievementsToggle()) {
                    Toast.makeText(getActivity(), R.string.achievements_enabled,
                            Toast.LENGTH_LONG).show();
                    enableAchievements.setIcon(R.drawable.ic_check_box_grey600_24dp);
                } else {
                    Toast.makeText(getActivity(), R.string.achievements_disabled,
                            Toast.LENGTH_LONG).show();
                    enableAchievements.setIcon(R.drawable.ic_check_box_outline_blank_grey600_24dp);
                }
                break;
            case R.id.resetAchievements:
                AchievementResetDialog dialog = new AchievementResetDialog();
                dialog.showDialog(getContext(), this, achievementsAdapter);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        root = null;
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
