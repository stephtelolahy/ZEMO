package com.telolahy.mariosokoban.object;

import android.content.Context;
import android.graphics.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by stephanohuguestelolahy on 11/17/14.
 */
public class GameMap {

    public static final char EMPTY = ' ';
    public static final char WALL = '#';
    public static final char BOX = '$';
    public static final char BOX_OK = '*';
    public static final char GOAL = '.';
    public static final char PLAYER = '@';
    public static final char PLAYER_ON_GOAL = '+';

    private int mSizeX;
    private int mSizeY;
    private char mElement[][];

    public int getSizeX() {
        return mSizeX;
    }

    public int getSizeY() {
        return mSizeY;
    }

    public int getElement(Point position) {
        return mElement[position.x][position.y];
    }

    public void setElement(Point position, char element) {
        mElement[position.x][position.y] = element;
    }

    public void loadLevel(String file, Context context) {

        ArrayList<String> lines = new ArrayList<String>();
        int linesCount = 0;
        int columnsCount = 0;

        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {

                lines.add(line);
                linesCount++;
                columnsCount = Math.max(columnsCount, line.length());
            }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot read level: "+ file);
        }

        mSizeX = columnsCount;
        mSizeY = linesCount;
        mElement = new char[columnsCount][linesCount];

        for (int y = 0; y < linesCount; y++) {

            String line = lines.get(linesCount - 1 - y);
            int lineLength = line.length();
            for (int x = 0; x < columnsCount; x++) {

                if (x < lineLength) {
                    mElement[x][y] = line.charAt(x);
                } else {
                    mElement[x][y] = EMPTY;
                }
            }
        }
    }

    public boolean isLevelCompleted() {
        int remainGoals = 0;
        for (int y = 0; y < mSizeY; y++) {
            for (int x = 0; x < mSizeX; x++) {

                if (mElement[x][y] == GOAL) {
                    remainGoals++;
                }
            }
        }
        return (remainGoals == 0);
    }

}
