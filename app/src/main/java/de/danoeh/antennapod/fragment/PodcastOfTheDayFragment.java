package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter;
import de.danoeh.antennapod.core.glide.ApGlideSettings;

public class PodcastOfTheDayFragment extends Fragment {

    public static final String TAG = "PodcastOfTheDayFragment";

    //initialize only here
    ItunesAdapter.Podcast potd = new ItunesAdapter.Podcast("Joe Rogan Experience",
            "https://is1-ssl.mzstatic.com/image/thumb/Podcasts114/v4/ec/db/85/ecdb85e6-9a4c-4231-0e0c-a2a8953940ea/mza_4877052704493588045.jpg/170x170bb-85.png",
            "http://joeroganexp.joerogan.libsynpro.com/rss", "THE JOE ROGAN EXPERIENCE", 1000);

    TextView potdTitle;
    TextView potdAuthor; //not a variable in the Podcast class, to be populated another way or removed.
    TextView potdDescription;
    ImageView potdImage;
    Button butGoToPodcast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.potd, container, false);

        //call api to set potd here
        potdTitle = (TextView) root.findViewById(R.id.potdTitle);
        potdAuthor = (TextView) root.findViewById(R.id.potdAuthor);
        potdDescription = (TextView) root.findViewById(R.id.potdDescription);
        potdImage = (ImageView) root.findViewById(R.id.potdCover);
        butGoToPodcast = (Button) root.findViewById(R.id.butGoToPodcast);

        Button butGenerateNew = (Button) root.findViewById(R.id.butGenerateNew);

        butGenerateNew.setOnClickListener(v -> {
            // call fxn here to replace potd
            potd = new ItunesAdapter.Podcast("Ben Shapiro Show",
                    "https://is2-ssl.mzstatic.com/image/thumb/Podcasts114/v4/44/d1/13/44d11389-a5e4-fc86-e047-5b16a9e91df4/mza_6616198664604292592.jpg/170x170bb-85.png",
                    "http://feeds.soundcloud.com/users/soundcloud:users:174770374/sounds.rss", "THE BEN SHAPIRO SHOW", 1000);
            populate();
        });

        populate();
        return root;
    }

    private void populate(){
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
}
