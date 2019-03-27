package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.storage.DBWriter;

public class CreateCategoryDialog {

    public void showCreateCategoryDialog(Activity context) {
        // Create category alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.category_alert_title);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        final EditText input = new EditText(context);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(context.getString(R.string.category_hint));
        input.setSelection(input.getText().length());
        layout.addView(input);

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
                        String inputCategoryName = input.getText().toString();
                        if (!inputCategoryName.isEmpty()) {
                            DBWriter.setCategory(new Category(-1, inputCategoryName));
                            dialog.dismiss();
                            Toast.makeText(context, context.getString(R.string.category_success) + inputCategoryName, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.category_error_toast), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        alertDialog.show();

    }
}
