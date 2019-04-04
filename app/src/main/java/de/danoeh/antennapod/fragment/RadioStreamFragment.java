package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.adapter.RadioStreamAdapter;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.playback.PlaybackController;

public class RadioStreamFragment extends Fragment {

    private View root;
    private PlaybackController controller;

    TextView emptyView;
    RecyclerView recyclerView;
    RadioStreamAdapter radioStreamAdapter;
    List<RadioStream> radioStreamList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.radio_stream_fragment, container, false);
        recyclerView = root.findViewById(R.id.radio_stream_list);
        emptyView = root.findViewById(R.id.empty_radio_stream_view);

        Boolean isRecommended = this.getArguments().getBoolean("isRecommended");
        if(isRecommended){
            radioStreamList = DBReader.getAllRecommendedRadioStreams();

            //For now have fake radioStreams for testing purposes
            RadioStream stream1 = new RadioStream(1, "Title", "Url");
            RadioStream stream2 = new RadioStream(2, "Title", "Url");

            radioStreamList.add(stream1);
            radioStreamList.add(stream2);

        }
        else{
            radioStreamList = DBReader.getAllUserRadioStreams();
            RadioStream stream1 = new RadioStream(1, "Title", "Url");

            radioStreamList.add(stream1);
        }

        radioStreamAdapter = new RadioStreamAdapter(radioStreamList, controller, isRecommended);
        radioStreamAdapter.setContext(this.getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(radioStreamAdapter);

        // If Radio Stream list is empty, display a message in the view.
        if (radioStreamList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        return root;
    }


    public void setController(PlaybackController controller) {
        this.controller = controller;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // prevent memory leaks
        root = null;
    }


}
