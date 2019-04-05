package de.danoeh.antennapod.core.achievements;

import android.graphics.drawable.Drawable;
import android.view.View;

import de.danoeh.antennapod.core.achievements.AchievementIconView.*;

/**
 * Class that holds the data to be displayed by
 * AchievementUnlocked object using the
 * {@link AchievementUnlocked#show(AchievementData...)} method
 */
public class AchievementData {
    private String title = "", subtitle;
    private Drawable icon;
    private int textColor = 0xff000000, backgroundColor = 0xffffffff, iconBackgroundColor = 0x0;
    private View.OnClickListener onClickListener;
    private AchievementIconViewStates state = null;

    public AchievementData setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public static AchievementData copyFrom(AchievementData data) {
        AchievementData result = new AchievementData();
        result.setTitle(data.getTitle());
        result.setSubtitle(data.getSubtitle());
        result.setIcon(data.getIcon());
        result.setState(data.getState());
        result.setBackgroundColor(data.getBackgroundColor());
        result.setIconBackgroundColor(data.getIconBackgroundColor());
        result.setTextColor(data.getTextColor());
        result.setPopUpOnClickListener(data.getPopUpOnClickListener());
        return result;
    }

    public View.OnClickListener getPopUpOnClickListener() {
        return onClickListener;
    }

    /**
     * Assign a per-data onclick listener to the popup
     *
     * @return same AchievementData object
     */
    public AchievementData setPopUpOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public AchievementData setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AchievementData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public AchievementIconView.AchievementIconViewStates getState() {
        return state;
    }

    /**
     * Indicate whether the popup icon should stay the same or
     * fade when showing different Achievement data. Default is
     * null which is the same as SAME_DRAWABLE.
     * When FADE_DRAWABLE is set, the icon will animate change to the
     * next data icon.
     *
     * @param state either of these two: FADE_DRAWABLE, SAME_DRAWABLE
     */
    public void setState(AchievementIconView.AchievementIconViewStates state) {
        this.state = state;
    }

    public Drawable getIcon() {
        return icon;
    }

    /**
     * Set popuup icon. Transparent one will be used if non is assigned
     *
     * @param icon icon drawable
     * @return same AchievementData object
     */
    public AchievementData setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set popup background color
     *
     * @param backgroundColor integer color of background
     * @return same AchievementData object
     */
    public AchievementData setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    int getIconBackgroundColor() {
        return iconBackgroundColor;
    }

    /**
     * Set the background of the popup's icon
     *
     * @param iconBackgroundColor integer color
     * @return same AchievementData object
     */
    public AchievementData setIconBackgroundColor(int iconBackgroundColor) {
        this.iconBackgroundColor = iconBackgroundColor;
        return this;
    }
}
