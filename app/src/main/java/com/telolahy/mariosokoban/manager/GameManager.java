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
    private static final String RETRIES_PREFS_KEY = "level";

    public static GameManager getInstance() {
        return INSTANCE;
    }

    public int maxLevelReached() {

        SharedPreferences prefs = preferences();
        int level = prefs.getInt(LEVEL_PREFS_KEY, 1);
        return level;
    }

    public void completedLevel(int level) {

        int nextLevel = Math.min(level + 1, Constants.TOTAL_LEVELS_COUNT);
        int maxLevelReached = maxLevelReached();
        if (nextLevel > maxLevelReached) {

            SharedPreferences prefs = preferences();
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt(LEVEL_PREFS_KEY, level);
            edit.commit();
        }
    }

    public int displayedLevelsCount() {

        return Constants.TOTAL_LEVELS_COUNT;
    }

    public boolean isOnLastLevel() {
        return maxLevelReached() >= Constants.TOTAL_LEVELS_COUNT;
    }

    public int retriesForLevel(int level) {
        return 1;
    }

    public void incrementRetriesForLevel(int level) {

    }

    private SharedPreferences preferences() {

        return ResourcesManager.getInstance().activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
