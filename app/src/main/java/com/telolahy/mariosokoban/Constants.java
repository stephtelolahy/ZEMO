package com.telolahy.mariosokoban;

import org.andengine.engine.options.ScreenOrientation;

/**
 * Created by stephanohuguestelolahy on 11/15/14.
 */
public class Constants {

    public static void initWithScreenSize(int width, int height) {

        SCREEN_HEIGHT = height * SCREEN_WIDTH / width;
    }

    public static final int SCREEN_WIDTH = 480;
    public static int SCREEN_HEIGHT;
    public static final ScreenOrientation SCREEN_ORIENTATION = ScreenOrientation.PORTRAIT_FIXED;

    public static final int TOTAL_LEVELS_COUNT = 64;
    public static final int LEVEL_COLUMNS_PER_SCREEN = 3;
    public static final int LEVEL_ROWS_PER_SCREEN = 2;

    public static final boolean DEBUG = true;

}
