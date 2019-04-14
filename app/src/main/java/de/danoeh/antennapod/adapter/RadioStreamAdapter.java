package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.dialog.AddingRecommendationsToMyListDialog;
import de.danoeh.antennapod.fragment.RadioStreamFragment;

public class RadioStreamAdapter extends RecyclerView.Adapter<RadioStreamAdapter.RadioStreamViewHolder> {
    private List<RadioStream> radioStreamList;
    private RadioStreamViewHolder view;

    private Activity context;
    private RadioStreamFragment.RadioStreamListener radioStreamListener;

    private PlaybackController controller;

    private boolean isRecommended;

    private int position;

    public class RadioStreamViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView radioStreamTitle, radioStreamUrl;
        private ImageView addToRadioListImg, radioStreamPlayImg;

        public RadioStreamViewHolder(View view, boolean hasContextMenu) {
            super(view);
            radioStreamTitle = view.findViewById(R.id.txtvRadioStreamTitle);
            radioStreamUrl = view.findViewById(R.id.txtUrl);
            addToRadioListImg = view.findViewById(R.id.imgAddToList);
            radioStreamPlayImg = view.findViewById(R.id.imgRadioStreamPlay);

            addToRadioListImg.setOnClickListener(v -> {
                        RadioStream radioStream = radioStreamList.get(getAdapterPosition());
                        AddingRecommendationsToMyListDialog addingDialog = new AddingRecommendationsToMyListDialog();
                        addingDialog.showDialog(radioStream, context);
                    });

            radioStreamPlayImg.setOnClickListener(v -> {
                String radioTitle = radioStreamTitle.getText().toString();
                String radioUrl = radioStreamUrl.getText().toString();
                RadioStream selectedRadioStream = new RadioStream(-1, radioTitle, radioUrl);
                radioStreamListener.onRadioStreamSelected(selectedRadioStream);
            });

            if(hasContextMenu){
                view.setOnCreateContextMenuListener(this);

                view.setOnLongClickListener(v -> {
                    setPosition(getAdapterPosition());
                    return false;
                });
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = context.getMenuInflater();
            inflater.inflate(R.menu.radio_stream_context, menu);

            menu.setHeaderTitle(radioStreamTitle.getText());
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


    public RadioStreamAdapter(List<RadioStream> radioStreamList, PlaybackController controller, Boolean isRecommended, RadioStreamFragment.RadioStreamListener radioStreamListener) {
        this.radioStreamList = radioStreamList;
        this.controller = controller;
        this.isRecommended = isRecommended;
        this.radioStreamListener = radioStreamListener;
    }

    @Override
    public RadioStreamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.radio_stream_container, parent, false);

        view = new RadioStreamViewHolder(itemView, !isRecommended);

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
        holder.addToRadioListImg.setImageResource(R.drawable.ic_add_playlist_light);
        holder.radioStreamPlayImg.setImageResource(R.drawable.ic_settings_input_antenna_grey600_24dp);

        boolean duplicate = DBReader.isDuplicateRadioStreamUrl(radioStream.getUrl());

        if(isRecommended && !duplicate){
            holder.addToRadioListImg.setVisibility(View.VISIBLE);
        }
        else{
            holder.addToRadioListImg.setVisibility(View.GONE);
        }
    }

    public void setRadioStreamList(List<RadioStream> radioStreamList) {
        this.radioStreamList = radioStreamList;
    }
}