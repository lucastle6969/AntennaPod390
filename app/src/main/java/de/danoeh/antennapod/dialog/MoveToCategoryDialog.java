package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

    public void MoveToCategoryDialog(){ }

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

        // Parent linear layout contains two child linear layouts
        LinearLayout parentLinearLayout = new LinearLayout(activity);
        parentLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        parentLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        parentLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        // This linear layout contains the dropdown menu for selecting a categoy
        LinearLayout spinnerLinearLayout = new LinearLayout(activity);
        spinnerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        spinnerLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(900, LinearLayout.LayoutParams.FILL_PARENT));
        spinnerLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        //Dropdown to choose the Category to which the podcast will be moved
        final Spinner categoriesDropdown = new Spinner(activity);

        List<String> categoryTitles = new ArrayList<>();
        for(int i=1; i < categories.size(); i++) {
            if(!(categories.get(i).getName().equals(currentCategory))) {
                categoryTitles.add(categories.get(i).getName());
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, categoryTitles);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                if(categories.size() != 1) {
                    String chosenCategoryName = categoriesDropdown.getSelectedItem().toString();
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

        AlertDialog alertDialog = builder.create();

        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                CreateCategoryDialog createCategoryDialog = new CreateCategoryDialog();
                createCategoryDialog.setOpenMoveDialogStatus(true);
                createCategoryDialog.setLastSelectedFeedId(feedId);
                createCategoryDialog.showCreateCategoryDialog(activity, fragment);
            }
        });

        alertDialog.show();
    }

}