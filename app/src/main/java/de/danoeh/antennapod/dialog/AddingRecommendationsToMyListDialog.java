package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.fragment.RadioStationFragment;

public class AddingRecommendationsToMyListDialog {

    public void showDialog(RadioStream radioStream, Activity context) {

        MainActivity activity = (MainActivity) context;
        RadioStationFragment fragment = (RadioStationFragment) activity.getCurrentFragment();

        // Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.adding_recommended_radio_stream);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50 ,50);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 10, 50, 10);
        layout.setLayoutParams(params);

        final TextView radioTitleTextView = new TextView(context);
        radioTitleTextView.setText(radioStream.getTitle());
        layout.addView(radioTitleTextView);

        final TextView radioUrlTextView = new TextView(context);
        radioUrlTextView.setText(radioStream.getUrl());
        layout.addView(radioUrlTextView);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBWriter.setRadioStream(radioStream);
                Toast.makeText(activity, activity.getString(R.string.successfully_added_recommended_radio_stream), Toast.LENGTH_LONG).show();
                fragment.refresh();
            }
        });

        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();

    }
}
