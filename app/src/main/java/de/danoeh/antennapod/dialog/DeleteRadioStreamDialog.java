package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Future;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.fragment.RadioStationFragment;
import de.danoeh.antennapod.fragment.RadioStreamFragment;

public class DeleteRadioStreamDialog {

    public void showDialog(Activity activity, RadioStream radioStream) {
        MainActivity mainActivity = (MainActivity) activity;
        RadioStationFragment fragment = (RadioStationFragment) mainActivity.getCurrentFragment();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.delete_radio_station_label);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50 ,50);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 10, 50, 10);
        layout.setLayoutParams(params);

        final TextView radioDeleteWarning = new TextView(activity);
        radioDeleteWarning.setText(activity.getString(R.string.radio_stream_delete_warning, radioStream.getTitle()));
        layout.addView(radioDeleteWarning);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, (dialog, which) -> {
            Future<?> task = DBWriter.deleteRadioStream(radioStream);
            dialog.dismiss();
            while(!task.isDone()) { /* Wait for radio stream to be inserted */}
            Toast.makeText(activity, activity.getString(R.string.radio_stream_delete_success) + radioStream.getTitle(), Toast.LENGTH_LONG).show();
                fragment.refresh();
        });

        builder.setNegativeButton(R.string.cancel_label, (dialog, which) -> dialog.cancel());

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
