package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
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
import de.danoeh.antennapod.fragment.SubscriptionFragment;

public class MoveToCategoryDialog {

    public void showMoveToCategoryDialog(Activity activity, long feedId, SubscriptionFragment fragment) {
        String currentCategory = "";

        List<Category> categories = DBReader.getAllCategories();
        for(int i=0; i < categories.size(); i++){
            if(categories.get(i).getFeedIds().contains(feedId)){
                currentCategory = categories.get(i).getName();
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.move_to_category_dialog_title);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        final Spinner categoriesDropdown = new Spinner(activity);

        List<String> categoryTitles = new ArrayList<String>();
        for(int i=1; i < categories.size(); i++) {
            if(!(categories.get(i).getName().equals(currentCategory))) {
                categoryTitles.add(categories.get(i).getName());
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, categoryTitles);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        categoriesDropdown.setAdapter(dataAdapter);
        categoriesDropdown.setPadding(10, 20, 10, 10);
        layout.addView(categoriesDropdown);

        //TODO: add + icon to redirect to an add a new category dialog

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String chosenCategoryName = categoriesDropdown.getSelectedItem().toString();
                if(!chosenCategoryName.isEmpty()) {
                    long categoryId = 0;
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getName().equals(chosenCategoryName)) {
                            categoryId = categories.get(i).getId();
                        }
                    }
                    if (categoryId != 0) {
                        DBWriter.updateFeedCategory(feedId, categoryId);
                        Toast.makeText(activity, activity.getString(R.string.podcast_moved_toast) + chosenCategoryName, Toast.LENGTH_LONG).show();
                        fragment.refresh();
                    }
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