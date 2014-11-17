package com.telolahy.mariosokoban.scene;

import android.util.Log;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.core.GameMap;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;

import java.util.ArrayList;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class GameScene extends BaseScene {

    private static final int X0 = 200;
    private static final int Y0 = 40;
    private static final int BLOC_SIZE = 34;

    private static final int NONE = -1;
    private static final int DOWN = 0;
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int LEFT = 3;


    private RepeatingSpriteBackground mGrassBackground;
    private AnimatedSprite mPlayer;
    private ArrayList<Sprite> mBoxes;

    private GameMap mMap;

    @Override
    public void createScene() {

        createBackground();
        createAnalogOnScreenControl();
        loadLevel(1);
        createHUD();
    }

    private void createAnalogOnScreenControl() {

        final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0, 0, this.mCamera, mResourcesManager.gameOnScreenControlBaseTextureRegion, mResourcesManager.gameOnScreenControlKnobTextureRegion, 0.1f, 200, mVertexBufferObjectManager, new AnalogOnScreenControl.IAnalogOnScreenControlListener() {
            @Override
            public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

                moveMario(pValueX, pValueY);
            }

            @Override
            public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {

            }
        });

        analogOnScreenControl.getControlBase().setAlpha(0.5f);
        analogOnScreenControl.getControlBase().setOffsetCenter(0, 0);
        analogOnScreenControl.getControlKnob().setScale(1.25f);

        setChildScene(analogOnScreenControl);
    }


    @Override
    public void disposeScene() {

        this.detachSelf();
        this.dispose();
    }

    @Override
    public void onBackKeyPressed() {

        SceneManager.getInstance().loadMenuScene();
    }

    private void createBackground() {

        mGrassBackground = new RepeatingSpriteBackground(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
        setBackground(mGrassBackground);
    }

    private void createHUD() {

    }

    private void loadLevel(int level) {

        GameMap gameMap = new GameMap();
        gameMap.loadLevel("level/level" + level + ".txt", mResourcesManager.activity);

        for (int y = 0; y < gameMap.getSizeY(); y++) {
            for (int x = 0; x < gameMap.getSizeX(); x++) {

                int posX = X0 + x * BLOC_SIZE + BLOC_SIZE / 2;
                int posY = Y0 + y * BLOC_SIZE + BLOC_SIZE / 2;

                switch (gameMap.getElement(x, y)) {

                    case GameMap.WALL:

                        Sprite wall = new Sprite(posX, posY, mResourcesManager.gameWallTextureRegion, mVertexBufferObjectManager);
                        attachChild(wall);
                        break;

                    case GameMap.BOX:

                        Sprite box = new Sprite(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager);
                        attachChild(box);
                        break;

                    case GameMap.BOX_OK:

                        Sprite boxOk = new Sprite(posX, posY, mResourcesManager.gameBoxOKTextureRegion, mVertexBufferObjectManager);
                        attachChild(boxOk);
                        break;

                    case GameMap.GOAL:

                        Sprite goal = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
                        attachChild(goal);
                        break;

                    case GameMap.PLAYER:

                        mPlayer = new AnimatedSprite(posX, posY, mResourcesManager.gamePlayerTextureRegion, mVertexBufferObjectManager);
                        attachChild(mPlayer);
                        break;

                    case GameMap.PLAYER_ON_GOAL:
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private int getDirection(float dx, float dy) {

        float threshold = 0.5f;
        if (Math.abs(dx) < threshold && Math.abs(dy) < threshold) {
            return NONE;
        }

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0)
                return RIGHT;
            else
                return LEFT;
        } else {
            if (dy > 0)
                return UP;
            else
                return DOWN;
        }
    }

    private void moveMario(float dx, float dy) {

        int direction = getDirection(dx, dy);
        Log.i("", "dx:" + dx + " dy:" + dy + " dir: " + direction);

        switch (direction) {
            case UP:
                mPlayer.setPosition(mPlayer.getX(), mPlayer.getY() + BLOC_SIZE);
                mPlayer.setCurrentTileIndex(direction);
                break;

            case DOWN:
                mPlayer.setPosition(mPlayer.getX(), mPlayer.getY() - BLOC_SIZE);
                mPlayer.setCurrentTileIndex(direction);
                break;

            case LEFT:
                mPlayer.setPosition(mPlayer.getX() - BLOC_SIZE, mPlayer.getY());
                mPlayer.setCurrentTileIndex(direction);
                break;

            case RIGHT:
                mPlayer.setPosition(mPlayer.getX() + BLOC_SIZE, mPlayer.getY());
                mPlayer.setCurrentTileIndex(direction);
                break;

            default:
                break;
        }

        /*
        int x = pos_mario.x;
        int y = pos_mario.y;

        switch (dir) {

            case UP:
                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (y - 1 < 0 || map.getEl(x, y - 1) == Map.WALL)
                    break;
                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (map.getEl(x, y - 1) == Map.BOX || map.getEl(x, y - 1) == Map.OK_BOX) {
                    if (y - 2 >= 0 && (map.getEl(x, y - 2) == Map.EMPTY || map.getEl(x, y - 2) == Map.GOAL))
                        // Il y a une caisse ‡ dÈplacer
                        moveBox(x, y - 1, x, y - 2);
                    else
                        break; //sinon on arrÍte
                }
                pos_mario.y--; // On peut enfin faire monter mario (oufff !)
                break;

            case DOWN:
                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (y + 1 >= map.ym || map.getEl(x, y + 1) == Map.WALL)
                    break;
                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (map.getEl(x, y + 1) == Map.BOX || map.getEl(x, y + 1) == Map.OK_BOX) {
                    if (y + 2 < map.ym && (map.getEl(x, y + 2) == Map.EMPTY || map.getEl(x, y + 2) == Map.GOAL))
                        // Il y a une caisse ‡ dÈplacer
                        moveBox(x, y + 1, x, y + 2);
                    else
                        break; //sinon on arrÍte
                }
                pos_mario.y++; // On peut enfin faire descendre mario (oufff !)
                break;

            case LEFT:
                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (x - 1 < 0 || map.getEl(x - 1, y) == Map.WALL)
                    break;
                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (map.getEl(x - 1, y) == Map.BOX || map.getEl(x - 1, y) == Map.OK_BOX) {
                    if (x - 2 >= 0 && (map.getEl(x - 2, y) == Map.EMPTY || map.getEl(x - 2, y) == Map.GOAL))
                        // Il y a une caisse ‡ dÈplacer
                        moveBox(x - 1, y, x - 2, y);
                    else
                        break; //sinon on arrÍte
                }
                pos_mario.x--; // On peut enfin faire bouger mario ‡ gauche (oufff !)
                break;

            case RIGHT:
                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (x + 1 >= map.ym || map.getEl(x + 1, y) == Map.WALL)
                    break;
                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (map.getEl(x + 1, y) == Map.BOX || map.getEl(x + 1, y) == Map.OK_BOX) {
                    if (x + 2 < map.xm && (map.getEl(x + 2, y) == Map.EMPTY || map.getEl(x + 2, y) == Map.GOAL))
                        // Il y a une caisse ‡ dÈplacer
                        moveBox(x + 1, y, x + 2, y);
                    else
                        break; //sinon on arrÍte
                }
                pos_mario.x++; // On peut enfin faire descendre mario (oufff !)
                break;
        }
        */
    }

    private void moveBox(int sourceX, int sourceY, int targetX, int targetY) {

        if (mMap.getElement(sourceX, sourceY) == GameMap.BOX_OK)
            mMap.setElement(sourceX, sourceY, GameMap.GOAL);
        else
            mMap.setElement(sourceX, sourceY, GameMap.EMPTY);

        if (mMap.getElement(targetX, targetY) == GameMap.GOAL)
            mMap.setElement(targetX, targetY, GameMap.BOX_OK);
        else
            mMap.setElement(targetX, targetY, GameMap.BOX);
    }

}
