package de.test.antennapod.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;

import com.robotium.solo.Solo;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.achievements.AchievementBuilder;
import de.danoeh.antennapod.core.storage.PodDBAdapter;

public class AchievementsTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private Context context;
    private UITestUtils uiTestUtils;
    private SharedPreferences prefs;

    public AchievementsTest() {
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
        adapter.close();
        AchievementBuilder.buildAchievements();

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

    private void openNavDrawer() {
        solo.clickOnImageButton(0);
        getInstrumentation().waitForIdleSync();
    }

    private String getActionbarTitle() {
        return ((MainActivity) solo.getCurrentActivity()).getSupportActionBar().getTitle().toString();
    }

    public void testAchievementsFragment() {
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.achievements));
        solo.waitForView(R.id.achievementList);
        assertEquals(solo.getString(R.string.achievements), getActionbarTitle());
    }

    public void testUnlockAchievement() {
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.achievements));
        solo.waitForView(R.id.achievementList);
        RecyclerView achievementList = (RecyclerView) solo.getView(R.id.achievementList);
        int size = achievementList.getAdapter().getItemCount();
        for(int i =0; i<size; i++){
            TextView name = solo.clickInRecyclerView(i).get(1);
            System.out.println(String.valueOf(name.getText()));
            if(name.getText().equals("? ? ?")){
                Log.d("ACHIEVEMENTS", "True");
            }

        }

    }

}
