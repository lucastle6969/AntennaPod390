package de.danoeh.antennapod.fragment.gpodnet;

import java.util.List;

import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;

public class GpodderSearchFragment extends PodcastSearchListFragment {
    private static final int PODCAST_COUNT = 50;

    @Override
    protected List<GpodnetPodcast> loadPodcastData(GpodnetService service) throws GpodnetServiceException {
        return service.getPodcastToplist(PODCAST_COUNT);
    }

    @Override
    protected List<GpodnetPodcast> reloadPodcastData (GpodnetService service, String query) throws GpodnetServiceException {
        return service.searchPodcasts(query, 0);
    }
}
