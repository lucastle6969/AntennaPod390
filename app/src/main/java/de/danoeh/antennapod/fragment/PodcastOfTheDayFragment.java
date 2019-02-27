package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PodcastOfTheDayFragment extends Fragment {

    public static final String TAG = "PodcastOfTheDayFragment";
    final String LISTENNOTES_URL = "https://listennotes.p.rapidapi.com/api/v1/";
    final String API_KEY = "387264864dmshfd180124e6714c0p185435jsn064b6c62d311";

    TextView potdTitle;
    TextView potdAuthor; //not a variable in the Podcast class, to be populated another way or removed.
    TextView potdDescription;
    ImageView potdImage;
    Button butGoToPodcast;

    // The iTunes Podcast object does not contain all the info required for POTD functionality, therefore a new object was required to not interfere with the iTunes Activity
    private class DailyPodcast {

        /**
         * The name of the podcast
         */
        private final String title;

        /**
         * URL of the podcast image
         */
        @Nullable
        private final String imageUrl;
        /**
         * URL of the podcast feed
         */
        @Nullable
        private final String feedUrl;

        /**
         * The description of a podcast
         */
        @Nullable
        private final String description;

        /**
         *  The number of episodes of a podcast
         */
        private final int numOfEpisodes;

        /**
         *  The author of a podcast
         */
        private final String author;


        private DailyPodcast(String title, @Nullable String imageUrl, @Nullable String feedUrl, @Nullable String description, int numOfEpisodes, @Nullable String author) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.feedUrl = feedUrl;
            this.description = description;
            this.numOfEpisodes = numOfEpisodes;
            this.author = author;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.potd, container, false);

        AtomicReference<ListenNotesOperation> blue = new AtomicReference<>(new ListenNotesOperation());
        blue.get().execute();

        potdTitle = (TextView) root.findViewById(R.id.potdTitle);
        potdAuthor = (TextView) root.findViewById(R.id.potdAuthor);
        potdDescription = (TextView) root.findViewById(R.id.potdDescription);
        potdImage = (ImageView) root.findViewById(R.id.potdCover);
        butGoToPodcast = (Button) root.findViewById(R.id.butGoToPodcast);

        Button butGenerateNew = (Button) root.findViewById(R.id.butGenerateNew);

        butGenerateNew.setOnClickListener(v -> {
            blue.set(new ListenNotesOperation());
            blue.get().execute();
        });

        return root;
    }

    private void populate(DailyPodcast potd){
        butGoToPodcast.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
            intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, potd.feedUrl);
            intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, potd.title);
            startActivity(intent);
        });

        Glide.with(this)
                .load(potd.imageUrl)
                .placeholder(R.color.light_gray)
                .error(R.color.light_gray)
                .diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY)
                .fitCenter()
                .dontAnimate()
                .into(potdImage);

        potdTitle.setText(potd.title);
        potdDescription.setText(potd.description);
        potdAuthor.setText(potd.author);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // So, we certainly *don't* have an options menu,
        // but unless we say we do, old options menus sometimes
        // persist.  mfietz thinks this causes the ActionBar to be invalidated
        setHasOptionsMenu(true);
    }

    public DailyPodcast getDailyPodcast(String apiRes) {

        JSONObject json;
        DailyPodcast potDay;
        try {
            json = new JSONObject(apiRes);
            String dailyTitle = json.getString("title");
            String dailyImage = json.getString("image");
            String dailyRSS = json .getString("rss");
            String dailyDescription = json.getString("description");
            int dailyEpisodes = Integer.parseInt(json.getString("total_episodes"));
            String dailyAuthor = json.getString("publisher");
            potDay = new DailyPodcast(dailyTitle, dailyImage, dailyRSS, dailyDescription, dailyEpisodes, dailyAuthor);
        } catch (JSONException e) {
            e.printStackTrace();
            potDay = new DailyPodcast("JSON_ERROR", "JSON_ERROR", "JSON_ERROR", "JSON_ERROR", 0, "JSON_ERROR");
        }

        return potDay;
    }

    private class ListenNotesOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String res;
            try {
                String url = LISTENNOTES_URL + "just_listen";
                OkHttpClient client = AntennapodHttpClient.getHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .header("X-RapidAPI-Key", API_KEY)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    res = response.body().string();
                    Log.d("APIResponse_Random", res);
                } else {
                    return "FAIL";
                }

                JSONObject json = new JSONObject(res);
                String podcastID = json.getString("podcast_id");
                url = LISTENNOTES_URL + "podcasts/" + podcastID;
                request = new Request.Builder()
                        .url(url)
                        .header("X-RapidAPI-Key", API_KEY)
                        .build();

                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    res = response.body().string();
                    Log.d("APIResponse_Podcast", res);
                } else {
                    return "FAIL";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "FAIL";
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Response", result);
            DailyPodcast potDay;
            if (result.equals("FAIL")) {
                potDay = new DailyPodcast("API_ERROR", "API_ERROR", "API_ERROR", "API_ERROR", 0, "API_ERROR");
            } else {
                potDay = getDailyPodcast(result);
            }
            populate(potDay);

        }

    }

}
