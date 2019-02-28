package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;


import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.core.util.URLChecker;

public class URLSearchFragment extends Fragment {
    private static final String ARG_FEED_URL = "feedurl";

    private EditText etxtFeedurl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_url_search, container, false);

        etxtFeedurl = root.findViewById(R.id.etxtFeedurl);
        Button butConfirm = root.findViewById(R.id.butConfirm);

        // Setting default text for URL search box.
        Bundle args = getArguments();
        if (args != null && args.getString(ARG_FEED_URL) != null) {
            etxtFeedurl.setText(args.getString(ARG_FEED_URL));
        }

        etxtFeedurl.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitURL();
                return true;
            }
            return false;
        });

        butConfirm.setOnClickListener(v -> {
            submitURL();
        });

        return root;
    }

    private void submitURL(){
        if(URLChecker.validateURL(etxtFeedurl.getText().toString())){
            Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
            intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, etxtFeedurl.getText().toString());
            intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, getString(R.string.add_feed_label));
            startActivity(intent);
        }
        else{
            etxtFeedurl.setError("Please enter a valid URL.");
        }
    }


}
