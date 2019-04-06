package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.Future;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.fragment.RadioStreamFragment;

public class CreateRadioStreamDialog {

    public void showDialog(Context context, RadioStreamFragment radioStreamFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.radio_stream_add_dialog_title);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        final EditText inputTitle = new EditText(context);
        inputTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        inputTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        inputTitle.setHint(context.getString(R.string.radio_stream_dialog_hint_title));
        inputTitle.setSelection(inputTitle.getText().length());
        layout.addView(inputTitle);

        final EditText inputUrl = new EditText(context);
        inputUrl.setGravity(Gravity.CENTER_HORIZONTAL);
        inputUrl.setInputType(InputType.TYPE_CLASS_TEXT);
        inputUrl.setHint(context.getString(R.string.radio_stream_dialog_hint_url));
        inputUrl.setSelection(inputUrl.getText().length());
        layout.addView(inputUrl);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, (dialog, which) -> {
            String inputRadioStreamTitle = inputTitle.getText().toString();
            String inputRadioStreamURL = inputUrl.getText().toString();

            if (!inputRadioStreamTitle.isEmpty() && !inputRadioStreamURL.isEmpty()) {
                Future<?> task = DBWriter.setRadioStream(new RadioStream(-1, inputRadioStreamTitle, inputRadioStreamURL));
                dialog.dismiss();
                while(!task.isDone()) { /* Wait for radio stream to be inserted */}
                Toast.makeText(context, context.getString(R.string.radio_stream_create_success) + inputRadioStreamTitle, Toast.LENGTH_LONG).show();
//                radioStreamFragment.refresh();


            } else {
                if (inputRadioStreamTitle.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.radio_stream_title_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.radio_stream_url_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel_label, (dialog, which) -> dialog.cancel());

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
