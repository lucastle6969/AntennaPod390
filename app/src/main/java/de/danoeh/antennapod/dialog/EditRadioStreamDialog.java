package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Future;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.fragment.RadioStationFragment;

public class EditRadioStreamDialog {

    public void showDialog(Context context, RadioStream radioStream, RadioStationFragment radioStreamFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_radio_stream_label);

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

        final EditText inputTitle = new EditText(context);
        inputTitle.setText(radioStream.getTitle());
        inputTitle.setGravity(Gravity.LEFT);
        inputTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        inputTitle.setSelection(inputTitle.getText().length());
        layout.addView(inputTitle);

        final TextView radioUrlTextView = new TextView(context);
        radioUrlTextView.setText(R.string.enter_radio_url_label);
        layout.addView(radioUrlTextView);

        final EditText inputURL = new EditText(context);
        inputURL.setText(radioStream.getUrl());
        inputURL.setGravity(Gravity.LEFT);
        inputURL.setInputType(InputType.TYPE_CLASS_TEXT);
        inputURL.setSelection(inputURL.getText().length());
        layout.addView(inputURL);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, (dialog, which) -> dialog.cancel());

        builder.setNegativeButton(R.string.cancel_label, (dialog, which) -> dialog.cancel());

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
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

                Future<?> task = DBWriter.updateRadioStream(new RadioStream(radioStream.getId(), inputRadioTitle, inputRadioUrl));
                dialog.dismiss();
                while(!task.isDone()) { /* Wait for radio stream to be inserted */}
                Toast.makeText(context, context.getString(R.string.edited_radio_stream), Toast.LENGTH_LONG).show();
                radioStreamFragment.refresh();
            });
        });
        alertDialog.show();
    }
}
