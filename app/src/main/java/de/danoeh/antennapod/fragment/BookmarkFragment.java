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

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MediaplayerInfoActivity.MediaplayerInfoContentFragment;
import de.danoeh.antennapod.adapter.BookmarkAdapter;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;

public class BookmarkFragment extends Fragment implements MediaplayerInfoContentFragment {

    private static final String TAG = "BookmarkFragment";
    private static final String ARG_PLAYABLE = "arg.playable";

    private Playable media;

    private View root;
    private PlaybackController controller;

    RecyclerView recyclerView;
    BookmarkAdapter bookmarkAdapter;
    List<String> bookmarkList = new ArrayList<>();

    public static BookmarkFragment newInstance(Playable item) {
        BookmarkFragment bookmark = new BookmarkFragment();
        bookmark.media = item;
        return bookmark;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (media == null) {
            Log.e(TAG, TAG + " was called without media");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.bookmark_fragment, container, false);
        recyclerView = root.findViewById(R.id.bookmarkList);

        bookmarkAdapter = new BookmarkAdapter(bookmarkList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookmarkAdapter);

        bookmarkList.add("TEST 1");
        bookmarkList.add("TEST 2");
        return root;
    }

    private void loadMediaInfo() {
        if (media != null) {
            Log.d(TAG, "loadMediaInfo called normally");
        } else {
            Log.w(TAG, "loadMediaInfo was called while media was null");
        }
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

    public void setController(PlaybackController controller) {
        this.controller = controller;
    }
}
