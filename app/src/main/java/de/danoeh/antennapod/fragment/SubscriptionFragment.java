package de.danoeh.antennapod.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.achievements.AchievementData;
import de.danoeh.antennapod.core.achievements.AchievementUnlocked;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.SubscriptionsAdapter;
import de.danoeh.antennapod.adapter.SubscriptionsAdapterAdd;
import de.danoeh.antennapod.core.asynctask.FeedRemover;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.dialog.CreateCategoryDialog;
import de.danoeh.antennapod.dialog.EditCategoryDialog;
import de.danoeh.antennapod.dialog.MoveToCategoryDialog;
import de.danoeh.antennapod.dialog.RemoveFromCategoryDialog;
import de.danoeh.antennapod.dialog.RenameFeedDialog;
import de.danoeh.antennapod.view.WrappedGridView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Fragment for displaying feed subscriptions
 */
public class SubscriptionFragment extends Fragment {

    public static final String TAG = "SubscriptionFragment";

    private static final int EVENTS = EventDistributor.FEED_LIST_UPDATE
            | EventDistributor.UNREAD_ITEMS_UPDATE;

    private DBReader.NavDrawerData navDrawerData;

    private ArrayList<SubscriptionsAdapter> subscriptionsAdapterList = new ArrayList<>();
    private ArrayList<GridView> gridViewList = new ArrayList<>();

    private List<Category> categoryArrayList = new ArrayList<>();
    private List<Feed> feedList = new ArrayList<>();
    private List<Integer> counterList = new ArrayList<>();

    private static final int GRID_COL_NUM = 3;

    private int mPosition = -1;
    private int aPosition = -1;
    private int fragmentId;

    private boolean categoryView;

    private SearchView subscriptionSearch;

    private Subscription subscription;

    public SubscriptionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // So, we certainly *don't* have an options menu,
        // but unless we say we do, old options menus sometimes
        // persist.  mfietz thinks this causes the ActionBar to be invalidated
        setHasOptionsMenu(true);
        subscriptionsAdapterList = new ArrayList<>();
        categoryView = UserPreferences.getCategoryToggle();
        fragmentId = this.getId();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        subscriptionSearch = root.findViewById(R.id.subscriptionSearch);
        subscriptionSearch.setQueryHint(getResources().getString(R.string.search_subscription));
        subscriptionsAdapterList = new ArrayList<>();
        gridViewList = new ArrayList<>();
        TableLayout table = root.findViewById(R.id.tableLayout);
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        TableRow[] tableGridRow = new TableRow[1];
        subscriptionSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int drawableID = getContext().getResources().getIdentifier("round_button", "drawable", getActivity().getPackageName());
                ImageView iv = new ImageView(getContext());
                iv.setImageResource(android.R.drawable.btn_star);
                table.removeAllViews();
                tableGridRow[0] = addGridRowSimple(query);
                table.addView(tableGridRow[0]);
                AchievementUnlocked achievementUnlocked = new AchievementUnlocked(getContext().getApplicationContext());
                achievementUnlocked.setRounded(false).setLarge(true).setTopAligned(true).setDismissible(true);
                AchievementData data0 = new AchievementData();
                data0.setTitle("Elementary");
                data0.setSubtitle("Search for a podcast.");
                data0.setIcon(iv.getDrawable());
                data0.setTextColor(Color.BLACK);
                data0.setIconBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                data0.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                data0.setPopUpOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                achievementUnlocked.show(data0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("") || newText == null) {
                    table.removeAllViews();
                    subscriptionsAdapterList = new ArrayList<>();
                    gridViewList = new ArrayList<>();
                    if(categoryView) {
                        addCategoryViewsToViewTable(tableGridRow, table);
                    } else {
                        tableGridRow[0] = addGridRowSimple("");
                        table.addView(tableGridRow[0]);
                    }
                } else {
                    table.removeAllViews();
                    tableGridRow[0] = addGridRowSimple(newText);
                    table.addView(tableGridRow[0]);
                }
                return false;
            }
        });
        if(!categoryView){
            tableGridRow[0] = addGridRowSimple("");
            table.addView(tableGridRow[0]);
            return root;
        }

        categoryArrayList = DBReader.getAllCategories();

        addCategoryViewsToViewTable(tableGridRow, table);

        return root;
    }

    public TableRow addGridRowSimple(String searchQuery){
        TableRow gridRow = new TableRow(getActivity());
        WrappedGridView gridView = new WrappedGridView(getActivity());
        gridView.setNumColumns(GRID_COL_NUM);
        feedAndCounterReset();

        populateFeedAndCounterWithAllNavDrawerFeeds();

        if (!searchQuery.equals("")) {
            feedList = search(searchQuery, feedList);
        }
        if (subscriptionsAdapterList.size() == 0) {
            subscriptionsAdapterList.add(new SubscriptionsAdapterAdd((MainActivity) getActivity(), feedList, counterList));
        } else {
            subscriptionsAdapterList.set(0, new SubscriptionsAdapterAdd((MainActivity) getActivity(), feedList, counterList));
        }

        gridView.setAdapter(subscriptionsAdapterList.get(0));
        gridView.setOnItemClickListener(subscriptionsAdapterList.get(0));

        registerForContextMenu(gridView);

        gridRow.addView(gridView);

        return gridRow;
    }

    public List<Feed> search(String searchQuery, List<Feed> feeds) {
        Iterator<Feed> feedIterator = feeds.iterator();
        int i = 0;
        while(feedIterator.hasNext()){
            Feed feed = feedIterator.next();
            if(!feed.getTitle().toUpperCase().contains(searchQuery.toUpperCase())) {
                feedIterator.remove();
            }
            i++;
        }
        return feeds;
    }

    public TableRow addRowTitle(Category category){
        TableRow rowTitle = new TableRow(getActivity());
        rowTitle.setGravity(Gravity.FILL_HORIZONTAL);

        LinearLayout rowLayout = new LinearLayout(getActivity());
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        rowLayout.setGravity(Gravity.NO_GRAVITY);
        rowLayout.setPadding(25,0,50,0);
        rowLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                toggle_contents(v);
            }
        });
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT
        );
        rowLayout.setLayoutParams(layoutParams);

        ImageView expandButton = new ImageView(getActivity());
        expandButton.setImageResource(R.drawable.ic_expand_more_grey600_36dp);
        expandButton.setId(R.id.category_collapse_button);

        rowLayout.addView(expandButton);

        TextView title = new TextView(getActivity());
        title.setText(category.getName());
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        title.setId(R.id.category_title_view);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                700, LinearLayout.LayoutParams.MATCH_PARENT
        );
        title.setLayoutParams(textParams);

        rowLayout.addView(title);

        if(!category.getName().equals(PodDBAdapter.UNCATEGORIZED_CATEGORY_NAME)) {
            LinearLayout editLinearLayout = new LinearLayout(getActivity());
            editLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            editLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            editLinearLayout.setGravity(Gravity.RIGHT);

            ImageButton editCategoryButton = new ImageButton(getActivity());
            editCategoryButton.setImageResource(R.drawable.ic_edit_category_light);
            editCategoryButton.setId(R.id.edit_category_button);
            editCategoryButton.setBackgroundColor(0x00000000);

            editCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EditCategoryDialog().showEditCategoryDialog(getActivity(), category, (SubscriptionFragment)getFragmentManager().findFragmentById(fragmentId));
                }
            });

            editLinearLayout.addView(editCategoryButton);
            rowLayout.addView(editLinearLayout);
        }

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 6;

        rowTitle.addView(rowLayout, params);
        return rowTitle;
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    public TableRow addGridRow(int rowNumber, Category category){
        TableRow gridRow = new TableRow(getActivity());

        WrappedGridView gridView = new WrappedGridView(getActivity());
        gridView.setNumColumns(GRID_COL_NUM);

        feedAndCounterReset();

        populateFeedAndCounterWithCategoryFeeds(category);

        if(rowNumber!=0) {
            subscriptionsAdapterList.add(new SubscriptionsAdapter((MainActivity) getActivity(), feedList, counterList));
        }else{
            if(subscriptionsAdapterList.size() == 0) {
                subscriptionsAdapterList.add(new SubscriptionsAdapterAdd((MainActivity) getActivity(), feedList, counterList));
            } else {
                subscriptionsAdapterList.set(0, new SubscriptionsAdapterAdd((MainActivity) getActivity(), feedList, counterList));
            }
        }
        gridView.setAdapter(subscriptionsAdapterList.get(rowNumber));
        gridView.setOnItemClickListener(subscriptionsAdapterList.get(rowNumber));

        registerForContextMenu(gridView);
        gridViewList.add(gridView);

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 6;
        gridRow.addView(gridView, params);

        return gridRow;
    }

    private void toggle_contents(View v){
        TextView title = v.findViewById(R.id.category_title_view);
        ImageView collapse= v.findViewById(R.id.category_collapse_button);
        String currentText = title.getText().toString();

        for(int i = 0; i<categoryArrayList.size(); i++){
            if(categoryArrayList.get(i).getName().equals(currentText)){
                GridView currentView = gridViewList.get(i);
                if(currentView.isShown()){
                    collapse.animate().rotation(-90).setDuration(300);
                    currentView.animate()
                            .translationY(-currentView.getHeight())
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation){
                                    super.onAnimationEnd(animation);
                                    currentView.setVisibility(View.GONE);
                                }
                    });

                }else{
                    collapse.animate().rotation(0).setDuration(300);
                    currentView.setVisibility(View.VISIBLE);
                    currentView.animate()
                            .translationY(0)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                     super.onAnimationEnd(animation);
                                     currentView.setVisibility(View.VISIBLE);
                                 }
                             });
                }
                break;
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadSubscriptions();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.subscriptions_label);
        }
        EventDistributor.getInstance().register(contentUpdate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void loadSubscriptions() {
        if(subscription != null) {
            subscription.unsubscribe();
        }
        subscription = Observable.fromCallable(DBReader::getNavDrawerData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    navDrawerData = result;
                    updateFeeds();
                    for(SubscriptionsAdapter adapter: subscriptionsAdapterList){
                        adapter.notifyDataSetChanged();
                    }
                }, error -> Log.e(TAG, Log.getStackTraceString(error)));
    }

    private void updateFeeds(){
        feedAndCounterReset();

        if(!categoryView){
            populateFeedAndCounterWithAllNavDrawerFeeds();
            subscriptionsAdapterList.get(0).updateFeeds(feedList, counterList);

            return;
        }

        for(int rowNumber=0; rowNumber<categoryArrayList.size(); rowNumber++) {
            Category category = categoryArrayList.get(rowNumber);
            feedAndCounterReset();

          populateFeedAndCounterWithCategoryFeeds(category);

          subscriptionsAdapterList.get(rowNumber).updateFeeds(feedList, counterList);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isAdded()) {
            return;
        }
        getActivity().getMenuInflater().inflate(R.menu.subscription_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.addCategory:
                CreateCategoryDialog categoryDialog = new CreateCategoryDialog();
                SubscriptionFragment sf = (SubscriptionFragment) getFragmentManager().findFragmentById(fragmentId);
                categoryDialog.showCreateCategoryDialog(getActivity(), sf);
                break;
            case R.id.toggleCategoryView:
                categoryView = !categoryView;
                UserPreferences.setCategoryToggle(categoryView);
                refresh();
                subscriptionSearch.setQuery("", false);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUserPreferencesToCategoryView(){
        if(!categoryView) {
            categoryView = !categoryView;
            UserPreferences.setCategoryToggle(categoryView);
            refresh();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = adapterInfo.position;

        GridView selectedGridView = (GridView)v;
        SubscriptionsAdapter selectedAdapter = (SubscriptionsAdapter)selectedGridView.getAdapter();
        for(int i=0; i<subscriptionsAdapterList.size(); i++){
            if(subscriptionsAdapterList.get(i) == selectedAdapter){
                aPosition = i;
                break;
            }
        }

        Object selectedObject = selectedAdapter.getItem(position);
        if (selectedObject.equals(SubscriptionsAdapterAdd.ADD_ITEM_OBJ)) {
            mPosition = position;
            return;
        }

        Feed feed = (Feed)selectedObject;

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.nav_feed_context, menu);

        long currentCategoryId = -1;
        for(int i=0; i < categoryArrayList.size(); i++){
            if(categoryArrayList.get(i).getFeedIds().contains(feed.getId())){
                currentCategoryId = categoryArrayList.get(i).getId();
            }
        }
        if(currentCategoryId == PodDBAdapter.UNCATEGORIZED_CATEGORY_ID) {
            menu.findItem(R.id.remove_from_category_item).setVisible(false);
        }

        menu.setHeaderTitle(feed.getTitle());

        mPosition = position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final int position = mPosition;
        mPosition = -1; // reset
        final int adapterPosition = aPosition;
        if(position < 0 || adapterPosition < 0) {
            return false;
        }

        Object selectedObject = subscriptionsAdapterList.get(adapterPosition).getItem(position);
        if (selectedObject.equals(SubscriptionsAdapterAdd.ADD_ITEM_OBJ)) {
            // this is the add object, do nothing
            return false;
        }

        Feed feed = (Feed)selectedObject;
        switch(item.getItemId()) {
            case R.id.mark_all_seen_item:
                ConfirmationDialog markAllSeenConfirmationDialog = new ConfirmationDialog(getActivity(),
                        R.string.mark_all_seen_label,
                        R.string.mark_all_seen_confirmation_msg) {

                    @Override
                    public void onConfirmButtonPressed(DialogInterface dialog) {
                        dialog.dismiss();

                        Observable.fromCallable(() -> DBWriter.markFeedSeen(feed.getId()))
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(result -> loadSubscriptions(),
                                        error -> Log.e(TAG, Log.getStackTraceString(error)));
                    }
                };
                markAllSeenConfirmationDialog.createNewDialog().show();
                return true;
            case R.id.mark_all_read_item:
                ConfirmationDialog markAllReadConfirmationDialog = new ConfirmationDialog(getActivity(),
                        R.string.mark_all_read_label,
                        R.string.mark_all_read_confirmation_msg) {

                    @Override
                    public void onConfirmButtonPressed(DialogInterface dialog) {
                        dialog.dismiss();
                        Observable.fromCallable(() -> DBWriter.markFeedRead(feed.getId()))
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(result -> loadSubscriptions(),
                                        error -> Log.e(TAG, Log.getStackTraceString(error)));
                    }
                };
                markAllReadConfirmationDialog.createNewDialog().show();
                return true;
            case R.id.rename_item:
                new RenameFeedDialog(getActivity(), feed).show();
                return true;

            case R.id.remove_from_category_item:
                new RemoveFromCategoryDialog().showRemoveFromCategoryDialog(getActivity(), categoryArrayList,  feed.getId(), (SubscriptionFragment) getFragmentManager().findFragmentById(fragmentId));
            return true;

            case R.id.remove_item:
                final FeedRemover remover = new FeedRemover(getContext(), feed) {
                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        DBWriter.removeFeedFromSubscriptions(feed);
                        loadSubscriptions();
                    }
                };
                ConfirmationDialog conDialog = new ConfirmationDialog(getContext(),
                        R.string.remove_feed_label,
                        getString(R.string.feed_delete_confirmation_msg, feed.getTitle())) {
                    @Override
                    public void onConfirmButtonPressed(
                            DialogInterface dialog) {
                        dialog.dismiss();
                        long mediaId = PlaybackPreferences.getCurrentlyPlayingFeedMediaId();
                        if (mediaId > 0 &&
                                FeedItemUtil.indexOfItemWithMediaId(feed.getItems(), mediaId) >= 0) {
                            Log.d(TAG, "Currently playing episode is about to be deleted, skipping");
                            remover.skipOnCompletion = true;
                            int playerStatus = PlaybackPreferences.getCurrentPlayerStatus();
                            if(playerStatus == PlaybackPreferences.PLAYER_STATUS_PLAYING) {
                                IntentUtils.sendLocalBroadcast(getContext(), PlaybackService.ACTION_PAUSE_PLAY_CURRENT_EPISODE);

                            }
                        }
                        remover.executeAsync();
                    }
                };
                conDialog.createNewDialog().show();
                return true;
            case R.id.move_to_category:
                new MoveToCategoryDialog().showMoveToCategoryDialog(getActivity(), feed.getId(), (SubscriptionFragment)getFragmentManager().findFragmentById(fragmentId));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubscriptions();
    }

    private final void feedAndCounterReset(){
        feedList = new ArrayList<>();
        counterList = new ArrayList<>();
    }

    private final void addCategoryViewsToViewTable(TableRow[] tableGridRow, TableLayout table) {
        for (int i = 0; i < categoryArrayList.size(); i++) {
            Category category = categoryArrayList.get(i);
            TableRow tableRowTitle = addRowTitle(category);
            table.addView(tableRowTitle);
            tableGridRow[0] = addGridRow(i, category);
            table.addView(tableGridRow[0]);
        }
    }

    private final void populateFeedAndCounterWithAllNavDrawerFeeds(){
        if(navDrawerData!=null){
            for(int i=0; i<navDrawerData.feeds.size(); i++){
                feedList.add(navDrawerData.feeds.get(i));
                counterList.add(navDrawerData.feedCounters.get(navDrawerData.feeds.get(i).getId()));
            }
        }
    }

    private final void populateFeedAndCounterWithCategoryFeeds(Category category){
        if (navDrawerData != null) {
            List<Long> categoryFeedIds = category.getFeedIds();
            for (Long feedId:categoryFeedIds) {
                Feed feedToAdd = navDrawerData.getFeedById(feedId);
                if(feedToAdd != null){
                    feedList.add(feedToAdd);
                    counterList.add(navDrawerData.feedCounters.get(feedId));
                }
            }
        }
    }

    private final EventDistributor.EventListener contentUpdate = new EventDistributor.EventListener() {
        @Override
        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((EVENTS & arg) != 0) {
                Log.d(TAG, "Received contentUpdate Intent.");
                loadSubscriptions();
            }
        }
    };
}
