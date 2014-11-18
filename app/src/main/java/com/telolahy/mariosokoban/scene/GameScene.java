package com.telolahy.mariosokoban.scene;

import android.graphics.Point;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.core.BoxSprite;
import com.telolahy.mariosokoban.core.Game;
import com.telolahy.mariosokoban.core.MarioSprite;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
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
    private Game mGame;
    private MarioSprite mMario;
    private ArrayList<BoxSprite> mBoxes;

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

                handleInput(pValueX, pValueY);
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

        mGame = new Game();
        mGame.loadLevel("level/level" + level + ".txt", mResourcesManager.activity);

        mBoxes = new ArrayList<BoxSprite>();

        for (int y = 0; y < mGame.getSizeY(); y++) {
            for (int x = 0; x < mGame.getSizeX(); x++) {

                int posX = X0 + x * BLOC_SIZE + BLOC_SIZE / 2;
                int posY = Y0 + y * BLOC_SIZE + BLOC_SIZE / 2;

                switch (mGame.getElement(x, y)) {

                    case Game.WALL:
                        Sprite wall = new Sprite(posX, posY, mResourcesManager.gameWallTextureRegion, mVertexBufferObjectManager);
                        attachChild(wall);
                        break;

//                    case Game.GOAL:
//                        Sprite goal = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
//                        attachChild(goal);
//                        break;
//
//                    case Game.BOX:
//                        BoxSprite box = new BoxSprite(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager, x, y, false);
//                        mBoxes.add(box);
//                        attachChild(box);
//                        break;
//
//                    case Game.BOX_OK:
//                        BoxSprite boxOk = new BoxSprite(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager, x, y, true);
//                        boxOk.setCurrentTileIndex(1);
//                        mBoxes.add(boxOk);
//                        attachChild(boxOk);
//                        break;


                    case Game.PLAYER:
                        MarioSprite player = new MarioSprite(posX, posY, mResourcesManager.gamePlayerTextureRegion, mVertexBufferObjectManager, x, y);
                        mMario = player;
                        attachChild(player);
                        break;

                    case Game.PLAYER_ON_GOAL:
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private Point getDirection(float dx, float dy) {

        float THRESHOLD = 0.4f;
        if (Math.abs(dx) < THRESHOLD && Math.abs(dy) < THRESHOLD) {
            return new Point(0, 0);     // none
        }

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                return new Point(1, 0);     // Right
            } else {
                return new Point(-1, 0);    // Left
            }
        } else {
            if (dy > 0) {
                return new Point(0, 1);     //Up
            } else {
                return new Point(0, -1);    // Down
            }
        }
    }

    private void handleInput(float dx, float dy) {

        Point direction = getDirection(dx, dy);

        if (direction.x == 0 && direction.y == 0) {
            return; // invalid input
        }

        if (mMario.active) {
            return; // mario is busy
        }

        Point destination = new Point(mMario.position.x + direction.x, mMario.position.y + direction.y);

        if (destination.x < 0 || destination.x > mGame.getSizeX() - 1 || destination.y < 0 || destination.y > mGame.getSizeY() - 1) {
            return; // reached limit of he world
        }

        if (mGame.getElement(destination.x, destination.y) == Game.WALL) {
            return; // blocked by a wall
        }

        moveMario(destination);



        /*

        int x = mMarioPosition.x;
        int y = mMarioPosition.y;

        switch (direction) {
            case UP:

                // Si le joueur dÈpasse l'Ècran, ou
                // s'il y a un mur, on arrÍte
                if (y + 1 >= mGame.getSizeY() || mGame.getElement(x, y + 1) == Game.WALL) {
                    break;
                }

                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse)
                if (mGame.getElement(x, y + 1) == Game.BOX || mGame.getElement(x, y + 1) == Game.BOX_OK) {

                    if (y + 2 < mGame.getSizeY()
                            && (mGame.getElement(x, y + 2) == Game.EMPTY || mGame.getElement(x, y + 2) == Game.GOAL))
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
                if (y - 1 < 0 || mGame.getElement(x, y - 1) == Game.WALL)
                    break;

                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (mGame.getElement(x, y - 1) == Game.BOX || mGame.getElement(x, y - 1) == Game.BOX_OK) {
                    if (y - 2 < 0 && (mGame.getElement(x, y - 2) == Game.EMPTY || mGame.getElement(x, y - 2) == Game.GOAL))
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
                if (x - 1 < 0 || mGame.getElement(x - 1, y) == Game.WALL)
                    break;
                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (mGame.getElement(x - 1, y) == Game.BOX || mGame.getElement(x - 1, y) == Game.BOX_OK) {
                    if (x - 2 >= 0 && (mGame.getElement(x - 2, y) == Game.EMPTY || mGame.getElement(x - 2, y) == Game.GOAL))
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
                if (x + 1 >= mGame.getSizeX() || mGame.getElement(x + 1, y) == Game.WALL)
                    break;
                // Si on veut pousser une caisse, il faut vÈrifier qu'il n'y a pas
                // de mur derriËre (ou une autre caisse, ou la limite du monde)
                if (mGame.getElement(x + 1, y) == Game.BOX || mGame.getElement(x + 1, y) == Game.BOX_OK) {
                    if (x + 2 < mGame.getSizeX() && (mGame.getElement(x + 2, y) == Game.EMPTY || mGame.getElement(x + 2, y) == Game.GOAL))
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
        */
    }

    private void moveBox(int sourceX, int sourceY, int targetX, int targetY) {

        if (mGame.getElement(sourceX, sourceY) == Game.BOX_OK)
            mGame.setElement(sourceX, sourceY, Game.GOAL);
        else
            mGame.setElement(sourceX, sourceY, Game.EMPTY);

        if (mGame.getElement(targetX, targetY) == Game.GOAL)
            mGame.setElement(targetX, targetY, Game.BOX_OK);
        else
            mGame.setElement(targetX, targetY, Game.BOX);
    }

    private void moveBox(Point source, Point destination) {

    }

    private void moveMario(Point destination){

        mMario.position = destination;
        float x0 = mMario.getX();
        float y0 = mMario.getY();
        float x1 = X0 + destination.x * BLOC_SIZE + BLOC_SIZE / 2;
        float y1 = Y0 + destination.y * BLOC_SIZE + BLOC_SIZE / 2;
        final Path marioPath= new Path(2).to(x0, y0).to(x1, y1);
        mMario.registerEntityModifier(new PathModifier(0.5f, marioPath, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

                mMario.active = true;
            }

            @Override
            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {

                mMario.active = false;
            }
        }));
    }

}
