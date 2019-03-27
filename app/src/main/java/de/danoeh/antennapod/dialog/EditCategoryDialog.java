package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;

public class EditCategoryDialog {

    public void EditCategoryDialog(){ }

    public void showEditCategoryDialog(Activity activity, Category category){

        //Get information to display
        long categoryId = category.getId();
        String categoryName = category.getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.edit_category_dialog_title);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        //TODO: Add trashcan icon to click and redirect to new dialog

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

        builder.setView(layout);

        builder.setPositiveButton(R.string.save_rename_category_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: code to save changes
            }
        });
        builder.setNegativeButton(R.string.cancel_rename_category_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

}