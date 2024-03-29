package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter;
import de.danoeh.antennapod.core.achievements.AchievementBuilder;
import de.danoeh.antennapod.core.achievements.AchievementManager;
import de.danoeh.antennapod.core.achievements.AchievementUnlocked;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import de.mfietz.fyydlin.FyydClient;
import de.mfietz.fyydlin.FyydResponse;
import de.mfietz.fyydlin.SearchHit;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static de.danoeh.antennapod.adapter.itunes.ItunesAdapter.Podcast;
import static java.util.Collections.emptyList;

public class FyydSearchFragment extends Fragment {

    private static final String TAG = "FyydSearchFragment";

    /**
     * Adapter responsible with the search results
     */
    private ItunesAdapter adapter;
    private GridView gridView;
    private ProgressBar progressBar;
    private TextView txtvError;
    private Button butRetry;
    private TextView txtvEmpty;

    private final FyydClient client = new FyydClient(AntennapodHttpClient.getHttpClient());

    /**
     * List of podcasts retrieved from the search
     */
    private List<Podcast> searchResults;
    private Subscription subscription;

    private final String [] PodcastCategory = {"Arts", "Business", "Comedy", "Education", "Games", "Government","Health","Kids","Music","News","Religion","Science", "Society"};
    private final Random randomNumGen = new Random();

    /**
     * Constructor
     */
    public FyydSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_provider_search, container, false);
        gridView = root.findViewById(R.id.gridView);
        adapter = new ItunesAdapter(getActivity(), new ArrayList<>());
        gridView.setAdapter(adapter);

        //Show information about the podcast when the list item is clicked
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            Podcast podcast = searchResults.get(position);
            Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
            intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, podcast.feedUrl);
            intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, podcast.title);
            startActivity(intent);
        });
        progressBar = root.findViewById(R.id.progressBar);
        txtvError = root.findViewById(R.id.txtvError);
        butRetry = root.findViewById(R.id.butRetry);
        txtvEmpty = root.findViewById(android.R.id.empty);

        final SearchView sv = root.findViewById(R.id.action_search);
        sv.setIconifiedByDefault(false);
        sv.setQueryHint(getString(R.string.search_fyyd_label));

        if(!sv.isFocused()) {
            sv.clearFocus();
        }

        sv.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                sv.clearFocus();
                search(s);
                AchievementManager.getInstance(new AchievementUnlocked(getContext()))
                        .increment(new ArrayList<>(Arrays.asList(
                                AchievementBuilder.SEARCH_FYYD_ACHIEVEMENT,
                                AchievementBuilder.SEARCH_ACHIEVEMENT
                        )), getContext().getApplicationContext());
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        sv.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose(){

                //Clear query
                sv.setQuery("", false);
                sv.clearFocus();

                getActivity().getSupportFragmentManager().popBackStack();
                loadDefaultPodcastWithQuery(PodcastCategory[randomNumGen.nextInt(12)]);
                return true;
            }
        });

        loadDefaultPodcastWithQuery(PodcastCategory[randomNumGen.nextInt(12)]);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        adapter = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.provider_search, menu);
    }

    /**
     * Replace adapter data with provided search results from SearchTask.
     * @param result List of Podcast objects containing search results
     */
    private void updateData(List<Podcast> result) {
        this.searchResults = result;
        adapter.clear();
        if (result != null && result.size() > 0) {
            gridView.setVisibility(View.VISIBLE);
            txtvEmpty.setVisibility(View.GONE);
            for (Podcast p : result) {
                adapter.add(p);
            }
            adapter.notifyDataSetInvalidated();
        } else {
            gridView.setVisibility(View.GONE);
            txtvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void loadDefaultPodcastWithQuery(String query) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        gridView.setVisibility(View.GONE);
        txtvError.setVisibility(View.GONE);
        butRetry.setVisibility(View.GONE);
        txtvEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        search(query);
        updateData(searchResults);
    }

    private void search(String query) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        showOnlyProgressBar();
        subscription =  client.searchPodcasts(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    progressBar.setVisibility(View.GONE);
                    processSearchResult(result);
                }, error -> {
                    Log.e(TAG, Log.getStackTraceString(error));
                    progressBar.setVisibility(View.GONE);
                    txtvError.setText(error.toString());
                    txtvError.setVisibility(View.VISIBLE);
                    butRetry.setOnClickListener(v -> search(query));
                    butRetry.setVisibility(View.VISIBLE);
                });
    }

    private void showOnlyProgressBar() {
        gridView.setVisibility(View.GONE);
        txtvError.setVisibility(View.GONE);
        butRetry.setVisibility(View.GONE);
        txtvEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void processSearchResult(FyydResponse response) {
        adapter.clear();
        if (!response.getData().isEmpty()) {
            adapter.clear();
            searchResults = new ArrayList<>();
            for (SearchHit searchHit : response.getData().values()) {
                Podcast podcast = Podcast.fromSearch(searchHit);
                searchResults.add(podcast);
            }
        } else {
            searchResults = emptyList();
        }
        for(Podcast podcast : searchResults) {
            adapter.add(podcast);
        }
        adapter.notifyDataSetInvalidated();
        gridView.setVisibility(!searchResults.isEmpty() ? View.VISIBLE : View.GONE);
        txtvEmpty.setVisibility(searchResults.isEmpty() ? View.VISIBLE : View.GONE);
    }

}
