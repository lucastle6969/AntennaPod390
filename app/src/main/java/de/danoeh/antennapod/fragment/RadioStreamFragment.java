package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MediaplayerInfoActivity.MediaplayerInfoContentFragment;
import de.danoeh.antennapod.adapter.RadioStreamAdapter;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;

public class RadioStreamFragment extends Fragment implements MediaplayerInfoContentFragment {

    private static final String TAG = "RadioStreamFragment";

    private Playable media;

    private View root;
    private PlaybackController controller;

    TextView emptyView;
    RecyclerView recyclerView;
    RadioStreamAdapter radioStreamAdapter;
    List<RadioStream> radioStreamList;

    public static RadioStreamFragment newInstance(Playable item) {
        RadioStreamFragment radioStream = new RadioStreamFragment();
        radioStream.media = item;
        return radioStream;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (media == null) {
            Log.e(TAG, TAG + " was called without media");
        }
        setHasOptionsMenu(true);

        //Retrieve bookmark from db
        radioStreamList = retrieveRadioStreams();
    }


    public List<RadioStream> retrieveRadioStreams(){
        List<RadioStream> retrievedRadioStreams = new ArrayList<>();

        retrievedRadioStreams = DBReader.getAllRecommendedRadioStreams();

        return retrievedRadioStreams;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.radio_stream_fragment, container, false);
        recyclerView = root.findViewById(R.id.radio_stream_list);
        emptyView = root.findViewById(R.id.empty_radio_stream_view);

        radioStreamAdapter = new RadioStreamAdapter(radioStreamList, controller);
        radioStreamAdapter.setContext(this.getActivity());

        radioStreamList = retrieveRadioStreams();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(radioStreamAdapter);

        // If bookmark list is empty, display a message in the view.
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

    //For logging purposes (do not remove)
    private void loadMediaInfo() {
        if (media != null) {
            Log.d(TAG, "loadMediaInfo called normally");
        } else {
            Log.w(TAG, "loadMediaInfo was called while media was null");
        }
    }

    public void setController(PlaybackController controller) {
        this.controller = controller;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "On Start");
        super.onStart();
        if (media != null) {
            Log.d(TAG, "Loading media info");
            loadMediaInfo();
        } else {
            Log.w(TAG, "Unable to load media info: media was null");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // prevent memory leaks
        root = null;
    }

    @Override
    public void onMediaChanged(Playable media) {
        if(this.media == media) {
            return;
        }
        this.media = media;
        if (isAdded()) {
            loadMediaInfo();
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        if(!isAdded()) {
//            return;
//        }
//
//        super.onCreateOptionsMenu(menu, inflater);
//
//        MenuItem delete_button = menu.findItem(R.id.deleteBookmarks);
//
//    }
}
