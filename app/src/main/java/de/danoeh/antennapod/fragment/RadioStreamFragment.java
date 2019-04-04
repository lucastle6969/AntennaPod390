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
import de.danoeh.antennapod.core.util.playback.PlaybackController;

public class RadioStreamFragment extends Fragment {

    private static final String TAG = "RadioStreamFragment";

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

        //Retrieve radio stream from db
//        radioStreamList = retrieveRadioStreams();

        RadioStream stream1 = new RadioStream(1, "Title", "Url");
        RadioStream stream2 = new RadioStream(2, "Title", "Url");
        RadioStream stream3 = new RadioStream(3, "Title", "Url");
        RadioStream stream4 = new RadioStream(4, "Title", "Url");
        RadioStream stream5 = new RadioStream(5, "Title", "Url");

        radioStreamList.add(stream1);
        radioStreamList.add(stream2);
        radioStreamList.add(stream3);
        radioStreamList.add(stream4);
        radioStreamList.add(stream5);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.radio_stream_fragment, container, false);
        recyclerView = root.findViewById(R.id.radio_stream_list);
        emptyView = root.findViewById(R.id.empty_radio_stream_view);

        Boolean isRecommended = this.getArguments().getBoolean("isRecommended");

        radioStreamAdapter = new RadioStreamAdapter(radioStreamList, controller, isRecommended);
        radioStreamAdapter.setContext(this.getActivity());

//        radioStreamList = retrieveRadioStreams();

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
