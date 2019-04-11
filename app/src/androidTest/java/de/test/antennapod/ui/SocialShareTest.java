package de.test.antennapod.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;

import com.facebook.FacebookActivity;
import com.robotium.solo.Solo;
import com.twitter.sdk.android.core.identity.OAuthActivity;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;

public class SocialShareTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private Context context;
    private UITestUtils uiTestUtils;
    private SharedPreferences prefs;

    public SocialShareTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.context = getInstrumentation().getTargetContext();
        uiTestUtils = new UITestUtils(context);
        uiTestUtils.setup();

        prefs = getInstrumentation().getTargetContext().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(MainActivity.PREF_IS_FIRST_LAUNCH, false).commit();

        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        uiTestUtils.tearDown();
        solo.finishOpenedActivities();

        prefs.edit().clear().commit();

        super.tearDown();
    }

    private void openNavDrawer() {
        solo.clickOnImageButton(0);
        getInstrumentation().waitForIdleSync();
    }

    private void startStream(){
        String query = "Hello Internet";

        openNavDrawer();
        solo.clickOnText("Add Podcast");
        solo.waitForDialogToOpen(5000);
        solo.waitForView(R.id.sliding_tabs);
        solo.enterText(0, query);
        solo.sendKey(Solo.ENTER);
        solo.waitForView(R.id.gridView);

        solo.clickOnText("Educational");
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Subscribe");
        solo.sleep(5000);
        solo.clickOnText("Open Podcast");
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Twelve Drummers Drumming");
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Stream");
        solo.sleep(6000);

}


    public void testFacebookShare(){
        startStream();
        solo.clickOnView(solo.getView( R.id.fb_share_button));
        solo.waitForDialogToOpen(10000);
        assertEquals(solo.getCurrentActivity().getClass(), FacebookActivity.class);
    }

    public void testTwitterShare(){
        startStream();
        solo.clickOnView(solo.getView( R.id.login_button));
        solo.waitForDialogToOpen(10000);
        assertEquals(solo.getCurrentActivity().getClass(), OAuthActivity.class);
    }
}