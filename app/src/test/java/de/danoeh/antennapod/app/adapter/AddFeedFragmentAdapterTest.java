package de.danoeh.antennapod.app.adapter;

import android.app.Application;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.test.ApplicationTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.adapter.AddPodcastFragmentAdapter;
import de.danoeh.antennapod.fragment.FyydSearchFragment;
import de.danoeh.antennapod.fragment.ItunesSearchFragment;
import de.danoeh.antennapod.fragment.URLSearchFragment;
import de.danoeh.antennapod.fragment.gpodnet.PodcastTopListFragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AddFeedFragmentAdapterTest extends ApplicationTestCase<Application> {

    private FragmentManager fm;
    private Resources resources;
    private AddPodcastFragmentAdapter adapter;

    public AddFeedFragmentAdapterTest() {
        super(Application.class);
    }

    @Before
    public void setUp() {
        fm = mock(FragmentManager.class);
        resources = mock(Resources.class);

        when(resources.getString(R.string.tab_itunes)).thenReturn("iTunes");
        when(resources.getString(R.string.tab_gpodder)).thenReturn("gPodder");
        when(resources.getString(R.string.tab_fyyd)).thenReturn("fyyd");
        when(resources.getString(R.string.tab_url)).thenReturn("url");

        adapter = new AddPodcastFragmentAdapter(fm, resources);
    }

    @Test
    public void testGetItemFragment() {
        assertTrue(adapter.getItem(0) instanceof ItunesSearchFragment);
        assertTrue(adapter.getItem(1) instanceof PodcastTopListFragment);
        assertTrue(adapter.getItem(2) instanceof FyydSearchFragment);
        assertTrue(adapter.getItem(3) instanceof URLSearchFragment);
        assertNull(adapter.getItem(4));
    }

    @Test
    public void testGetPageTitle() {
        CharSequence title;

        title = adapter.getPageTitle(0);
        assertEquals("iTunes", title);

        title = adapter.getPageTitle(1);
        assertEquals("gPodder", title);

        title = adapter.getPageTitle(2);
        assertEquals("fyyd", title);

        title = adapter.getPageTitle(3);
        assertEquals("url", title);
    }
}