package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.fragment.AddFeedFragment;
import de.danoeh.antennapod.fragment.ItemlistFragment;
import jp.shts.android.library.TriangleLabelView;

/**
 * Adapter for subscriptions
 */
public class SubscriptionsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private static final String TAG = "SubscriptionsAdapter";

    private final WeakReference<MainActivity> mainActivityRef;
    private List<Feed> feedList;
    private List<Integer> counterList;

    // default constructor to allow the class to be extended - do not use
    public SubscriptionsAdapter(){
        this.mainActivityRef = null;
    }

    public SubscriptionsAdapter(MainActivity mainActivity, List<Feed> feedList, List<Integer> counterList) {
        this.mainActivityRef = new WeakReference<>(mainActivity);
        this.feedList = feedList;
        this.counterList = counterList;
    }

    public void updateFeeds(List<Feed> updatedFeeds, List<Integer> updatedCounters){
        feedList = updatedFeeds;
        counterList = updatedCounters;
    }

    @Override
    public int getCount() {
        return feedList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedList.get(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return feedList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubscriptionsAdapter.Holder holder;

        if (convertView == null) {
            holder = new SubscriptionsAdapter.Holder();

            LayoutInflater layoutInflater =
                    (LayoutInflater) mainActivityRef.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.subscription_item, parent, false);
            holder.feedTitle = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imgvCover);
            holder.count = (TriangleLabelView) convertView.findViewById(R.id.triangleCountView);


            convertView.setTag(holder);
        } else {
            holder = (SubscriptionsAdapter.Holder) convertView.getTag();
        }

        final Feed feed = (Feed) getItem(position);
        if (feed == null) return null;

        holder.feedTitle.setText(feed.getTitle());
        holder.feedTitle.setVisibility(View.VISIBLE);
        int count = counterList.get(position);
        if(count > 0) {
            holder.count.setPrimaryText(String.valueOf(counterList.get(position)));
            holder.count.setVisibility(View.VISIBLE);
        } else {
            holder.count.setVisibility(View.GONE);
        }
        Glide.with(mainActivityRef.get())
                .load(feed.getImageLocation())
                .error(R.color.light_gray)
                .diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY)
                .fitCenter()
                .dontAnimate()
                .into(new CoverTarget(null, holder.feedTitle, holder.imageView, mainActivityRef.get()));

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ItemlistFragment.newInstance(getItemId(position));
        mainActivityRef.get().loadChildFragment(fragment);
    }

    static class Holder {
        public TextView feedTitle;
        public ImageView imageView;
        public TriangleLabelView count;
    }

}
