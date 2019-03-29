package de.danoeh.antennapod.fragment;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.R;
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
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.dialog.CreateCategoryDialog;
import de.danoeh.antennapod.dialog.EditCategoryDialog;
import de.danoeh.antennapod.dialog.RemoveFromCategoryDialog;
import de.danoeh.antennapod.dialog.RenameFeedDialog;
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

    private int mPosition = -1;
    private int aPosition = -1;

    private boolean categoryView;

    private static final int GRID_COL_NUM = 3;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        subscriptionsAdapterList = new ArrayList<>();
        gridViewList = new ArrayList<>();
        TableLayout table = root.findViewById(R.id.tableLayout);
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        TableRow tableGridRow;
        if(!categoryView){
            tableGridRow = addGridRowSimple();
            table.addView(tableGridRow);
            return root;
        }

        ArrayList<String> categoryTitles = getCategoryTitles();

        for(int i=0; i<categoryTitles.size(); i++){
            TableRow tableRowTitle = addRowTitle(categoryTitles.get(i));
            table.addView(tableRowTitle);
            tableGridRow = addGridRow(i);
            table.addView(tableGridRow);
        }

        return root;
    }

    public ArrayList<String> getCategoryTitles(){
        // this should actually come from the db
        ArrayList<String> categoryTitles = new ArrayList<>();

        categoryTitles.add("uncategorized subscriptions");
        categoryTitles.add("Category 1");
        // categoryTitles.add("Category 2");

        return categoryTitles;
    }

    public int getNavDrawerPositionOffset(int rowNumber){
        // this should come from db
        int navDrawerPositionOffset = 0;
        if(rowNumber == 0){
            return navDrawerPositionOffset;
        }else{
            for(int i=0; i<rowNumber; i++){
                navDrawerPositionOffset += getNumberOfFeeds(i);
            }
            return navDrawerPositionOffset;
        }
    }

    public int getNumberOfFeeds(int rowNumber){
        // this should come from db
        // numberOfFeeds = 0;
        // example for(int i = 0; i<totalFeeds.size(); i++){
        //      if(totalFeeds.get(i).categoryTitle.equals(getCategoryTitles().get(rowNumber)){
        //          numberOfFeeds++;
        // }
        // return numberOfFeeds;
        return 2;
    }

    public TableRow addGridRowSimple(){
        TableRow gridRow = new TableRow(getActivity());
        GridView gridView = new GridView(getActivity());
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT
        );
        gridView.setLayoutParams(params);
        gridView.setNumColumns(GRID_COL_NUM);

        List<Feed> feedList = new ArrayList<>();
        List<Integer> counterList = new ArrayList<>();
        if(navDrawerData!=null){
            for(int i=0; i<navDrawerData.feeds.size(); i++){
                feedList.add(navDrawerData.feeds.get(i));
                counterList.add(navDrawerData.feedCounters.get(navDrawerData.feeds.get(i).getId()));
            }
        }else{
            Log.d("ITEM_ACCESS", "navDrawerData was null in addGridRowSimple");
        }

        subscriptionsAdapterList.add(new SubscriptionsAdapterAdd((MainActivity) getActivity(), feedList, counterList));

        gridView.setAdapter(subscriptionsAdapterList.get(0));
        gridView.setOnItemClickListener(subscriptionsAdapterList.get(0));

        registerForContextMenu(gridView);
        
        gridRow.addView(gridView);
        setGridViewHeightBasedOnChildren(gridView, GRID_COL_NUM);

        return gridRow;
    }

    public TableRow addRowTitle(String categoryTitle){
        TableRow rowTitle = new TableRow(getActivity());
        rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(getActivity());
        title.setText(categoryTitle);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        title.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                toggle_contents(v);
            }
        });

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 6;

        rowTitle.addView(title, params);
        return rowTitle;
    }

    public TableRow addGridRow(int rowNumber){
        TableRow gridRow = new TableRow(getActivity());
        GridView gridView = new GridView(getActivity());
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT
        );
        gridView.setLayoutParams(params);
        gridView.setNumColumns(GRID_COL_NUM);

        int navDrawerPositionOffset = getNavDrawerPositionOffset(rowNumber);
        int numberOfFeeds = getNumberOfFeeds(rowNumber);

        List<Feed> feedList = new ArrayList<>();
        List<Integer> counterList = new ArrayList<>();
        if(navDrawerData!=null){
            for(int i=0; i<numberOfFeeds; i++){
                feedList.add(navDrawerData.feeds.get(i+navDrawerPositionOffset));
                counterList.add(navDrawerData.feedCounters.get(navDrawerData.feeds.get(i+navDrawerPositionOffset).getId()));
            }
        }else{
            Log.d("ITEM_ACCESS", "navDrawerData was null in addGridRow");
        }

        if(rowNumber!=0) {
            subscriptionsAdapterList.add(new SubscriptionsAdapter((MainActivity) getActivity(), feedList, counterList));
        }else{
            subscriptionsAdapterList.add(new SubscriptionsAdapterAdd((MainActivity) getActivity(), feedList, counterList));
        }
        gridView.setAdapter(subscriptionsAdapterList.get(rowNumber));
        gridView.setOnItemClickListener(subscriptionsAdapterList.get(rowNumber));

        registerForContextMenu(gridView);
        gridViewList.add(gridView);
        gridRow.addView(gridView);
        setGridViewHeightBasedOnChildren(gridView, GRID_COL_NUM);

        return gridRow;
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight;
        int items = listAdapter.getCount();
        int rows;

        // this number seems to match the feed image height
        totalHeight = 500;

        float x;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    private void toggle_contents(View v){
        TextView title = (TextView) v;
        List<String> categoryTitles = getCategoryTitles();
        String currentText = title.getText().toString();

        for(int i = 0; i<categoryTitles.size(); i++){
            if(categoryTitles.get(i).equals(currentText)){
                GridView currentView = gridViewList.get(i);
                if(currentView.isShown()){
                    currentView.setVisibility(View.GONE);
                }else{
                    currentView.setVisibility(View.VISIBLE);
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
                    categorizeSubscriptions();
                }, error -> Log.e(TAG, Log.getStackTraceString(error)));
    }

    private void categorizeSubscriptions() {
        List<Long> feedIdsInCategories = DBReader.getAllFeedIdsInCategories();
        for (Feed f : navDrawerData.feeds) {
            if(!feedIdsInCategories.contains(f.getId())) {
                // Insert new subscriptions into uncategorized category
                DBWriter.addFeedToUncategorized(f.getId());
            }
        }
    }

    private void updateFeeds(){
        List<Feed> feedList = new ArrayList<>();
        List<Integer> counterList = new ArrayList<>();

        if(!categoryView){
            if (navDrawerData != null) {
                for (int i = 0; i < navDrawerData.feeds.size(); i++) {
                    feedList.add(navDrawerData.feeds.get(i));
                    counterList.add(navDrawerData.feedCounters.get(navDrawerData.feeds.get(i).getId()));
                }
            } else {
                Log.d("ITEM_ACCESS", "navDrawerData was null in updateFeeds");
            }
            subscriptionsAdapterList.get(0).updateFeeds(feedList, counterList);

            return;
        }

        ArrayList<String> categoryTitles = getCategoryTitles();

        for(int rowNumber=0; rowNumber<categoryTitles.size(); rowNumber++) {

            feedList = new ArrayList<>();
            counterList = new ArrayList<>();

          int navDrawerPositionOffset = getNavDrawerPositionOffset(rowNumber);
          int numberOfFeeds = getNumberOfFeeds(rowNumber);

          if (navDrawerData != null) {
              for (int i = 0; i < numberOfFeeds; i++) {
                  feedList.add(navDrawerData.feeds.get(i + navDrawerPositionOffset));
                  counterList.add(navDrawerData.feedCounters.get(navDrawerData.feeds.get(i + navDrawerPositionOffset).getId()));
              }
          } else {
              Log.d("ITEM_ACCESS", "navDrawerData was null in updateFeeds");
          }
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

        if (id == R.id.addCategory) {
            CreateCategoryDialog categoryDialog = new CreateCategoryDialog();
            categoryDialog.showCreateCategoryDialog(getActivity());
        }
        if (id == R.id.toggleCategoryView){
            categoryView = categoryView ? false : true;
            UserPreferences.setCategoryToggle(categoryView);
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
        return super.onOptionsItemSelected(item);
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
                Category category1 = new Category(1, "My Category for remove");
                new RemoveFromCategoryDialog().showRemoveFromCategoryDialog(getActivity(), category1);
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
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubscriptions();
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
