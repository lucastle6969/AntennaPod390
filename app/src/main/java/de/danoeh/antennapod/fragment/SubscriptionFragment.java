package de.danoeh.antennapod.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    private int mPosition = -1;
    private int aPosition = -1;

    private boolean categoryView;

    private List<Category> categoryArrayList = new ArrayList<>();

    private static final int GRID_COL_NUM = 3;

    private Subscription subscription;

    private int fragmentId;

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

        categoryArrayList = DBReader.getAllCategories();

        for(int i=0; i<categoryArrayList.size(); i++){
            Category category = categoryArrayList.get(i);
            TableRow tableRowTitle = addRowTitle(category);
            table.addView(tableRowTitle);
            tableGridRow = addGridRow(i, category);
            table.addView(tableGridRow);
        }

        return root;
    }

    public TableRow addGridRowSimple(){
        TableRow gridRow = new TableRow(getActivity());
        WrappedGridView gridView = new WrappedGridView(getActivity());
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

        return gridRow;
    }

    public TableRow addRowTitle(Category category){
        TableRow rowTitle = new TableRow(getActivity());
        rowTitle.setGravity(Gravity.FILL_HORIZONTAL);
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.NO_GRAVITY);
        layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                toggle_contents(v);
            }
        });
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT
        );
        layout.setLayoutParams(layoutParams);

        ImageView expandButton = new ImageView(getActivity());
        expandButton.setImageResource(R.drawable.ic_expand_more_grey600_36dp);
        expandButton.setId(R.id.category_collapse_button);

        layout.addView(expandButton);

        TextView title = new TextView(getActivity());
        title.setText(category.getName());
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                700, LinearLayout.LayoutParams.WRAP_CONTENT
        );
//        textParams.setMargins(100,0,100, 0);
        title.setLayoutParams(textParams);
        title.setPadding(50, 8, 0, 4);
        title.setGravity(Gravity.LEFT);
        title.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        title.setId(R.id.category_title_view);
        layout.addView(title);

        if(category.getId() != 0) {
            ImageButton editCategoryButton = new ImageButton(getActivity());
            editCategoryButton.setImageResource(R.drawable.ic_edit_category_light);
            editCategoryButton.setId(R.id.edit_category_button);
            editCategoryButton.setRight(10);
            editCategoryButton.setBackgroundColor(0x00000000);
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                    150, LinearLayout.LayoutParams.FILL_PARENT
            );
            editParams.setMargins(100,0,10, 0);
            editCategoryButton.setLayoutParams(editParams);

            editCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EditCategoryDialog().showEditCategoryDialog(getActivity(), category, (SubscriptionFragment)getFragmentManager().findFragmentById(fragmentId));
                }
            });
            layout.addView(editCategoryButton);
        }

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 6;

        rowTitle.addView(layout, params);
        return rowTitle;
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    public TableRow addGridRow(int rowNumber, Category category){
        TableRow gridRow = new TableRow(getActivity());

        WrappedGridView gridView = new WrappedGridView(getActivity());
        gridView.setNumColumns(GRID_COL_NUM);

        List<Feed> feedList = new ArrayList<>();
        List<Integer> counterList = new ArrayList<>();
        if(navDrawerData!=null){
            List<Long> categoryFeedIds = category.getFeedIds();
            for(Long feedId:categoryFeedIds){
                Feed feedToAdd = navDrawerData.getFeedById(feedId);
                if(feedToAdd != null){
                    feedList.add(feedToAdd);
                    counterList.add(navDrawerData.feedCounters.get(feedId));
                }
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

        for(int rowNumber=0; rowNumber<categoryArrayList.size(); rowNumber++) {
            Category category = categoryArrayList.get(rowNumber);
            feedList = new ArrayList<>();
            counterList = new ArrayList<>();

            List<Long> categoryFeedIds = category.getFeedIds();
          if (navDrawerData != null) {
              for (Long feedId:categoryFeedIds) {
                  Feed feedToAdd = navDrawerData.getFeedById(feedId);
                  if(feedToAdd != null){
                      feedList.add(feedToAdd);
                      counterList.add(navDrawerData.feedCounters.get(feedId));
                  }
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
            SubscriptionFragment sf = (SubscriptionFragment) getFragmentManager().findFragmentById(fragmentId);
            categoryDialog.showCreateCategoryDialog(getActivity(), sf);
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

        long currentCategoryId = 0;
        for(int i=0; i < categoryArrayList.size(); i++){
            if(categoryArrayList.get(i).getFeedIds().contains(feed.getId())){
                currentCategoryId = categoryArrayList.get(i).getId();
            }
        }
        if(currentCategoryId ==0) {
            menu.getItem(5).setVisible(false);
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
                refresh();
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
