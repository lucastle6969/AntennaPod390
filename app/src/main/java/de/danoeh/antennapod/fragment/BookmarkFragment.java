package de.danoeh.antennapod.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MediaplayerInfoActivity.MediaplayerInfoContentFragment;
import de.danoeh.antennapod.adapter.BookmarkAdapter;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.feed.Bookmark;
import de.danoeh.antennapod.core.util.playback.PlaybackController;

public class BookmarkFragment extends Fragment implements MediaplayerInfoContentFragment {

    private static final String TAG = "BookmarkFragment";
    private static final String ARG_PLAYABLE = "arg.playable";

    private Playable media;

    private View root;
    private PlaybackController controller;

    TextView emptyView;
    RecyclerView recyclerView;
    BookmarkAdapter bookmarkAdapter;
    List<Bookmark> bookmarkList;

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
        setHasOptionsMenu(true);

        //Retrieve bookmark from db
        bookmarkList = retrieveBookmarks();

    }


    public List<Bookmark> retrieveBookmarks(){
        List<Bookmark> retrievedBookmarks = new ArrayList<>();
        String podcastTitle = media.getFeedTitle();
        String episodeId = media.getIdentifier().toString();

        retrievedBookmarks = DBReader.getBookmarksWithTitleAndUID(podcastTitle, episodeId);

        return retrievedBookmarks;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.bookmark_fragment, container, false);
        recyclerView = root.findViewById(R.id.bookmarkList);
        emptyView = root.findViewById(R.id.empty_view);

        bookmarkAdapter = new BookmarkAdapter(bookmarkList);
        bookmarkAdapter.setContext(this.getActivity());

        bookmarkList = retrieveBookmarks();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookmarkAdapter);

        // If bookmark list is empty, display a message in the view.
        if (bookmarkList.isEmpty()) {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!isAdded()) {
            return;
        }

        super.onCreateOptionsMenu(menu, inflater);

        MenuItem delete_button = menu.findItem(R.id.delete_bookmarks);

        //Set a listener for when the user clicks on the trash can in the action bar
        delete_button.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Hide all icons and only display confirm check or cancel button
                        menu.findItem(R.id.add_to_favorites_item).setVisible(false);
                        menu.findItem(R.id.set_sleeptimer_item).setVisible(false);
                        menu.findItem(R.id.audio_controls).setVisible(false);
                        menu.findItem(R.id.confirmDelete).setVisible(true);
                        menu.findItem(R.id.cancelDelete).setVisible(true);

                        bookmarkAdapter.showCheckBox(true);

                        //Notify the adapter to update the view
                        bookmarkAdapter.notifyDataSetChanged();

                        return true;
                    }
                });

        MenuItem confirmCheck = menu.findItem(R.id.confirmDelete);
        confirmCheck.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Displays the other icons again
                        menu.findItem(R.id.add_to_favorites_item).setVisible(true);
                        menu.findItem(R.id.audio_controls).setVisible(true);
                        menu.findItem(R.id.confirmDelete).setVisible(false);
                        menu.findItem(R.id.cancelDelete).setVisible(false);

                        //Display a dialog to confirm
                        showDeleteBookmarkDialog();

                        return true;
                    }
                });

        MenuItem cancelDelete = menu.findItem(R.id.cancelDelete);
        cancelDelete.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        menu.findItem(R.id.add_to_favorites_item).setVisible(true);
                        menu.findItem(R.id.audio_controls).setVisible(true);
                        menu.findItem(R.id.confirmDelete).setVisible(false);
                        menu.findItem(R.id.cancelDelete).setVisible(false);

                        bookmarkAdapter.showCheckBox(false);
                        bookmarkAdapter.notifyDataSetChanged();

                        return true;
                    }
                });
    }

    private void showDeleteBookmarkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.delete_multiple_bookmark_confirmation);

        LinearLayout layout = new LinearLayout(this.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bookmarkAdapter.showCheckBox(false);

                //Notify adapter to delete the selection bookmarks and update view
                bookmarkAdapter.deleteCheckedBookmarks();
                bookmarkAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }
}
