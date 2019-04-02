package de.danoeh.antennapod.core.util;

import java.util.concurrent.TimeUnit;

import de.danoeh.antennapod.core.preferences.UserPreferences;

/**
 * This class calculates the proper rewind time after the pause and resume.
 * <p>
 * User might loose context if he/she pauses and resumes the media after longer time.
 * Media file should be "rewinded" x seconds after user resumes the playback.
 */
public class RewindAfterPauseUtils {
    private RewindAfterPauseUtils(){}

    public static final long ELAPSED_TIME_FOR_SHORT_REWIND = TimeUnit.MINUTES.toMillis(1);
    public static final long ELAPSED_TIME_FOR_MEDIUM_REWIND = TimeUnit.HOURS.toMillis(1);
    public static final long ELAPSED_TIME_FOR_LONG_REWIND = TimeUnit.DAYS.toMillis(1);

    public static final long SHORT_REWIND =  TimeUnit.SECONDS.toMillis(3);
    public static final long MEDIUM_REWIND = TimeUnit.SECONDS.toMillis(10);
    public static final long LONG_REWIND = TimeUnit.SECONDS.toMillis(20);

    /**
     * @param currentPosition  current position in a media file in ms
     * @param lastPlayedTime  timestamp when was media paused
     * @return  new rewinded position for playback in milliseconds
     */
    public static int calculatePositionWithRewind(int currentPosition, long lastPlayedTime) {
        if (currentPosition > 0 && lastPlayedTime > 0) {
            long elapsedTime = System.currentTimeMillis() - lastPlayedTime;
            long rewindTime = 0;

            if (elapsedTime > ELAPSED_TIME_FOR_LONG_REWIND) {
                rewindTime = LONG_REWIND;
            } else if (elapsedTime > ELAPSED_TIME_FOR_MEDIUM_REWIND) {
                rewindTime = MEDIUM_REWIND;
            } else if (elapsedTime > ELAPSED_TIME_FOR_SHORT_REWIND) {
                rewindTime = SHORT_REWIND;
            }

            int newPosition = currentPosition - (int) rewindTime;

            return newPosition > 0 ? newPosition : 0;
        }
        else {
            return currentPosition;
        }
    }

    /**
     * @param currentPosition  current position in a media file in ms
     * @param rewindSeconds  seconds by which to calculate the time to rewind to
     * @return  new rewinded position for playback in milliseconds, guaranteed to be non-negative.
     */
    public static int calculatePositionWithFixedRewind(int currentPosition, int rewindSeconds){
        if(rewindSeconds == UserPreferences.AUTOMATIC_REWIND_DISABLED || rewindSeconds == UserPreferences.AUTOMATIC_REWIND_VARIABLE){
            return currentPosition;
        } else if(rewindSeconds > 0) {
            long rewindTime = TimeUnit.SECONDS.toMillis(rewindSeconds);
            int newPosition = currentPosition - ((int) rewindTime);
            if(newPosition < 0){
                newPosition = 0;
            }
            return newPosition;
        } else {
            throw(new RuntimeException("Expected input to be positive or a predefined constant!"));
        }
    }
}
