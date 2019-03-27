package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.storage.DBWriter;

public class MoveToCategoryDialog {

    public void MoveToCategoryDialog(){ }

    public void showMoveToCategoryDialog(Activity activity, long feedId, Category category){

        //Get information to display
        long categoryId = category.getId();
        String categoryName = category.getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.move_to_category_dialog_title);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        //Dropdown to choose the Category to which the podcast will be moved
        final Spinner categoriesDropdown = new Spinner(activity);
        //TODO: logic to be added to show existing categories
        layout.addView(categoriesDropdown);

        //TODO: add + icon to redirect to an add a new category dialog

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: code to save changes
                String chosenCategoryName = categoriesDropdown.getSelectedItem().toString();

                //DBWriter.updateFeedCategory(feedId,get)
                dialog.dismiss();
                Toast.makeText(activity, activity.getString(R.string.podcast_moved_toast) + chosenCategoryName, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

}