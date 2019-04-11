package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.achievements.AchievementBuilder;
import de.danoeh.antennapod.core.achievements.AchievementManager;
import de.danoeh.antennapod.core.achievements.AchievementUnlocked;
import de.danoeh.antennapod.core.feed.Bookmark;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.core.util.playback.PlaybackController;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {
    private List<Bookmark> bookmarkList;
    private List<Bookmark> deletedBookmarkList = new ArrayList<>();
    private BookmarkViewHolder view;

    private boolean hideIcons = false;

    private Activity context;

    private PlaybackController controller;

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTimestamp, bookmarkTitle;
        private ImageView playImg, editImg, deleteImg;
        private CheckBox deleteCheckbox;
        private int timestamp;

        public BookmarkViewHolder(View view) {
            super(view);
            txtTimestamp = view.findViewById(R.id.txtvTimestamp);
            bookmarkTitle = view.findViewById(R.id.txtvBookmarkTitle);
            playImg = view.findViewById(R.id.imgBookmarkPlay);
            deleteImg = view.findViewById(R.id.imgBookmarkDelete);
            deleteCheckbox = view.findViewById(R.id.bookmarkCheckBox);
            editImg = view.findViewById(R.id.imgBookmarkEdit);
            timestamp = 0;

            editImg.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            editBookmark(getAdapterPosition());
                        }
                    }
            );

            deleteImg.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeSingleBookmark(getAdapterPosition());
                        }
                    });

            deleteCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((CompoundButton) view).isChecked()){
                        //If checkbox is selected, add the list of bookmarks to delete
                        deletedBookmarkList.add(bookmarkList.get(getAdapterPosition()));
                    } else {
                        //If checkbox is unchecked, remove it from the list of bookmarks to delete
                        deletedBookmarkList.remove(bookmarkList.get(getAdapterPosition()));
                    }
                }
            });

            playImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controller.seekTo(timestamp);
                }
            });

        }

        public void removeSingleBookmark(int position) {
            Bookmark bookmarkToDelete = bookmarkList.get(position);

            //Display dialog to confirm deletion
            showDeleteSingleBookmarkDialog(bookmarkToDelete, position);
        }

        public void removeCheckedBookmarks(List<Bookmark> lstBookmark){
            for(Bookmark b: lstBookmark){
                DBWriter.deleteBookmark(b);
                notifyItemRemoved((int)b.getId());
            }

            bookmarkList.removeAll(lstBookmark);
            deletedBookmarkList.removeAll(lstBookmark);
        }

        public void addToDeletedBookmarkList(int id) {
            deletedBookmarkList.add(bookmarkList.get(id));
        }

        public void editBookmark(int position) {
            Bookmark bookmarkToEdit = bookmarkList.get(position);

            //Display dialog to edit bookmark
            showEditBookmarkDialog(bookmarkToEdit, position);
        }
    }



    public void setContext(Activity context) {
        this.context = context;
    }


    public BookmarkAdapter(List<Bookmark> bookmarkList, PlaybackController controller) {
        this.bookmarkList = bookmarkList;
        this.controller = controller;
    }

    @Override
    public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookmark_container, parent, false);

        view = new BookmarkViewHolder(itemView);

        return view;
    }

    @Override
    public void onBindViewHolder(BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarkList.get(position);
        holder.bookmarkTitle.setText(bookmark.getTitle());
        holder.txtTimestamp.setText(Converter.getDurationStringLong(bookmark.getTimestamp()));
        holder.timestamp = bookmark.getTimestamp();

        holder.deleteImg.setImageResource(R.drawable.ic_delete_grey600_24dp);
        holder.editImg.setImageResource(R.drawable.ic_sort_grey600_24dp);
        holder.playImg.setImageResource(R.drawable.ic_skip_grey600_36dp);

        if(!hideIcons) {
            holder.deleteCheckbox.setVisibility(View.GONE);
            holder.deleteCheckbox.setChecked(false);
            holder.deleteImg.setVisibility(View.VISIBLE);
            holder.editImg.setVisibility(View.VISIBLE);
            holder.playImg.setVisibility(View.VISIBLE);
        }
        else{
            holder.deleteCheckbox.setVisibility(View.VISIBLE);
            holder.deleteImg.setVisibility(View.GONE);
            holder.editImg.setVisibility(View.GONE);
            holder.playImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public void setBookmarkList(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public void resetCheckbox(boolean value){
        hideIcons = value;
        this.view.deleteCheckbox.setChecked(false);
    }

    public void clearDeletedBookmarkList (){
        this.deletedBookmarkList.clear();
    }

    public void deleteCheckedBookmarks(){
        this.view.removeCheckedBookmarks(deletedBookmarkList);
    }

    private void showDeleteSingleBookmarkDialog(Bookmark bookmark, int position) {

        //Get information to display
        String podcastTitle = bookmark.getPodcastTitle();
        String episodeTitle = bookmark.getTitle();
        int timestamp = bookmark.getTimestamp();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_single_bookmark_confirmation);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        //Set podcast title
        final TextView titleView = new TextView(context);
        titleView.setText(podcastTitle);
        titleView.setPadding(50, 50, 50, 10);
        layout.addView(titleView);

        //Set episode title
        final TextView episodeTitleView = new TextView(context);
        episodeTitleView.setText(episodeTitle);
        episodeTitleView.setPadding(50, 10, 50, 50);
        layout.addView(episodeTitleView);

        //Set time
        final TextView timestampView = new TextView(context);
        timestampView.setGravity(Gravity.CENTER_HORIZONTAL);
        timestampView.setText(DateUtils.formatTimestamp(timestamp));
        timestampView.setPadding(50, 10, 50, 50);
        layout.addView(timestampView);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Delete in the db
                DBWriter.deleteBookmark(bookmark);

                //Remove the bookmark from list and notify adapter to update
                bookmarkList.remove(position);
                notifyItemRemoved(position);
            }
        });
        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetCheckbox(false);
                notifyDataSetChanged();
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    public boolean hasBookmarksToDelete() {
        return !deletedBookmarkList.isEmpty();
    }

    private void showEditBookmarkDialog(Bookmark bookmark, int position) {

        //Get information to display
        long bookmark_id = bookmark.getId();
        String podcastTitle = bookmark.getPodcastTitle();
        String bookmarkTitle = bookmark.getTitle();
        String episodeId = bookmark.getUid();
        int timestamp = bookmark.getTimestamp();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_bookmark_header);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        //Display podcast title
        final TextView titleView = new TextView(context);
        titleView.setText(podcastTitle);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setPadding(50, 50, 50, 10);
        layout.addView(titleView);

        //Field to modify the bookmark title
        final EditText editBookmarkTitle = new EditText(context);
        editBookmarkTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        editBookmarkTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        editBookmarkTitle.setPadding(50, 10, 50, 50);
        editBookmarkTitle.setText(bookmarkTitle);
        editBookmarkTitle.setSelection(editBookmarkTitle.getText().length());
        layout.addView(editBookmarkTitle);

        final TextView timestampView = new TextView(context);
        timestampView.setText(DateUtils.formatTimestamp(timestamp));
        timestampView.setGravity(Gravity.CENTER);
        timestampView.setPadding(50, 10, 50, 50);
        layout.addView(timestampView);

        builder.setView(layout);

        builder.setPositiveButton(R.string.save_edit_bookmark_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String new_title = editBookmarkTitle.getText().toString();
                Bookmark editedBookmark = new Bookmark(bookmark_id, new_title, timestamp, podcastTitle, episodeId);
                DBWriter.updateBookmark(editedBookmark);
                bookmarkList.set(position, editedBookmark);
                notifyItemChanged(position);
                AchievementManager.getInstance(new AchievementUnlocked(context))
                        .increment(new ArrayList<>(Arrays.asList(
                                AchievementBuilder.MOD_BKMK_ACHIEVEMENT,
                                AchievementBuilder.MODIFY_ACHIEVEMENT
                        )), context.getApplicationContext());
            }
        });
        builder.setNegativeButton(R.string.cancel_edit_bookmark_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }
}
