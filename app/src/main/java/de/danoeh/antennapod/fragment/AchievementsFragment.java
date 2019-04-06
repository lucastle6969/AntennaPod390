package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.adapter.AchievementsAdapter;
import de.danoeh.antennapod.core.feed.Achievement;
import de.danoeh.antennapod.core.storage.DBReader;

public class AchievementsFragment extends Fragment {

    public static final String TAG = "AchievementFragment";

    private View root;

    private RecyclerView recyclerView;
    private AchievementsAdapter achievementsAdapter;
    private ConcurrentHashMap<String, Achievement> achievementMap;
    private List<Achievement> achievementList;

    public AchievementsFragment (){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        achievementMap = DBReader.getAchievements();
        achievementList = toList(achievementMap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = inflater.inflate(R.layout.achievement_fragment, container, false);
        recyclerView = root.findViewById(R.id.achievementList);

        achievementsAdapter = new AchievementsAdapter(achievementList);
        achievementsAdapter.setContext(this.getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(achievementsAdapter);

        return root;
    }

    public List<Achievement> toList(ConcurrentHashMap<String, Achievement> achievementMap){
        return (List<Achievement>) achievementMap.values();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        root = null;
    }

}
