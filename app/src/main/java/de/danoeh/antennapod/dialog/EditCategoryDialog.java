package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
    }

}