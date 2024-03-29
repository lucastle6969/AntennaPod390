package de.danoeh.antennapod.adapter.itunes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.mfietz.fyydlin.SearchHit;

public class ItunesAdapter extends ArrayAdapter<ItunesAdapter.Podcast> {
    /**
     * Related Context
     */
    private final Context context;

    /**
     * List holding the podcasts found in the search
     */
    private final List<Podcast> data;

    /**
     * Constructor.
     *
     * @param context Related context
     * @param objects Search result
     */
    public ItunesAdapter(Context context, List<Podcast> objects) {
        super(context, 0, objects);
        this.data = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //Current podcast
        Podcast podcast = data.get(position);

        //ViewHolder
        PodcastViewHolder viewHolder;

        //Resulting view
        View view;

        //Handle view holder stuff
        if(convertView == null) {
            view = ((MainActivity) context).getLayoutInflater()
                    .inflate(R.layout.itunes_podcast_listitem, parent, false);
            viewHolder = new PodcastViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (PodcastViewHolder) view.getTag();
        }

        //Set the title
        viewHolder.titleView.setText(podcast.title);
        if (podcast.description != null){
            viewHolder.urlView.setText(podcast.description);
            viewHolder.urlView.setVisibility(View.VISIBLE);
        }
        else if(podcast.feedUrl != null) {
            viewHolder.urlView.setText(podcast.feedUrl);
            viewHolder.urlView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.urlView.setVisibility(View.GONE);
        }

        if(podcast.numOfEpisodes == 0){ // If the number of episodes of the podcast is 0 (i.e. could not be retrieved), hide the podcast episode count and icon.
            viewHolder.episodesIconView.setVisibility(View.GONE);
            viewHolder.episodesView.setVisibility(View.GONE);
        } else {
            viewHolder.episodesIconView.setVisibility(View.VISIBLE);
            viewHolder.episodesView.setVisibility(View.VISIBLE);
            viewHolder.episodesView.setText(String.format("%s", podcast.numOfEpisodes));
        }


        //Update the empty imageView with the image from the feed
        Glide.with(context)
                .load(podcast.imageUrl)
                .placeholder(R.color.light_gray)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .dontAnimate()
                .into(viewHolder.coverView);

        //Feed the grid view
        return view;
    }

    /**
     * Represents an individual podcast on the iTunes Store.
     */
    public static class Podcast { //TODO: Move this out eventually. Possibly to core.itunes.model

        /**
         * The name of the podcast
         */
        public final String title;

        /**
         * URL of the podcast image
         */
        @Nullable
        public final String imageUrl;
        /**
         * URL of the podcast feed
         */
        @Nullable
        public final String feedUrl;

        /**
         * The description of a podcast
         */
        @Nullable
        public final String description;

        /**
         *  The number of episodes of a podcast
         */
        public final int numOfEpisodes;


        private Podcast(String title, @Nullable String imageUrl, @Nullable String feedUrl, @Nullable String description, int numOfEpisodes) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.feedUrl = feedUrl;
            this.description = description;
            this.numOfEpisodes = numOfEpisodes;
        }

        /**
         * Constructs a Podcast instance from a iTunes search result
         *
         * @param json object holding the podcast information
         * @throws JSONException
         */
        public static Podcast fromSearch(JSONObject json) {
            String title = json.optString("collectionName", "");
            String imageUrl = json.optString("artworkUrl100", null);
            String feedUrl = json.optString("feedUrl", null);
            String description = json.optJSONArray("genres").optString(0); // Create description from genre due to lack of description provided by API.
            int numOfEpisodes = json.optInt("trackCount", 0);
            return new Podcast(title, imageUrl, feedUrl, description, numOfEpisodes);
        }

        /**
         * Constructs a Podcast instance from a fyyd search result
         * @param searchHit
         * @return
         */
        public static Podcast fromSearch(SearchHit searchHit) {
            return new Podcast(searchHit.getTitle(), searchHit.getImageUrl(), searchHit.getXmlUrl(), searchHit.getDescription(), searchHit.getCountEpisodes());
        }

        /**
         * Constructs a Podcast instance from iTunes toplist entry
         *
         * @param json object holding the podcast information
         * @throws JSONException
         */
        public static Podcast fromToplist(JSONObject json) throws JSONException {
            String title = json.getJSONObject("title").getString("label");
            String imageUrl = null;
            JSONArray images =  json.getJSONArray("im:image");
            for(int i=0; imageUrl == null && i < images.length(); i++) {
                JSONObject image = images.getJSONObject(i);
                String height = image.getJSONObject("attributes").getString("height");
                if(Integer.parseInt(height) >= 100) {
                    imageUrl = image.getString("label");
                }
            }
            String feedUrl = "https://itunes.apple.com/lookup?id=" +
                    json.getJSONObject("id").getJSONObject("attributes").getString("im:id");
            String description = json.getJSONObject("summary").getString("label");

            int numOfEpisodes = 0;

            return new Podcast(title, imageUrl, feedUrl, description, numOfEpisodes);
        }

    }

    /**
     * View holder object for the GridView
     */
    static class PodcastViewHolder {

        /**
         * ImageView holding the Podcast image
         */
        final ImageView coverView;

        /**
         * TextView holding the Podcast title
         */
        final TextView titleView;

        /**
         * TextView for holding Podcast url
         */
        final TextView urlView;

        /**
         * TextView for holding Podcast epidode number
         */
        final TextView episodesView;

        /**
         * ImageView for holding Podcast epidode number icon
         */
        final ImageView episodesIconView;

        /**
         * Constructor
         * @param view GridView cell
         */
        PodcastViewHolder(View view){
            coverView = (ImageView) view.findViewById(R.id.imgvCover);
            titleView = (TextView) view.findViewById(R.id.txtvTitle);
            urlView = (TextView) view.findViewById(R.id.txtvUrl);
            episodesView = (TextView) view.findViewById(R.id.txtvEpisodes);
            episodesIconView = (ImageView) view.findViewById(R.id.imgFeed);
        }
    }
}
