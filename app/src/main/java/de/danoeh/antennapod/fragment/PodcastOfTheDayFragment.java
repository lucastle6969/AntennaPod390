package de.danoeh.antennapod.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
    TextView potdAuthor;
    TextView potdDescription;
    ImageView potdImage;
    Button butGoToPodcast;
    Button butGenerateNew;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;


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
        potdTitle = (TextView) root.findViewById(R.id.potdTitle);
        potdAuthor = (TextView) root.findViewById(R.id.potdAuthor);
        potdDescription = (TextView) root.findViewById(R.id.potdDescription);
        potdImage = (ImageView) root.findViewById(R.id.potdCover);
        butGoToPodcast = (Button) root.findViewById(R.id.butGoToPodcast);
        butGenerateNew = (Button) root.findViewById(R.id.butGenerateNew);
        AtomicReference<ListenNotesOperation> potdOp = new AtomicReference<>(new ListenNotesOperation());

        prefs = getActivity().getSharedPreferences("POTD", Context.MODE_PRIVATE);
        if (compareDates()){
            String title = prefs.getString("title", "");
            String imageUrl = prefs.getString("imageUrl", "");
            String feedUrl = prefs.getString("feedUrl", "");
            String description = prefs.getString("description", "");
            int numOfEpisodes = prefs.getInt("numOfEpisodes", 0);
            String author = prefs.getString("title", "");

            DailyPodcast storedPodcast = new DailyPodcast(title, imageUrl, feedUrl, description, numOfEpisodes, author);
            populate(storedPodcast);

        }else {
            potdOp.get().execute();
        }

        butGenerateNew.setOnClickListener(v -> {
            potdOp.set(new ListenNotesOperation());
            potdOp.get().execute();
        });
        return root;
    }

    private String getTodayDateStr(){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        return dateFormat.format(date);
    }

    private boolean compareDates(){
        String storedDate = prefs.getString("date", "");
        return (getTodayDateStr().equals(storedDate));
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

        if (!compareDates()){
            edit = prefs.edit();
            edit.putString("date", getTodayDateStr());
            edit.putString("title", potd.title);
            edit.putString("imageUrl", potd.imageUrl);
            edit.putString("feedUrl", potd.feedUrl);
            edit.putString("description", potd.description);
            edit.putInt("numOfEpisodes", potd.numOfEpisodes);
            edit.putString("author", potd.author);
            edit.apply();
        }

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
        DailyPodcast potd;
        try {
            json = new JSONObject(apiRes);
            String dailyTitle = json.getString("title");
            String dailyImage = json.getString("image");
            String dailyRSS = json .getString("rss");
            String dailyDescription = json.getString("description");
            int dailyEpisodes = Integer.parseInt(json.getString("total_episodes"));
            String dailyAuthor = json.getString("publisher");
            potd = new DailyPodcast(dailyTitle, dailyImage, dailyRSS, dailyDescription, dailyEpisodes, dailyAuthor);
        } catch (JSONException e) {
            e.printStackTrace();
            potd = new DailyPodcast("JSON_ERROR", "JSON_ERROR", "JSON_ERROR", "JSON_ERROR", 0, "JSON_ERROR");
        }

        return potd;
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
