package de.danoeh.antennapod.dialog;

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
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.fragment.RadioStreamFragment;

public class DeleteRadioStreamDialog {

    public void showDialog(Context context, RadioStream radioStream, RadioStreamFragment radioStreamFragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.radio_stream_delete_dialog_title);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        final TextView warningMessage = new TextView(context);
        warningMessage.setText(R.string.radio_stream_delete_prompt);
        warningMessage.setPadding(50, 10, 50, 50);
        warningMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(warningMessage);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, (dialog, which) -> {
            Future<?> task = DBWriter.deleteRadioStream(radioStream);
            dialog.dismiss();
            while(!task.isDone()) { /* Wait for radio stream to be inserted */}
            Toast.makeText(context, context.getString(R.string.radio_stream_delete_success) + radioStream.getTitle(), Toast.LENGTH_LONG).show();
//                radioStreamFragment.refresh();

        });

        builder.setNegativeButton(R.string.cancel_label, (dialog, which) -> dialog.cancel());

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
