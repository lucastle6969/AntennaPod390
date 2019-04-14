package de.danoeh.antennapod.fragment;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.test.ApplicationTestCase;
import android.test.mock.MockContext;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class PodcastOfTheDayTest extends ApplicationTestCase<Application> {

    public PodcastOfTheDayTest() {
        super(Application.class);
    }

    private PodcastOfTheDayFragment potdFragment;
    private PodcastOfTheDayFragment potdFragmentMock;
    private String apiJSON;
    private FragmentManager fm;


    @Before
    public void setUp() {
        // This mock is for mocking out the API call, avoiding overage fees.
        Context context = new MockContext();
        potdFragmentMock = mock(PodcastOfTheDayFragment.class);
        potdFragment = new PodcastOfTheDayFragment();
        potdFragment.butGenerateNew = mock(Button.class);
        potdFragment.butGoToPodcast = mock(Button.class);
        potdFragment.potdTitle = mock(TextView.class);
        potdFragment.potdAuthor = mock(TextView.class);
        potdFragment.potdDescription = mock(TextView.class);
        potdFragment.potdImage = mock(ImageView.class);

        String potdTitle = "TED Talks Daily";
        String potdImage = "https://d3sv2eduhewoas.cloudfront.net/channel/image/6988ba944a3c4be6945bfc1ecddbec18.png";
        String potdFeed = "https://www.listennotes.com/c/r/9d6939745ed34e3aab0eb78a408ab40d";
        String potdDescription = "Want TED Talks on the go? Every weekday, this feed brings you our latest talks in audio" +
                " format. Hear thought-provoking ideas on every subject imaginable -- from Artificial Intelligence to Zoology, " +
                "and everything in between -- given by the world's leading thinkers and doers. This collection of talks, given at" +
                " TED and TEDx conferences around the globe, is also available in video format.";
        int potdEpisodes = 753;
        String potdAuthor = "TED";
        PodcastOfTheDayFragment.PodcastOfTheDay potd = new PodcastOfTheDayFragment.PodcastOfTheDay(potdTitle, potdImage, potdFeed, potdDescription, potdEpisodes, potdAuthor);
        when(potdFragmentMock.getDailyPodcast("API_RESPONSE")).thenReturn(potd);

        apiJSON = "{" +
                "  \"publisher\": \"Scientific American\"," +
                "  \"image\": \"https://d3sv2eduhewoas.cloudfront.net/channel/image/fa482fb9d9ef4398954de45b1ce12a33.jpg\"," +
                "  \"total_episodes\": 441," +
                "  \"title\": \"60-Second Science\"," +
                "  \"thumbnail\": \"https://d3sv2eduhewoas.cloudfront.net/channel/image/fa482fb9d9ef4398954de45b1ce12a33.jpg\"," +
                "  \"listennotes_url\": \"https://www.listennotes.com/c/d0244d95782a4b08999dee758f012a5a/\"," +
                "  \"rss\": \"https://www.listennotes.com/c/r/d0244d95782a4b08999dee758f012a5a\"," +
                "  \"email\": \"webmaster@sciam.com\"," +
                "  \"looking_for\": {" +
                "    \"cohosts\": false," +
                "    \"cross_promotion\": false," +
                "    \"guests\": false," +
                "    \"sponsors\": false" +
                "  }," +
                "  \"next_episode_pub_date\": 1550193840000," +
                "  \"country\": \"United States\"," +
                "  \"id\": \"d0244d95782a4b08999dee758f012a5a\"," +
                "  \"itunes_id\": 189330872," +
                "  \"description\": \"Leading science journalists provide a daily minute commentary on some of the most interesting developments in the world of science. For a full-length, weekly podcast you can subscribe to Science Talk: The Podcast of Scientific American . To view all of our archived podcasts please go to www.scientificamerican.com/podcast\"" +
                "}";

    }

    @Test
    public void testPopulate() {
        PodcastOfTheDayFragment.PodcastOfTheDay potd = potdFragmentMock.getDailyPodcast("API_RESPONSE");
        potdFragment.populate(potd, Boolean.TRUE);
        verify(potdFragment.potdTitle).setText("TED Talks Daily");
        verify(potdFragment.potdAuthor).setText("TED");
        verify(potdFragment.potdDescription).setText("Want TED Talks on the go? Every weekday, this feed brings you our latest talks in audio" +
                " format. Hear thought-provoking ideas on every subject imaginable -- from Artificial Intelligence to Zoology, " +
                "and everything in between -- given by the world's leading thinkers and doers. This collection of talks, given at" +
                " TED and TEDx conferences around the globe, is also available in video format.");
        verify(potdFragment.butGoToPodcast).setOnClickListener(Mockito.any());

    }

    @Test
    public void testGetDailyPodcast() {
        PodcastOfTheDayFragment.PodcastOfTheDay potd = potdFragment.getDailyPodcast(apiJSON);
        assertEquals(potd.author, "Scientific American");
        assertEquals(potd.description, "Leading science journalists provide a daily minute commentary on some of the most " +
                "interesting developments in the world of science. For a full-length, weekly podcast you can subscribe to Science Talk:" +
                " The Podcast of Scientific American . To view all of our archived podcasts please go to www.scientificamerican.com/podcast");
        assertEquals(potd.feedUrl, "https://www.listennotes.com/c/r/d0244d95782a4b08999dee758f012a5a");
        assertEquals(potd.imageUrl, "https://d3sv2eduhewoas.cloudfront.net/channel/image/fa482fb9d9ef4398954de45b1ce12a33.jpg");
        assertEquals(potd.numOfEpisodes, 441);
        assertEquals(potd.title, "60-Second Science");

    }
}