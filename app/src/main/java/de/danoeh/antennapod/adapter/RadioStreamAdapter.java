package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.util.playback.PlaybackController;

public class RadioStreamAdapter extends RecyclerView.Adapter<RadioStreamAdapter.RadioStreamViewHolder> {
    private List<RadioStream> radioStreamList;
    private RadioStreamViewHolder view;

    private Activity context;

    private PlaybackController controller;

    public class RadioStreamViewHolder extends RecyclerView.ViewHolder {
        private TextView radioStreamTitle, radioStreamUrl;
        private ImageView addToRadioListImg, radioStreamPlayImg;

        public RadioStreamViewHolder(View view) {
            super(view);
            radioStreamTitle = view.findViewById(R.id.txtvRadioStreamTitle);
            radioStreamUrl = view.findViewById(R.id.txtUrl);
            addToRadioListImg = view.findViewById(R.id.imgAddToList);
            radioStreamPlayImg = view.findViewById(R.id.imgRadioStreamPlay);

            addToRadioListImg.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            editBookmark(getAdapterPosition());
                        }
                    }
            );

        }
    }


    public void setContext(Activity context) {
        this.context = context;
    }


    public RadioStreamAdapter(List<RadioStream> radioStreamList, PlaybackController controller) {
        this.radioStreamList = radioStreamList;
        this.controller = controller;
    }

    @Override
    public RadioStreamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.radio_stream_container, parent, false);

        view = new RadioStreamViewHolder(itemView);

        return view;
    }

    @Override
    public int getItemCount() {
        return radioStreamList.size();
    }

    @Override
    public void onBindViewHolder(RadioStreamViewHolder holder, int position) {

        RadioStream radioStream = radioStreamList.get(position);
        holder.radioStreamTitle.setText(radioStream.getTitle());
        holder.radioStreamUrl.setText(radioStream.getUrl());
        holder.addToRadioListImg.setImageResource(R.drawable.ic_add_grey600_24dp);
        holder.radioStreamPlayImg.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
    }

    public void setRadioStreamList(List<RadioStream> radioStreamList) {
        this.radioStreamList = radioStreamList;
    }

}