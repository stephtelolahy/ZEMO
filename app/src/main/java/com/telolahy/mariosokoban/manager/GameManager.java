package com.telolahy.mariosokoban.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.telolahy.mariosokoban.Constants;

/**
 * Created by stephanohuguestelolahy on 11/25/14.
 */
public class GameManager {

    private static final GameManager INSTANCE = new GameManager();

    private static final String PREFS_NAME = "preferences";
    private static final String LEVEL_PREFS_KEY = "level";

    public static GameManager getInstance() {
        return INSTANCE;
    }

    public int maxLevelReached() {

        return preferences().getInt(LEVEL_PREFS_KEY, 1);
    }

    public void completedLevel(int level) {

        int nextLevel = Math.min(level + 1, Constants.TOTAL_LEVELS_COUNT);
        if (nextLevel > maxLevelReached()) {
            SharedPreferences.Editor edit = preferences().edit();
            edit.putInt(LEVEL_PREFS_KEY, nextLevel);
            edit.commit();
        }
    }

    public int displayedLevelsCount() {

        int maxLevelReached = maxLevelReached();
        int levelsPerPage = Constants.LEVEL_ROWS_PER_SCREEN * Constants.LEVEL_COLUMNS_PER_SCREEN;
        int page = (maxLevelReached - 1) / levelsPerPage + 1;
        int displayedLevels = Math.min(page * levelsPerPage, Constants.TOTAL_LEVELS_COUNT);
        return displayedLevels;
    }

    public boolean isOnLastLevel() {
        return maxLevelReached() >= Constants.TOTAL_LEVELS_COUNT;
    }

    private SharedPreferences preferences() {

        return ResourcesManager.getInstance().activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
