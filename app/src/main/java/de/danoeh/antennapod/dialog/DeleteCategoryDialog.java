package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.fragment.SubscriptionFragment;

public class DeleteCategoryDialog {

    public void DeleteCategoryDialog(){ }

    public void showDeleteCategoryDialog(Activity activity, Category category, SubscriptionFragment fragment){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.delete_from_category_message);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        final TextView warningMessage = new TextView(activity);
        warningMessage.setText(R.string.delete_from_category_warning);
        warningMessage.setPadding(50, 10, 50, 50);
        warningMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(warningMessage);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Looping through all the feeds from the category and setting them to uncategorized
                for (Long feedId: category.getFeedIds()){
                    DBWriter.updateFeedCategory(feedId, PodDBAdapter.UNCATEGORIZED_CATEGORY_ID);
                }
                DBWriter.deleteCategory(category);

                Toast.makeText(activity, activity.getString(R.string.successfully_deleted_from_category), Toast.LENGTH_LONG).show();
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
