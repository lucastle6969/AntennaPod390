package de.danoeh.antennapod.fragment;

import android.app.Application;

import android.test.ApplicationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import de.danoeh.antennapod.core.feed.Feed;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class CategoryTest extends ApplicationTestCase<Application> {

    private SubscriptionFragment subFragment;
    private List<Feed> feeds;

    public CategoryTest() {
        super(Application.class);
    }

    @Before
    public void setUp() {
        feeds = new ArrayList();
        subFragment = new SubscriptionFragment();
        Feed feed1 = new Feed("The Joe Rogan Experience");
        Feed feed2 = new Feed("Always Open");
        Feed feed3 = new Feed("Below the Belt");
        Feed feed4 = new Feed("The Fighter and the Kid");
        Feed feed5 = new Feed("RT Podcast");
        Feed feed6 = new Feed("This Is Love");
        Feed feed7 = new Feed("Errthang");
        Feed feed8 = new Feed("Edge of Fame");
        Feed feed9 = new Feed("Last Seen");
        Feed feed10 = new Feed("Happy Face");
        feeds.add(feed1);
        feeds.add(feed2);
        feeds.add(feed3);
        feeds.add(feed4);
        feeds.add(feed5);
        feeds.add(feed6);
        feeds.add(feed7);
        feeds.add(feed8);
        feeds.add(feed9);
        feeds.add(feed10);

    }

    @Test
    public void testFeedSearch() {
        // Test multiple matches
        List<Feed> result1 = subFragment.search("The", feeds);
        assertEquals(3, result1.size());
        assertEquals("The Joe Rogan Experience", result1.get(0).getFeedTitle());
        assertEquals("Below the Belt", result1.get(1).getFeedTitle());
        assertEquals("The Fighter and the Kid", result1.get(2).getFeedTitle());

        // Test single exact match
        List<Feed> result2 = subFragment.search("Below the Belt", feeds);
        assertEquals(1, result2.size());
        assertEquals("Below the Belt", result2.get(0).getFeedTitle());

        // Test no matches
        List<Feed> result3 = subFragment.search("The Angry Chicken", feeds);
        assertEquals(0, result3.size());
    }
}
