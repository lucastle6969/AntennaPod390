package de.danoeh.antennapod.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.Bookmark;
import de.danoeh.antennapod.core.util.DateUtils;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {
    private List<Bookmark> bookmarkList;

    private BookmarkViewHolder view;

    private boolean hideIcons = false;

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private TextView timestamp, bookmarkTitle;
        private ImageView playImg, editImg, deleteImg;
        private CheckBox deleteCheckbox;

        public BookmarkViewHolder(View view) {
            super(view);
            timestamp = view.findViewById(R.id.txtvTimestamp);
            bookmarkTitle = view.findViewById(R.id.txtvBookmarkTitle);
            playImg = view.findViewById(R.id.imgBookmarkPlay);
            deleteImg = view.findViewById(R.id.imgBookmarkDelete);
            deleteCheckbox = view.findViewById(R.id.bookmarkCheckBox);
            editImg = view.findViewById(R.id.imgBookmarkEdit);

            deleteImg.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeBookmark(getAdapterPosition());
                        }
                    });
        }

        public void removeBookmark(int id) {
            bookmarkList.remove(id);
            notifyItemRemoved(id);
        }
    }


    public BookmarkAdapter(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
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
        holder.timestamp.setText(DateUtils.formatTimestamp(bookmark.getTimestamp()));

        //When clicking on the trashcan in the action bar, hide the icons and only show checkbox
        if(!hideIcons) {
            holder.deleteCheckbox.setVisibility(View.GONE);
            holder.deleteImg.setImageResource(R.drawable.ic_delete_grey600_24dp);
            holder.editImg.setImageResource(R.drawable.ic_sort_grey600_24dp);
            holder.playImg.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
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

    public void showCheckBox(boolean bool){
        hideIcons = bool;
    }

}
