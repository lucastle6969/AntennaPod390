package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;

public class MoveToCategoryDialog {

    public void MoveToCategoryDialog(){ }

    public void showMoveToCategoryDialog(Activity activity, long feedId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.move_to_category_dialog_title);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        //Dropdown to choose the Category to which the podcast will be moved
        final Spinner categoriesDropdown = new Spinner(activity);
        List<Category> categories = DBReader.getAllCategories();

        List<String> categoryTitles = new ArrayList<String>();
        for(int i=1; i < categories.size(); i++) {
            categoryTitles.add(categories.get(i).getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, categoryTitles);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesDropdown.setAdapter(dataAdapter);
        layout.addView(categoriesDropdown);

        //TODO: add + icon to redirect to an add a new category dialog

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBWriter.updateFeedCategory(1, 2);
                String chosenCategoryName = categoriesDropdown.getSelectedItem().toString();
                long categoryId = 0;
                for(int i=0; i < categories.size(); i++){
                    if(categories.get(i).getName().equals(chosenCategoryName)){
                        categoryId = categories.get(i).getId();
                    }
                }
                if(categoryId != 0) {
                    DBWriter.updateFeedCategory(feedId, categoryId);
                    Toast.makeText(activity, activity.getString(R.string.podcast_moved_toast) + chosenCategoryName, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

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