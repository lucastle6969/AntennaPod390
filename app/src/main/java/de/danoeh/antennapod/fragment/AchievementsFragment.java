package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.AchievementsAdapter;
import de.danoeh.antennapod.core.achievements.Achievement;
import de.danoeh.antennapod.core.storage.DBReader;

public class AchievementsFragment extends Fragment {

    public static final String TAG = "AchievementFragment";

    private View root;

    private RecyclerView recyclerView;
    private AchievementsAdapter achievementsAdapter;
    private ConcurrentHashMap<String, Achievement> achievementMap;
    private List<Achievement> achievementList;
    private TextView page_title_rank;
    private ImageView total_rank_image;

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
        achievementMap = DBReader.getAchievements();
        achievementList = toList(achievementMap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = inflater.inflate(R.layout.achievement_fragment, container, false);
        recyclerView = root.findViewById(R.id.achievementList);

        achievementsAdapter = new AchievementsAdapter(achievementList, getActivity());
        achievementsAdapter.setContext(this.getActivity());
        page_title_rank = root.findViewById(R.id.achievement_page_title_rank);
        total_rank_image = root.findViewById(R.id.achievement_rank_image);

        page_title_rank.setText("Your Rank: Rookie" );

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(achievementsAdapter);

        return root;
    }

    public List<Achievement> toList(ConcurrentHashMap<String, Achievement> achievementMap){
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
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Achievements");
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        root = null;
    }

}
