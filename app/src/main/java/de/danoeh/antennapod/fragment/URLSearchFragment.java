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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_url_search, container, false);

        EditText etxtFeedurl = root.findViewById(R.id.etxtFeedurl);
        Button butConfirm = root.findViewById(R.id.butConfirm);

        // Setting default text for URL search box.
        Bundle args = getArguments();
        if (args != null && args.getString(ARG_FEED_URL) != null) {
            etxtFeedurl.setText(args.getString(ARG_FEED_URL));
        }

        // Setting listener for keyboard search action
        etxtFeedurl.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitURL(etxtFeedurl);
                return true;
            }
            return false;
        });

        // Setting listener for confirm button
        butConfirm.setOnClickListener(v -> submitURL(etxtFeedurl));

        return root;
    }

    /* Opens the detailed view of a searched podcast, if URL is valid.
       Otherwise, displays error text. */
    private void submitURL(EditText urlSearchBox){
        if(URLChecker.validateURL(urlSearchBox.getText().toString())){
            Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
            intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, urlSearchBox.getText().toString());
            intent.putExtra(OnlineFeedViewActivity.ARG_TITLE, getString(R.string.add_feed_label));
            startActivity(intent);
        }
        else{
            urlSearchBox.setError(getString(R.string.url_search_error_invalid));
        }
    }


}
