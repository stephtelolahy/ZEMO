package com.telolahy.mariosokoban.manager;

/**
 * Created by stephanohuguestelolahy on 11/25/14.
 */
public class GameManager {

    public static final int LEVELS_COUNT = 30;
    private static final GameManager INSTANCE = new GameManager();

    public static GameManager getInstance() {
        return INSTANCE;
    }

    public int maxLevelReached() {

        return 20;
    }

    public boolean isOnLastLevel() {
        return maxLevelReached() >= LEVELS_COUNT;
    }

    public int retriesForLevel(int level) {
        return 1;
    }
}
