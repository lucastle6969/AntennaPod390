package de.danoeh.antennapod.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.RadioStreamTabAdapter;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.service.playback.PlayerStatus;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.dialog.CreateRadioStreamDialog;

/**
 * Provides actions for displaying the recommended radio list and personal radio list
 */

public class RadioStationFragment extends Fragment {
    public static final String TAG = "RadioStationFragment";
    private int fragmentId;

    private LinearLayout radioPlayerLayout;

    private MediaPlayer mediaPlayer;
    private boolean isRadioPlaying = false;
    private boolean isRadioStreamSetup;

    private RelativeLayout loadingCircle;
    private LinearLayout playBtnLayout;

    private Button playBtn;
    private TextView txtvTitle;
    private TextView txtvURL;

    private RadioStream lastSelectedRadioStream;

    private AsyncTask<String, Void, Boolean> radioStreamTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.radio_stream_label);

        View root = inflater.inflate(R.layout.radio_stations_layout, container, false);
        ViewPager viewPager = root.findViewById(R.id.viewpager);

        RadioStreamTabAdapter adapter = new RadioStreamTabAdapter(getChildFragmentManager(), getResources());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = root.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        ExternalPlayerFragment externalPlayerFragment = ((MainActivity) getActivity()).getExternalPlayerFragment();
        if (externalPlayerFragment != null) {
            PlaybackController controller = externalPlayerFragment.getController();
            if (controller != null && controller.getStatus() == PlayerStatus.PLAYING) {
                controller.init();
                controller.playPause();
            }

            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.remove(externalPlayerFragment);
            transaction.commit();
        }

        radioPlayerLayout = root.findViewById(R.id.radio_player);

        txtvTitle = (TextView) root.findViewById(R.id.txtvRadioTitle);
        txtvURL = (TextView) root.findViewById(R.id.txtvRadioUrl);

        loadingCircle = root.findViewById(R.id.loadingPanel);
        playBtnLayout = root.findViewById(R.id.playBtnLayout);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        playBtn = (Button) root.findViewById(R.id.btnPlay);
        playBtn.setEnabled(false);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRadioPlaying) {
                    mediaPlayer.pause();
                    isRadioPlaying = false;
                    playBtn.setText(R.string.play_label);
                } else {
                    mediaPlayer.start();
                    isRadioPlaying = true;
                    playBtn.setText(R.string.pause_label);
                }
            }
        });

        radioPlayerLayout.setVisibility(View.GONE);
        return root;
    }

    public void updateRadioStream(RadioStream radioStream) {
        if (radioStreamTask != null) {
            radioStreamTask.cancel(true);
        }

        radioPlayerLayout.setVisibility(View.VISIBLE);
        lastSelectedRadioStream = radioStream;

        mediaPlayer.stop();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        playBtn.setEnabled(false);
        isRadioPlaying = false;
        isRadioStreamSetup = false;

        playBtnLayout.setVisibility(View.GONE);
        loadingCircle.setVisibility(View.VISIBLE);

        txtvTitle.setText(radioStream.getTitle());
        txtvURL.setText(radioStream.getUrl());
        radioStreamTask = new PlayTask().execute(radioStream.getUrl());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        fragmentId = this.getId();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mediaPlayer.stop();
        mediaPlayer = null;
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isAdded()) {
            return;
        }
        getActivity().getMenuInflater().inflate(R.menu.radio_streams_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.addRadioStreamBtn:
                CreateRadioStreamDialog radioStreamDialog = new CreateRadioStreamDialog();
                RadioStationFragment rsf = (RadioStationFragment) getFragmentManager().findFragmentById(fragmentId);
                radioStreamDialog.showDialog(getActivity(), rsf);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class PlayTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                isRadioStreamSetup = true;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isRadioStreamSetup;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            playBtn.setEnabled(true);
            playBtn.setText(R.string.play_label);
            playBtnLayout.setVisibility(View.VISIBLE);
            loadingCircle.setVisibility(View.GONE);
        }
    }
}
