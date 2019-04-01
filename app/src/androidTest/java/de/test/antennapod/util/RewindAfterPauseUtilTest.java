package de.test.antennapod.util;

import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;

import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.RewindAfterPauseUtils;

/**
 * Tests for {@link RewindAfterPauseUtils}.
 */
public class RewindAfterPauseUtilTest extends TestCase {

    public void testCalculatePositionWithRewindNoRewind() {
        final int ORIGINAL_POSITION = 10000;
        long lastPlayed = System.currentTimeMillis();
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);

        assertEquals(ORIGINAL_POSITION, position);
    }

    public void testCalculatePositionWithRewindSmallRewind() {
        final int ORIGINAL_POSITION = 10000;
        long lastPlayed = System.currentTimeMillis() - RewindAfterPauseUtils.ELAPSED_TIME_FOR_SHORT_REWIND - 1000;
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);

        assertEquals(ORIGINAL_POSITION - RewindAfterPauseUtils.SHORT_REWIND, position);
    }

    public void testCalculatePositionWithRewindMediumRewind() {
        final int ORIGINAL_POSITION = 10000;
        long lastPlayed = System.currentTimeMillis() - RewindAfterPauseUtils.ELAPSED_TIME_FOR_MEDIUM_REWIND - 1000;
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);

        assertEquals(ORIGINAL_POSITION - RewindAfterPauseUtils.MEDIUM_REWIND, position);
    }

    public void testCalculatePositionWithRewindLongRewind() {
        final int ORIGINAL_POSITION = 30000;
        long lastPlayed = System.currentTimeMillis() - RewindAfterPauseUtils.ELAPSED_TIME_FOR_LONG_REWIND - 1000;
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);

        assertEquals(ORIGINAL_POSITION - RewindAfterPauseUtils.LONG_REWIND, position);
    }

    public void testCalculatePositionWithRewindNegativeNumber() {
        final int ORIGINAL_POSITION = 100;
        long lastPlayed = System.currentTimeMillis() - RewindAfterPauseUtils.ELAPSED_TIME_FOR_LONG_REWIND - 1000;
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);

        assertEquals(0, position);
    }

    public void testCalculateFixedPosition() {
        final int ORIGINAL_POSITION = 25000;
        final int REWIND_SECONDS = 10;
        final long REWIND_MILLISECONDS = TimeUnit.SECONDS.toMillis(REWIND_SECONDS);
        int position = RewindAfterPauseUtils.calculatePositionWithFixedRewind(ORIGINAL_POSITION, REWIND_SECONDS);
        assertEquals(ORIGINAL_POSITION - REWIND_MILLISECONDS, position);
    }

    public void testCalculateFixedPositionNegativeResult(){
        final int ORIGINAL_POSITION = 10000;
        final int REWIND_SECONDS = 20;
        final long REWIND_MILLISECONDS = TimeUnit.SECONDS.toMillis(REWIND_SECONDS);
        int position = RewindAfterPauseUtils.calculatePositionWithFixedRewind(ORIGINAL_POSITION, REWIND_SECONDS);
        assertEquals(0, position);
    }

    public void testCalculateFixedPositionDisabled() {
        final int ORIGINAL_POSITION = 25000;
        final int REWIND_SECONDS = UserPreferences.AUTOMATIC_REWIND_DISABLED;
        int position = RewindAfterPauseUtils.calculatePositionWithFixedRewind(ORIGINAL_POSITION, REWIND_SECONDS);
        assertEquals(ORIGINAL_POSITION, position);
    }

    public void testCalculateFixedPositionVariable() {
        final int ORIGINAL_POSITION = 25000;
        final int REWIND_SECONDS = UserPreferences.AUTOMATIC_REWIND_VARIABLE;
        int position = RewindAfterPauseUtils.calculatePositionWithFixedRewind(ORIGINAL_POSITION, REWIND_SECONDS);
        assertEquals(ORIGINAL_POSITION, position);
    }

    public void testCalculateFixedPositionUnexpectedInput() {
        final int ORIGINAL_POSITION = -15000;
        final int INVALID_REWIND_SECONDS = -7;
        boolean exceptionCaught = false;
        try {
            RewindAfterPauseUtils.calculatePositionWithFixedRewind(ORIGINAL_POSITION, INVALID_REWIND_SECONDS);
        } catch (RuntimeException e){
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }
}
