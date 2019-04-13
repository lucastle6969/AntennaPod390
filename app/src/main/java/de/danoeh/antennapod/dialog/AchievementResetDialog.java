package de.danoeh.antennapod.dialog;

import android.app.AlertDialog;
import android.content.Context;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.adapter.AchievementsAdapter;
import de.danoeh.antennapod.core.achievements.AchievementManager;
import de.danoeh.antennapod.fragment.AchievementsFragment;

public class AchievementResetDialog {
    public void showDialog(Context context, AchievementsFragment fragment, AchievementsAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
            .setMessage(R.string.achievement_reset_label)
            .setPositiveButton(R.string.confirm_label, (dialog, id) -> {
                AchievementManager.getInstance(null).resetAchievements();
                adapter.setAchievementList(AchievementsFragment.toList(AchievementManager.getInstance().getAchievements()));
                fragment.refresh();
                adapter.notifyDataSetChanged();
            })
            .setNegativeButton(R.string.cancel_label, (dialog, id) -> {
            })
            .show();
    }
}
