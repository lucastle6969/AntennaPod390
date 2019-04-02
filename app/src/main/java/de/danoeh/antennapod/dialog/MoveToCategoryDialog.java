package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
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

        final TextView dialogTitle = new TextView(activity);
        dialogTitle.setText(R.string.move_to_category_dialog_title);
        dialogTitle.setPadding(0, 30, 0, 20);
        dialogTitle.setGravity(Gravity.CENTER);
        dialogTitle.setTextSize(18);
        dialogTitle.setTypeface(null, Typeface.BOLD);
        builder.setCustomTitle(dialogTitle);

        // Parent linear layout contains two child linear layouts
        LinearLayout parentLinearLayout = new LinearLayout(activity);
        parentLinearLayout.setPadding(50,0,25,0);

        parentLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        parentLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        parentLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        // This linear layout contains the dropdown menu for selecting a categoy
        LinearLayout spinnerLinearLayout = new LinearLayout(activity);
        spinnerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        spinnerLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));
        spinnerLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        final Spinner categoriesDropdown = new Spinner(activity);

        List<String> categoryTitles = new ArrayList<>();
        for (Category category : categories) {
            if (category.getName().equals(PodDBAdapter.UNCATEGORIZED_CATEGORY_NAME)) {
                continue;
            }

            if (category.getName().equals(currentCategory)) {
                continue;
            }

            categoryTitles.add(category.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, categoryTitles);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        categoriesDropdown.setAdapter(dataAdapter);

        spinnerLinearLayout.addView(categoriesDropdown);

        parentLinearLayout.addView(spinnerLinearLayout);

        // This linear layout contains the button to create a new category
        LinearLayout addCategoryLinearLayout = new LinearLayout(activity);
        addCategoryLinearLayout.setOrientation(LinearLayout.VERTICAL);
        addCategoryLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(200,LinearLayout.LayoutParams.FILL_PARENT));
        addCategoryLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageButton addCategoryBtn = new ImageButton(activity);
        addCategoryBtn.setBackgroundResource(R.drawable.ic_create_new_folder_grey600_24dp);
        addCategoryBtn.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        addCategoryLinearLayout.addView(addCategoryBtn);

        parentLinearLayout.addView(addCategoryLinearLayout);

        // Assigning parent linear layout to the builder
        builder.setView(parentLinearLayout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!categoriesDropdown.getAdapter().isEmpty()) {

                    String chosenCategoryName = categoriesDropdown.getSelectedItem().toString();
                    long categoryId = -1;
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getName().equals(chosenCategoryName)) {
                            categoryId = categories.get(i).getId();
                        }
                    }
                    if (categoryId != -1) {
                        DBWriter.updateFeedCategory(feedId, categoryId);
                        Toast.makeText(activity, activity.getString(R.string.podcast_moved_toast) + chosenCategoryName, Toast.LENGTH_LONG).show();
                        fragment.refresh();
                    }
                }

                fragment.setUserPreferencesToCategoryView();

                dialog.dismiss();

            }
        });
        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();

        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                CreateCategoryDialog createCategoryDialog = new CreateCategoryDialog();
                createCategoryDialog.setOpenMoveDialogStatus(true);
                createCategoryDialog.setLastSelectedFeedId(feedId);
                createCategoryDialog.showCreateCategoryDialog(activity, fragment);
                fragment.setUserPreferencesToCategoryView();
            }
        });

        alertDialog.show();
    }

}