package de.danoeh.antennapod.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.adapter.RadioStreamAdapter;
import de.danoeh.antennapod.adapter.SubscriptionsAdapter;
import de.danoeh.antennapod.adapter.SubscriptionsAdapterAdd;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.dialog.DeleteRadioStreamDialog;
import de.danoeh.antennapod.dialog.EditRadioStreamDialog;

public class RadioStreamFragment extends Fragment {

    private View root;
    private PlaybackController controller;
    private RadioStreamListener radioStreamListener;

    TextView emptyView;
    RecyclerView recyclerView;
    RadioStreamAdapter radioStreamAdapter;
    List<RadioStream> radioStreamList = new ArrayList<>();

    public interface RadioStreamListener {
        void onRadioStreamSelected(RadioStream radioStream);
    }

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
        }
        else{
            radioStreamList = DBReader.getAllUserRadioStreams();
        }

        radioStreamAdapter = new RadioStreamAdapter(radioStreamList, controller, isRecommended, radioStreamListener);
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


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = radioStreamAdapter.getPosition();

        switch (item.getItemId()) {
            case R.id.edit_radio_stream:
                new EditRadioStreamDialog().showDialog(
                    this.getContext(),
                    this.radioStreamList.get(position),
                    this);
                return true;
            case R.id.delete_radio_stream:
                new DeleteRadioStreamDialog().showDialog(
                    this.getContext(),
                    this.radioStreamList.get(position),
                    this);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RadioStreamListener) {
            radioStreamListener = (RadioStreamListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RadioStreamListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        radioStreamListener = null;
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

}
