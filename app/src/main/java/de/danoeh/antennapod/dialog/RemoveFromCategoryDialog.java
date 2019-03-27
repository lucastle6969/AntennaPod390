package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Category;

public class RemoveFromCategoryDialog {

    public void RemoveFromCategoryDialog(){ }

    public void showRemoveFromCategoryDialog(Activity activity, Category category){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.remove_from_category_message);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: code to remove selected feed from category to uncategorized
                Toast.makeText(activity, activity.getString(R.string.successfully_removed_from_category), Toast.LENGTH_LONG).show();
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
