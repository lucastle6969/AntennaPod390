package de.test.antennapod.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;

import com.robotium.solo.Solo;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.RadioStream;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;

import static java.lang.Thread.sleep;

public class RadioTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private Context context;
    private UITestUtils uiTestUtils;
    private SharedPreferences prefs;

    public RadioTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.context = getInstrumentation().getTargetContext();
        uiTestUtils = new UITestUtils(context);
        uiTestUtils.setup();

        // create new database
        PodDBAdapter.init(context);
        PodDBAdapter.deleteDatabase();
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        insertRecommendedRadio();
        adapter.close();

        // override first launch preference
        // do this BEFORE calling getActivity()!
        prefs = getInstrumentation().getTargetContext().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(MainActivity.PREF_IS_FIRST_LAUNCH, false).commit();

        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        uiTestUtils.tearDown();
        solo.finishOpenedActivities();

        PodDBAdapter.deleteDatabase();

        // reset preferences
        prefs.edit().clear().commit();

        super.tearDown();
    }

    private void insertRecommendedRadio() {
        RadioStream uncategorizedCategory = new RadioStream(-1, "BBC media", "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio2_mf_p");

        synchronized (this) {
            try {
                DBWriter.setRecommendedRadioStreamTest(uncategorizedCategory);
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void openNavDrawer() {
        solo.clickOnImageButton(0);
        getInstrumentation().waitForIdleSync();
    }

    private String getActionbarTitle() {
        return ((MainActivity) solo.getCurrentActivity()).getSupportActionBar().getTitle().toString();
    }

    private void goingToRadioPage() throws Exception{
        uiTestUtils.addLocalFeedData(true);
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.radio_stream_label));
        solo.waitForView(android.R.id.list);
    }

    private void addRecommendedRadioToList(){
        solo.clickOnText(solo.getString(R.string.recommended_radio_streams));

        ImageButton addRecommendedRadioButton = (ImageButton) solo.getView("imgAddToList");
        solo.clickOnView(addRecommendedRadioButton);
        solo.waitForDialogToOpen();
        solo.clickOnText(solo.getString(R.string.confirm_label));
    }

    public void testAddingRecommendedRadioToOwnList() throws Exception{
        goingToRadioPage();

        addRecommendedRadioToList();

        solo.clickOnText(solo.getString(R.string.my_radio_streams));
        assertFalse(solo.searchText(solo.getString(R.string.no_radio_stream_available), true));
    }

    public void testAddingNewRadioToList() throws Exception{
        String URL = "http://stream.redbullradio.com/main";

        goingToRadioPage();

        solo.clickOnView(solo.getView(R.id.addRadioStreamBtn));
        solo.waitForDialogToOpen();
        solo.enterText(1, URL);
        solo.clickOnText(solo.getString(R.string.confirm_label));

        solo.clickOnText(solo.getString(R.string.my_radio_streams));
        assertTrue(solo.searchText("Radio Stream 1", true));
    }

    public void testPlayingRadio() throws Exception{
        goingToRadioPage();

        solo.clickOnText(solo.getString(R.string.recommended_radio_streams));

        ImageButton playRadioButton = (ImageButton) solo.getView("imgRadioStreamPlay");
        solo.clickOnView(playRadioButton);
        solo.sleep(5000);

        solo.clickOnText(solo.getString(R.string.play_label));
        assertTrue(solo.searchText(solo.getString(R.string.pause_label), true));
    }

    public void testEditRadio() throws Exception{
        String title = "BBC media";
        String newTitle = "BBC radio";

        goingToRadioPage();
        addRecommendedRadioToList();

        solo.clickOnText(solo.getString(R.string.my_radio_streams));
        solo.clickLongOnText(title);
        solo.clickOnText(solo.getString(R.string.edit_radio_station_label));
        solo.clearEditText(0);
        solo.enterText(0, newTitle);
        solo.clickOnText(solo.getString(R.string.confirm_label));

        assertTrue(solo.searchText(newTitle, true));
    }

    public void testDeleteRadio() throws Exception{
        String title = "BBC media";

        goingToRadioPage();
        addRecommendedRadioToList();

        solo.clickOnText(solo.getString(R.string.my_radio_streams));
        solo.clickLongOnText(title);
        solo.clickOnText(solo.getString(R.string.delete_radio_station_label));
        solo.clickOnText(solo.getString(R.string.confirm_label));

        assertTrue(solo.searchText(solo.getString(R.string.no_radio_stream_available), true));
    }

}
