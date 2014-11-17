package com.telolahy.mariosokoban.core;

import android.content.Context;
import android.util.Log;

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
    public static final char OK_BOX = '*';
    public static final char GOAL = '.';
    public static final char PLAYER = '@';
    public static final char PLAYER_ON_GOAL = '+';

    public int sizeX;
    public int sizeY;
    private char mElement[][];

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
        }

        sizeX = columnsCount;
        sizeY = linesCount;
        mElement = new char[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {

            String line = lines.get(linesCount - 1 - y);
            int lineLengh = line.length();
            for (int x = 0; x < sizeX; x++) {

                if (x < lineLengh) {
                    mElement[x][y] = line.charAt(x);
                } else {
                    mElement[x][y] = EMPTY;
                }
            }
        }

        Log.i("", "loaded level: " + sizeX + "x" + sizeY);
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Log.i("", y + ":" + x + ": '" + mElement[x][y] + "'");
            }
        }
    }

    public int getElement(int x, int y) {
        return mElement[x][y];
    }

    public void setElement(int x, int y, char e) {
        mElement[x][y] = e;
    }
}
