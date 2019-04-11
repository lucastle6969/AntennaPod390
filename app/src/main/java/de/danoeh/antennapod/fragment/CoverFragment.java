package de.danoeh.antennapod.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MediaplayerInfoActivity.MediaplayerInfoContentFragment;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.util.playback.Playable;

/**
 * Displays the cover and the title of a FeedItem.
 */
public class CoverFragment extends Fragment implements MediaplayerInfoContentFragment {

    private static final String TAG = "CoverFragment";
    private static final String ARG_PLAYABLE = "arg.playable";

    private Playable media;

    private View root;
    private TextView txtvPodcastTitle;
    private TextView txtvEpisodeTitle;
    private ImageView imgvCover;
    private TwitterLoginButton twitterLoginButton;
    private String sessionToken;
    private String sessionSecret;
    private Activity context;

    public static CoverFragment newInstance(Playable item) {
        CoverFragment f = new CoverFragment();
        f.media = item;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (media == null) {
            Log.e(TAG, TAG + " was called without media");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.cover_fragment, container, false);
        txtvPodcastTitle = (TextView) root.findViewById(R.id.txtvPodcastTitle);
        txtvEpisodeTitle = (TextView) root.findViewById(R.id.txtvEpisodeTitle);
        imgvCover = (ImageView) root.findViewById(R.id.imgvCover);

        return root;
    }

    private void loadMediaInfo() {
        if (media != null) {
            txtvPodcastTitle.setText(media.getFeedTitle());
            txtvEpisodeTitle.setText(media.getEpisodeTitle());
            Glide.with(this)
                    .load(media.getImageLocation())
                    .diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY)
                    .dontAnimate()
                    .fitCenter()
                    .into(imgvCover);

            twitterLogin();
            if(sessionToken != null){
                View twitterLoginButtonView = getActivity().findViewById(R.id.login_button);
                twitterLoginButtonView.setVisibility(View.GONE);
                View twitterTweetButtonView = getActivity().findViewById(R.id.tweet_button);
                twitterTweetButtonView.setVisibility(View.VISIBLE);
                twitterTweetButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        twitterComposeTweet();
                    }
                });
            }

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(media.getStreamUrl()))
                    .build();

            ShareButton shareButton = (ShareButton) root.findViewById(R.id.fb_share_button);
            shareButton.setShareContent(content);

        } else {
            Log.w(TAG, "loadMediaInfo was called while media was null");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void twitterLogin() {
        twitterLoginButton = root.findViewById(R.id.login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                sessionToken = authToken.token;
                sessionSecret = authToken.secret;
                twitterComposeTweet();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.v(TAG, getString(R.string.twitter_login_error));
            }
        });
    }

    public void twitterComposeTweet() {
        String title = media.getFeedTitle();
        String episode = media.getEpisodeTitle();
        String url = media.getStreamUrl();
        String imageLocation = media.getImageLocation();

        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(getActivity())
                .session(session)
                .text("Go listen to the '" + episode + "' episode from the '" + title + "' podcast! " + url)
                .hashtags("#AntennaPod390")
                .createIntent();
        startActivity(intent);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "On Start");
        super.onStart();
        if (media != null) {
            Log.d(TAG, "Loading media info");
            loadMediaInfo();
        } else {
            Log.w(TAG, "Unable to load media info: media was null");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // prevent memory leaks
        root = null;
    }

    @Override
    public void onMediaChanged(Playable media) {
        if(this.media == media) {
            return;
        }
        this.media = media;
        if (isAdded()) {
            loadMediaInfo();
        }
    }

}
