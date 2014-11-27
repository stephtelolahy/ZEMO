package com.telolahy.mariosokoban.manager;

/**
 * Created by stephanohuguestelolahy on 11/25/14.
 */
public class GameManager {

    private static final GameManager INSTANCE = new GameManager();

    public static GameManager getInstance() {
        return INSTANCE;
    }

    public int maxLevelReached() {

        return 20;
    }

}
