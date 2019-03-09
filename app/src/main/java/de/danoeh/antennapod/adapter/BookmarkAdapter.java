package de.danoeh.antennapod.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.danoeh.antennapod.R;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyViewHolder> {
    private List<String> bookmarkList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timestamp, bookmarkTitle;
        public ImageView playImg, editImg, deleteImg;

        public MyViewHolder(View view) {
            super(view);
            timestamp = view.findViewById(R.id.txtvTimestamp);
            bookmarkTitle = view.findViewById(R.id.txtvBookmarkTitle);
            playImg = view.findViewById(R.id.imgBookmarkPlay);
            deleteImg = view.findViewById(R.id.imgBookmarkDelete);
            editImg = view.findViewById(R.id.imgBookmarkEdit);
        }
    }


    public BookmarkAdapter(List<String> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookmark_container, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String bookmark = bookmarkList.get(position);
        holder.bookmarkTitle.setText(bookmark);
        holder.deleteImg.setImageResource(R.drawable.ic_delete_grey600_24dp);
        holder.editImg.setImageResource(R.drawable.ic_sort_grey600_24dp);
        holder.playImg.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }
}
