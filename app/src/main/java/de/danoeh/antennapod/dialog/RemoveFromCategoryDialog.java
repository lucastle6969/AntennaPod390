package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.fragment.SubscriptionFragment;

public class RemoveFromCategoryDialog {

    public void RemoveFromCategoryDialog(){ }

    public void showRemoveFromCategoryDialog(Activity activity, List<Category> categoryArrayList, long feedId, SubscriptionFragment fragment){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.remove_from_category_message);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        String currentCategoryName = "";
        for (Category category: categoryArrayList){
            if (category.getFeedIds().contains(feedId)){
                currentCategoryName = category.getName();
            }
        }

        String message = activity.getString(R.string.remove_from_category_message) + " from " + currentCategoryName;
        final TextView categoryWarning = new TextView(activity);
        categoryWarning.setText(message);
        categoryWarning.setPadding(50, 10, 50, 50);
        categoryWarning.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(categoryWarning);


        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBWriter.updateFeedCategory(feedId, PodDBAdapter.UNCATEGORIZED_CATEGORY_ID);
                Toast.makeText(activity, activity.getString(R.string.successfully_removed_from_category), Toast.LENGTH_LONG).show();

                fragment.setUserPreferencesToCategoryView();
                fragment.refresh();
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
