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
    public static final char INTERNAL_WALL = 'x';
    public static final char BOX = '$';
    public static final char BOX_ON_GOAL = '*';
    public static final char GOAL = '.';
    public static final char PLAYER = '@';
    public static final char PLAYER_ON_GOAL = '+';

    private int mSizeX;
    private int mSizeY;
    private char mElement[][];
    private char mWall[][];

    public int getSizeX() {
        return mSizeX;
    }

    public int getSizeY() {
        return mSizeY;
    }

    public char getElement(Point position) {

        if (isValidCoordinate(position)) {
            return mElement[position.x][position.y];
        } else {
            return EMPTY;
        }
    }

    public char getWall(Point position) {

        if (isValidCoordinate(position)) {
            return mWall[position.x][position.y];
        } else {
            return EMPTY;
        }
    }

    public void setElement(Point position, char element) {

        if (isValidCoordinate(position)) {
            mElement[position.x][position.y] = element;
        }
    }

    public boolean isValidCoordinate(Point point) {

        if (point.x < 0 || point.x > mSizeX - 1 || point.y < 0 || point.y > mSizeY - 1) {
            return false; // reached limit of he world
        } else {
            return true;
        }
    }

    public boolean loadLevel(String file, Context context) {

        ArrayList<String> lines = new ArrayList<String>();
        int maxX = 0;

        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                maxX = Math.max(maxX, line.length());
            }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        mSizeX = maxX;
        mSizeY = lines.size();
        mElement = new char[mSizeX][mSizeY];
        mWall = new char[mSizeX][mSizeY];

        for (int y = 0; y < mSizeY; y++) {

            String line = lines.get(mSizeY - 1 - y);
            int lineLength = line.length();
            for (int x = 0; x < mSizeX; x++) {

                if (x < lineLength) {
                    char el = line.charAt(x);
                    if (el == INTERNAL_WALL) {
                        mElement[x][y] = WALL;
                        mWall[x][y] = el;
                    } else {
                        mElement[x][y] = el;
                        mWall[x][y] = el == WALL ? el : EMPTY;
                    }
                } else {
                    mElement[x][y] = EMPTY;
                    mWall[x][y] = EMPTY;
                }
            }
        }

        return validateLevel();
    }

    public boolean isLevelCompleted() {
        for (int y = 0; y < mSizeY; y++) {
            for (int x = 0; x < mSizeX; x++) {
                if (mElement[x][y] == GOAL || mElement[x][y] == BOX) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateLevel() {

        int boxesCount = 0;
        int goalsCount = 0;
        int playerCount = 0;

        for (int y = 0; y < mSizeY; y++) {
            for (int x = 0; x < mSizeX; x++) {
                switch (mElement[x][y]) {
                    case PLAYER:
                        playerCount++;
                        break;
                    case BOX:
                        boxesCount++;
                        break;
                    case GOAL:
                        goalsCount++;
                        break;
                    case PLAYER_ON_GOAL:
                        playerCount++;
                        goalsCount++;
                        break;
                    case BOX_ON_GOAL:
                        boxesCount++;
                        goalsCount++;
                        break;
                    default:
                        break;
                }
            }
        }

        return (playerCount == 1 && boxesCount == goalsCount);
    }

}
