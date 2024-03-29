package de.danoeh.antennapod.fragment.gpodnet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.adapter.gpodnet.PodcastListAdapter;
import de.danoeh.antennapod.core.achievements.AchievementBuilder;
import de.danoeh.antennapod.core.achievements.AchievementManager;
import de.danoeh.antennapod.core.achievements.AchievementUnlocked;
import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;



/**
 * Displays a list of GPodnetPodcast-Objects in a GridView with a search bar (for gpodder search)
 * search does NOT search among displayed list results
 */
public abstract class PodcastSearchListFragment extends Fragment {

    private static final String TAG = "PodcastSearchListFrag";

    private GridView gridView;
    private ProgressBar progressBar;
    private TextView txtvError;
    private Button butRetry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_provider_search, container, false);

        gridView = root.findViewById(R.id.gridView);
        progressBar = root.findViewById(R.id.progressBar);
        txtvError = root.findViewById(R.id.txtvError);
        butRetry = root.findViewById(R.id.butRetry);

        gridView.setOnItemClickListener((parent, view, position, id) ->
                onPodcastSelected((GpodnetPodcast) gridView.getAdapter().getItem(position)));
        butRetry.setOnClickListener(v -> loadData());

        final SearchView searchView = root.findViewById(R.id.action_search);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.gpodnet_search_hint));

        if(!searchView.isFocused()) {
            searchView.clearFocus();
        }

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                reloadData(s);
                AchievementManager.getInstance(new AchievementUnlocked(getContext()))
                        .increment(new ArrayList<>(Arrays.asList(
                                AchievementBuilder.SEARCH_GPOD_ACHIEVEMENT,
                                AchievementBuilder.SEARCH_ACHIEVEMENT
                        )), getContext().getApplicationContext());
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose(){
                //Clear query
                searchView.setQuery("", false);
                searchView.clearFocus();
                loadData();
                return true;
            }
        });

        loadData();
        return root;
    }

    private void onPodcastSelected(GpodnetPodcast selection) {
        Log.d(TAG, "Selected podcast: " + selection.toString());
        Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
        intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, selection.getUrl());
        intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, getString(R.string.gpodnet_main_label));
        startActivity(intent);
    }

    protected abstract List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException;

    protected abstract List<GpodnetPodcast> reloadPodcastData (GpodnetService service, String query) throws GpodnetServiceException;

    final void loadData() {
        AsyncTask<Void, Void, List<GpodnetPodcast>> loaderTask = new AsyncTask<Void, Void, List<GpodnetPodcast>>() {
            volatile Exception exception = null;

            @Override
            protected List<GpodnetPodcast> doInBackground(Void... params) {
                GpodnetService service = null;
                try {
                    service = new GpodnetService();
                    return loadPodcastData(service);
                } catch (GpodnetServiceException e) {
                    exception = e;
                    e.printStackTrace();
                    return null;
                } finally {
                    if (service != null) {
                        service.shutdown();
                    }
                }
            }

            @Override
            protected void onPostExecute(List<GpodnetPodcast> gpodnetPodcasts) {
                super.onPostExecute(gpodnetPodcasts);
                final Context context = getActivity();
                if(context != null){
                    if (gpodnetPodcasts != null && gpodnetPodcasts.size() > 0) {
                        PodcastListAdapter listAdapter = new PodcastListAdapter(context, 0, gpodnetPodcasts);
                        gridView.setAdapter(listAdapter);
                        listAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        txtvError.setVisibility(View.GONE);
                        butRetry.setVisibility(View.GONE);
                    } else if (gpodnetPodcasts != null) {
                        gridView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        txtvError.setText(getString(R.string.search_status_no_results));
                        txtvError.setVisibility(View.VISIBLE);
                        butRetry.setVisibility(View.GONE);
                    } else {
                        gridView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        txtvError.setText(getString(R.string.error_msg_prefix) + exception.getMessage());
                        txtvError.setVisibility(View.VISIBLE);
                        butRetry.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                gridView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtvError.setVisibility(View.GONE);
                butRetry.setVisibility(View.GONE);
            }
        };

        loaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    final void reloadData(String query) {
        AsyncTask<Void, Void, List<GpodnetPodcast>> loaderTask = new AsyncTask<Void, Void, List<GpodnetPodcast>>() {
            volatile Exception exception = null;

            @Override
            protected List<GpodnetPodcast> doInBackground(Void... params) {
                GpodnetService service = null;
                try {
                    service = new GpodnetService();
                    return reloadPodcastData(service, query);
                } catch (GpodnetServiceException e) {
                    exception = e;
                    e.printStackTrace();
                    return null;
                } finally {
                    if (service != null) {
                        service.shutdown();
                    }
                }
            }

            @Override
            protected void onPostExecute(List<GpodnetPodcast> gpodnetPodcasts) {
                super.onPostExecute(gpodnetPodcasts);
                final Context context = getActivity();
                if(context != null){
                    if (gpodnetPodcasts != null && gpodnetPodcasts.size() > 0) {
                        PodcastListAdapter listAdapter = new PodcastListAdapter(context, 0, gpodnetPodcasts);
                        gridView.setAdapter(listAdapter);
                        listAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        txtvError.setVisibility(View.GONE);
                        butRetry.setVisibility(View.GONE);
                    } else if (gpodnetPodcasts != null) {
                        gridView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        txtvError.setText(getString(R.string.search_status_no_results));
                        txtvError.setVisibility(View.VISIBLE);
                        butRetry.setVisibility(View.GONE);
                    } else {
                        gridView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        txtvError.setText(getString(R.string.error_msg_prefix) + exception.getMessage());
                        txtvError.setVisibility(View.VISIBLE);
                        butRetry.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                gridView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtvError.setVisibility(View.GONE);
                butRetry.setVisibility(View.GONE);
            }
        };

        loaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

