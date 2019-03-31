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

import java.util.List;
import java.util.concurrent.Future;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.fragment.SubscriptionFragment;

public class CreateCategoryDialog {

    private boolean openMoveDialog = false;
    private long lastSelectedFeedId = -1;

    public void showCreateCategoryDialog(Activity context, SubscriptionFragment subscriptionFragment) {
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

                        boolean isDuplicateCategoryName = false;
                        List<Category> categoriesInDb = DBReader.getAllCategories();
                        for (Category category : categoriesInDb) {
                            if (category.getName().equalsIgnoreCase(inputCategoryName)) {
                                isDuplicateCategoryName = true;
                                break;
                            }
                        }

                        if (!inputCategoryName.isEmpty() && !isDuplicateCategoryName) {
                            Future<?> task = DBWriter.setCategory(new Category(-1, inputCategoryName));
                            dialog.dismiss();
                            while(!task.isDone()) { /* Wait for category to be inserted */}
                            Toast.makeText(context, context.getString(R.string.category_success) + inputCategoryName, Toast.LENGTH_LONG).show();
                            subscriptionFragment.refresh();

                            if (openMoveDialog) {
                                new MoveToCategoryDialog().showMoveToCategoryDialog(context, lastSelectedFeedId, subscriptionFragment);
                                openMoveDialog = false;
                                lastSelectedFeedId = -1;
                            }

                        } else {
                            if (isDuplicateCategoryName) {
                                Toast.makeText(context, context.getString(R.string.duplicate_category_error), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, context.getString(R.string.category_error_toast), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        alertDialog.show();

    }

    public void setOpenMoveDialogStatus(boolean value) {
        this.openMoveDialog = value;
    }

    public void setLastSelectedFeedId(long feedId) {
        this.lastSelectedFeedId = feedId;
    }
}
