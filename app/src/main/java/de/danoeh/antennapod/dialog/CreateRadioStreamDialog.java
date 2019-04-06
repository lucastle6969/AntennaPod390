package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Future;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.fragment.RadioStationFragment;

public class CreateRadioStreamDialog {

    public void showDialog(Activity context, RadioStationFragment fragment) {
        // Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_radio_stream_label);

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
        radioTitleTextView.setText(R.string.enter_radio_title_label);
        layout.addView(radioTitleTextView);

        List<RadioStream> radioStreamList = DBReader.getAllUserRadioStreams();
        String defaultRadioStreamTitle = context.getString(R.string.radio_stream_label) + " " + Integer.toString(radioStreamList.size() + 1);

        final EditText inputTitle = new EditText(context);
        inputTitle.setGravity(Gravity.LEFT);
        inputTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        inputTitle.setText(defaultRadioStreamTitle);
        inputTitle.setSelection(inputTitle.getText().length());
        layout.addView(inputTitle);

        final TextView radioUrlTextView = new TextView(context);
        radioUrlTextView.setText(R.string.enter_radio_url_label);
        layout.addView(radioUrlTextView);

        final EditText inputURL = new EditText(context);
        inputURL.setGravity(Gravity.LEFT);
        inputURL.setInputType(InputType.TYPE_CLASS_TEXT);
        inputURL.setHint(R.string.radio_url_hint);
        inputURL.setSelection(inputURL.getText().length());
        layout.addView(inputURL);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, null);
        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputRadioTitle = inputTitle.getText().toString();
                        String inputRadioUrl = inputURL.getText().toString();

                        if (inputRadioTitle.isEmpty()) {
                            Toast.makeText(context, context.getString(R.string.missing_radio_title_error), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (inputRadioUrl.isEmpty()) {
                            Toast.makeText(context, context.getString(R.string.missing_radio_url_error), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!URLUtil.isValidUrl(inputRadioUrl)) {
                            Toast.makeText(context, context.getString(R.string.url_search_error_invalid), Toast.LENGTH_LONG).show();
                            return;
                        }

                        Future<?> task = DBWriter.setRadioStream(new RadioStream(-1, inputRadioTitle, inputRadioUrl));
                        dialog.dismiss();
                        while(!task.isDone()) { /* Wait for radio stream to be inserted */}
                        Toast.makeText(context, context.getString(R.string.added_radio_stream), Toast.LENGTH_LONG).show();
                        fragment.refresh();
                    }
                });
            }
        });

        alertDialog.show();
    }
}
