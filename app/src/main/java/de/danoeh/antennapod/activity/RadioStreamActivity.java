package de.danoeh.antennapod.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import de.danoeh.antennapod.R;

public class RadioStreamActivity extends Activity {
    private final static String stream = "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio2_mf_p";
//    private final static String stream = "http://17833.live.streamtheworld.com/XLTNFM_SC";

    Button play;
    MediaPlayer mediaPlayer;
    boolean started = false;
    boolean prepared = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_stream);

        play = (Button) findViewById(R.id.btnPlay);
        play.setEnabled(false);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (started) {
                    mediaPlayer.pause();
                    started = false;
                    play.setText("Play");
                } else {
                    mediaPlayer.start();
                    started = true;
                    play.setText("Pause");
                }
            }
        });

        new PlayTask().execute(stream);
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* if(started)
            mediaPlayer.pause();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(started)
            mediaPlayer.start();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mediaPlayer.release();
    }

    private class PlayTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            play.setEnabled(true);
            play.setText("Play");
        }

    }
}