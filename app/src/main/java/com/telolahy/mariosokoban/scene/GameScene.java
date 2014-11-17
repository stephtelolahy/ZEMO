package com.telolahy.mariosokoban.scene;

import android.graphics.Point;
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
    private AnimatedSprite mMario;
    private Point mMarioPosition;
    private ArrayList<Sprite> mBoxes;

    private GameMap mGameMap;

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

        mGameMap = new GameMap();
        mGameMap.loadLevel("level/level" + level + ".txt", mResourcesManager.activity);

        for (int y = 0; y < mGameMap.getSizeY(); y++) {
            for (int x = 0; x < mGameMap.getSizeX(); x++) {

                int posX = X0 + x * BLOC_SIZE + BLOC_SIZE / 2;
                int posY = Y0 + y * BLOC_SIZE + BLOC_SIZE / 2;

                switch (mGameMap.getElement(x, y)) {

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

                        mMario = new AnimatedSprite(posX, posY, mResourcesManager.gamePlayerTextureRegion, mVertexBufferObjectManager);
                        attachChild(mMario);
                        mMarioPosition = new Point(x, y);
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

        int x = mMarioPosition.x;
        int y = mMarioPosition.y;

        switch (direction) {
            case UP:

                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (y + 1 >= mGameMap.getSizeY() || mGameMap.getElement(x, y + 1) == GameMap.WALL) {
                    break;
                }

                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse)
                if (mGameMap.getElement(x, y + 1) == GameMap.BOX || mGameMap.getElement(x, y + 1) == GameMap.BOX_OK) {

                    if (y + 2 < mGameMap.getSizeY()
                            && (mGameMap.getElement(x, y + 2) == GameMap.EMPTY || mGameMap.getElement(x, y + 2) == GameMap.GOAL))
                        // Il y a une caisse ‡ dÈplacer
                        moveBox(x, y + 1, x, y + 2);
                    else
                        break; //sinon on arrÍte
                }

                mMarioPosition.y++; // On peut enfin faire monter mario (oufff !)
                mMario.setPosition(mMario.getX(), mMario.getY() + BLOC_SIZE);
                mMario.setCurrentTileIndex(direction);
                break;

            case DOWN:

                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (y - 1 < 0 || mGameMap.getElement(x, y - 1) == GameMap.WALL)
                    break;

                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (mGameMap.getElement(x, y - 1) == GameMap.BOX || mGameMap.getElement(x, y - 1) == GameMap.BOX_OK) {
                    if (y - 2 < 0 && (mGameMap.getElement(x, y - 2) == GameMap.EMPTY || mGameMap.getElement(x, y - 2) == GameMap.GOAL))
                        // Il y a une caisse ‡ dÈplacer
                        moveBox(x, y - 1, x, y - 2);
                    else
                        break; //sinon on arrÍte
                }
                mMarioPosition.y--; // On peut enfin faire descendre mario (oufff !)
                mMario.setPosition(mMario.getX(), mMario.getY() - BLOC_SIZE);
                mMario.setCurrentTileIndex(direction);
                break;

            case LEFT:
                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (x - 1 < 0 || mGameMap.getElement(x - 1, y) == GameMap.WALL)
                    break;
                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (mGameMap.getElement(x - 1, y) == GameMap.BOX || mGameMap.getElement(x - 1, y) == GameMap.BOX_OK) {
                    if (x - 2 >= 0 && (mGameMap.getElement(x - 2, y) == GameMap.EMPTY || mGameMap.getElement(x - 2, y) == GameMap.GOAL))
                        // Il y a une caisse ‡ dÈplacer
                        moveBox(x - 1, y, x - 2, y);
                    else
                        break; //sinon on arrÍte
                }
                mMarioPosition.x--; // On peut enfin faire bouger mario ‡ gauche (oufff !)
                mMario.setPosition(mMario.getX() - BLOC_SIZE, mMario.getY());
                mMario.setCurrentTileIndex(direction);
                break;

            case RIGHT:
                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (x + 1 >= mGameMap.getSizeX() || mGameMap.getElement(x + 1, y) == GameMap.WALL)
                    break;
                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (mGameMap.getElement(x + 1, y) == GameMap.BOX || mGameMap.getElement(x + 1, y) == GameMap.BOX_OK) {
                    if (x + 2 < mGameMap.getSizeX() && (mGameMap.getElement(x + 2, y) == GameMap.EMPTY || mGameMap.getElement(x + 2, y) == GameMap.GOAL))
                        // Il y a une caisse ‡ dÈplacer
                        moveBox(x + 1, y, x + 2, y);
                    else
                        break; //sinon on arrÍte
                }
                mMarioPosition.x++; // On peut enfin faire descendre mario (oufff !)
                mMario.setPosition(mMario.getX() + BLOC_SIZE, mMario.getY());
                mMario.setCurrentTileIndex(direction);
                break;

            default:
                break;
        }
    }

    private void moveBox(int sourceX, int sourceY, int targetX, int targetY) {

        if (mGameMap.getElement(sourceX, sourceY) == GameMap.BOX_OK)
            mGameMap.setElement(sourceX, sourceY, GameMap.GOAL);
        else
            mGameMap.setElement(sourceX, sourceY, GameMap.EMPTY);

        if (mGameMap.getElement(targetX, targetY) == GameMap.GOAL)
            mGameMap.setElement(targetX, targetY, GameMap.BOX_OK);
        else
            mGameMap.setElement(targetX, targetY, GameMap.BOX);
    }

}
