package de.danoeh.antennapod.fragment;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.danoeh.antennapod.core.achievements.Achievement;
import de.danoeh.antennapod.core.achievements.AchievementManager;
import de.danoeh.antennapod.core.achievements.AchievementUnlocked;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DBReader.class, UserPreferences.class})
public class AchievementsTest extends ApplicationTestCase<Application> {

    public AchievementsTest() {
        super(Application.class);
    }

    private AchievementManager manager;
    private AchievementUnlocked animator;
    private ConcurrentHashMap<String, Achievement> achievements;
    private Context context;
    private List<String> names;
    private List<String> names_all;


    @Before
    public void setUp() {
        mockStatic(UserPreferences.class);
        mockStatic(DBReader.class);
        animator = mock(AchievementUnlocked.class);
        context = new MockContext();
        PowerMockito.when(UserPreferences.getAchievementsToggle()).thenReturn(true);

        Achievement achievement1 = new Achievement("Testing Achievement 1", 1, 1, "Testing Achievement 1 Description", 0);
        Achievement achievement2 = new Achievement("Testing Achievement 2", 1, 1, "Testing Achievement 2 Description", 0);
        Achievement achievement3 = new Achievement("Over Achiever", 1, 1, "All Achievements Description", 0);
        achievements = new ConcurrentHashMap<>();
        names = new ArrayList<>();
        names_all = new ArrayList<>();
        achievements.put(achievement1.getName(), achievement1);
        achievements.put(achievement2.getName(), achievement2);
        achievements.put(achievement3.getName(), achievement3);
        names.add(achievement1.getName());
        names_all.add(achievement2.getName());
        PowerMockito.when(DBReader.getAchievements()).thenReturn(achievements);

        manager = AchievementManager.getInstance(animator);
    }

    @Test
    public void testAchievementCompletion() {
        // Test the unlocking of a single achievement
        manager.increment(names, context);

        Achievement completed = manager.getAchievement("Testing Achievement 1");
        Achievement uncompleted = manager.getAchievement("Testing Achievement 2");

        assertEquals(completed.getGoal(), completed.getCounter());
        assertNotSame(uncompleted.getGoal(), uncompleted.getCounter());
        assertNotNull(completed.getDate());
        assertNull(uncompleted.getDate());

        verify(animator).show(Mockito.any());
        verifyStatic(Mockito.times(1));
        UserPreferences.getAchievementsToggle();
        verifyStatic(Mockito.times(1));
        DBReader.getAchievements();

        // Test the unlocking of the final ALL_COMPLETE achievement
        manager.increment(names_all, context);

        Achievement all_complete = manager.getAchievement("Over Achiever");

        assertEquals(all_complete.getGoal(), all_complete.getCounter());
        assertNotNull(all_complete.getDate());
    }
}