package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View;
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

        TextView title = new TextView(activity);
        title.setText(R.string.edit_category_dialog_title);
        title.setPadding(100,10,10,10);
        title.setTextSize(20);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(900,400));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        layout.addView(title);

        LinearLayout layoutForTrashCan = new LinearLayout(activity);
        layoutForTrashCan.setOrientation(LinearLayout.HORIZONTAL);
        layoutForTrashCan.setLayoutParams(new LinearLayout.LayoutParams(150, 100));
        layoutForTrashCan.setGravity(Gravity.LEFT);
        layoutForTrashCan.setPadding(10,30,10,10);

        LinearLayout overallLayout = new LinearLayout(activity);
        overallLayout.setOrientation(LinearLayout.HORIZONTAL);
        overallLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        overallLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageButton deleteTrashCan = new ImageButton(activity);
        deleteTrashCan.setBackgroundResource(R.drawable.ic_delete_grey600_24dp);

        layoutForTrashCan.addView(deleteTrashCan);

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
        overallLayout.addView(layoutForTrashCan);
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
                        new DeleteCategoryDialog().showDeleteCategoryDialog(activity, category);
                    }
                }
        );

        alertDialog.show();
        final AlertDialog alertDialog = builder.create();
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