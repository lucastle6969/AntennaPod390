package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MediaplayerActivity;
import de.danoeh.antennapod.core.feed.Bookmark;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;

public class InsertBookmarkDialog {

    private PlaybackController controller;
    private Bookmark insertedBookmark;
    private int timestamp;

    public InsertBookmarkDialog(PlaybackController controller) {
        this.controller = controller;
    }

    public void showSetBookmarkDialog(Activity context, TextView txtvPosition) {
        if(controller == null || context == null)
            return;

        Playable media = controller.getMedia();
        String podcastTitle = media.getFeedTitle();
        String episodeTitle = media.getEpisodeTitle();
        timestamp = controller.getPosition();
        if(timestamp == PlaybackService.INVALID_TIME && txtvPosition != null){
            // Assign txtvPosition time value if controller was unable to get timestamp
            timestamp = Converter.durationStringLongToMs(txtvPosition.getText().toString());
        }

        String episodeId = media.getIdentifier().toString();

        // Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.bookmark_alert_title);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        final TextView titleView = new TextView(context);
        titleView.setText(podcastTitle);
        titleView.setPadding(50, 50, 50, 10);
        layout.addView(titleView);

        final TextView episodeTitleView = new TextView(context);
        episodeTitleView.setText(episodeTitle);
        episodeTitleView.setPadding(50, 10, 50, 50);
        layout.addView(episodeTitleView);

        final TextView timestampView = new TextView(context);
        timestampView.setGravity(Gravity.CENTER_HORIZONTAL);
        timestampView.setText(DateUtils.formatTimestamp(timestamp));
        timestampView.setPadding(50, 10, 50, 50);
        layout.addView(timestampView);

        List<Bookmark> bookmarks = DBReader.getBookmarksWithTitleAndUID(podcastTitle, episodeId);
        String defaultBookmarkTitle = context.getString(R.string.bookmark_label) + " " + Integer.toString(bookmarks.size() + 1);

        final EditText input = new EditText(context);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(defaultBookmarkTitle);
        input.setSelection(input.getText().length());
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input_title = input.getText().toString();
                insertedBookmark = new Bookmark(0, input_title, timestamp, podcastTitle, episodeId);

                MediaplayerActivity activity = (MediaplayerActivity) context;
                activity.setNewBookmark(insertedBookmark);
                resumePlaybackController();
            }
        });
        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                resumePlaybackController();
            }
        });

        builder.create().show();
    }

    private void resumePlaybackController() {
        if(controller == null) {
            return;
        }
        controller.init();
        controller.playPause();
    }

}
