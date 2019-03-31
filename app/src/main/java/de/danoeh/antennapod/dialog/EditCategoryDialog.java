package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.fragment.SubscriptionFragment;

public class EditCategoryDialog {

    public void showEditCategoryDialog(Activity activity, Category category, SubscriptionFragment fragment){
        long categoryId = category.getId();
        String categoryName = category.getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LinearLayout headerLinearLayout = new LinearLayout(activity);
        headerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        headerLinearLayout.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        headerLinearLayout.setPadding(50,30,25,10);

        final TextView dialogTitle = new TextView(activity);
        dialogTitle.setText(R.string.edit_category_dialog_title);
        dialogTitle.setGravity(Gravity.CENTER);
        dialogTitle.setTextSize(18);
        dialogTitle.setTypeface(null, Typeface.BOLD);
        headerLinearLayout.addView(dialogTitle);

        LinearLayout deleteLinearLayout = new LinearLayout(activity);
        deleteLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        deleteLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        deleteLinearLayout.setGravity(Gravity.RIGHT);

        ImageButton deleteTrashCan = new ImageButton(activity);
        deleteTrashCan.setBackgroundResource(R.drawable.ic_delete_grey600_24dp);
        deleteLinearLayout.addView(deleteTrashCan);

        headerLinearLayout.addView(deleteLinearLayout);
        builder.setCustomTitle(headerLinearLayout);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout overallLayout = new LinearLayout(activity);
        overallLayout.setOrientation(LinearLayout.HORIZONTAL);
        overallLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        overallLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        final TextView renameCategoryHint = new TextView(activity);
        renameCategoryHint.setText(R.string.rename_category_hint);
        renameCategoryHint.setPadding(50,50,50,10);
        layout.addView(renameCategoryHint);

        //Field to rename the Category
        final EditText renameCategory = new EditText(activity);
        renameCategory.setInputType(InputType.TYPE_CLASS_TEXT);
        renameCategory.setGravity(Gravity.CENTER_HORIZONTAL);
        renameCategory.setPadding(50, 10, 50, 50);
        renameCategory.setText(categoryName);
        renameCategory.setSelection(renameCategory.getText().length());
        layout.addView(renameCategory);

        overallLayout.addView(layout);
        builder.setView(overallLayout);

        builder.setPositiveButton(R.string.confirm_label, null);

        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        AlertDialog alertDialog = builder.create();

        deleteTrashCan.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        new DeleteCategoryDialog().showDeleteCategoryDialog(activity, category, fragment);
                    }
                }
        );

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newCategoryName = renameCategory.getText().toString();
                        if(!newCategoryName.isEmpty()) {
                            Category updatedCategory = new Category(categoryId, newCategoryName);
                            DBWriter.updateCategory(updatedCategory);
                            dialog.dismiss();
                            Toast.makeText(activity, activity.getString(R.string.category_renamed_toast) + newCategoryName, Toast.LENGTH_LONG).show();
                            fragment.refresh();
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.category_error_toast), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        alertDialog.show();
    }

}