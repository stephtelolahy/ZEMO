package com.telolahy.mariosokoban.scene;

import android.graphics.Point;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.core.GameCharacter;
import com.telolahy.mariosokoban.core.GameDetector;
import com.telolahy.mariosokoban.core.GameMap;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import java.util.ArrayList;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class GameScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int X0 = 100;
    private static final int Y0 = 40;
    private static final int BLOC_SIZE = 58;
    private static final int STEP_DURATION_MILLIS = 800; // time to move one block

    private static final int NONE = -1;
    private static final int DOWN = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int UP = 3;

    private static final int BOX_STATE_NONE = 0;
    private static final int BOX_STATE_OK = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private GameMap mGame;
    private GameCharacter mMario;
    private ArrayList<GameCharacter> mBoxes;
    private GameDetector mDetector;

    // ===========================================================
    // Constructors
    // ===========================================================

    public GameScene() {
        super();
        setupGestureDetector();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

        mDetector.onManagedTouchEvent(pSceneTouchEvent);
        return true;
    }

    @Override
    public void createScene() {

        createBackground();
        createHUD();
        loadLevel(1);
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

    // ===========================================================
    // Methods
    // ===========================================================

    private void setupGestureDetector() {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mDetector = new GameDetector(new GameDetector.IScrollDetectorListener() {

                    @Override
                    public void onScroll(GameDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

                        if (Math.abs(pDistanceX) > Math.abs(pDistanceY)) {
                            if (pDistanceX > 0)
                                handleInput(new Point(1, 0));
                            else
                                handleInput(new Point(-1, 0));
                        } else {
                            if (pDistanceY > 0)
                                handleInput(new Point(0, 1));
                            else
                                handleInput(new Point(0, -1));
                        }
                    }

                    @Override
                    public void onScrollFinished(GameDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

                    }
                });
            }
        });
    }

    private void createBackground() {

        Sprite repeatingBackground = new Sprite(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT / 2, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
        attachChild(repeatingBackground);

//        RepeatingSpriteBackground grassBackground = new RepeatingSpriteBackground(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
//        setBackground(grassBackground);
    }

    private void createHUD() {

    }

    private void loadLevel(int level) {

        mGame = new GameMap();
        mGame.loadLevel("level/level" + level + ".txt", mResourcesManager.activity);

        mBoxes = new ArrayList<GameCharacter>();

        for (int y = 0; y < mGame.getSizeY(); y++) {
            for (int x = 0; x < mGame.getSizeX(); x++) {

                int posX = X0 + x * BLOC_SIZE + BLOC_SIZE / 2;
                int posY = Y0 + y * BLOC_SIZE + BLOC_SIZE / 2;

                switch (mGame.getElement(new Point(x, y))) {

                    case GameMap.WALL:
                        Sprite wall = new Sprite(posX, posY, mResourcesManager.gameWallTextureRegion, mVertexBufferObjectManager);
                        attachChild(wall);
                        break;

                    case GameMap.GOAL:
                        Sprite goal = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
                        attachChild(goal);
                        break;

                    case GameMap.BOX:
                        GameCharacter box = new GameCharacter(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager, x, y);
                        box.state = BOX_STATE_NONE;
                        mBoxes.add(box);
                        break;

                    case GameMap.BOX_OK:
                        GameCharacter boxOk = new GameCharacter(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager, x, y);
                        boxOk.state = BOX_STATE_OK;
                        boxOk.setCurrentTileIndex(1);
                        mBoxes.add(boxOk);
                        break;

                    case GameMap.PLAYER:
                        GameCharacter player = new GameCharacter(posX, posY, mResourcesManager.gamePlayerTextureRegion, mVertexBufferObjectManager, x, y);
                        mMario = player;
                        break;

                    case GameMap.PLAYER_ON_GOAL:
                        break;

                    default:
                        break;
                }
            }
        }

        for (GameCharacter box : mBoxes) {
            attachChild(box);
        }

        attachChild(mMario);

        mCamera.setChaseEntity(mMario);
    }

    private boolean isValidCoordinate(Point point) {

        if (point.x < 0 || point.x > mGame.getSizeX() - 1 || point.y < 0 || point.y > mGame.getSizeY() - 1) {
            return false; // reached limit of he world
        } else {
            return true;
        }
    }

    private boolean canMoveMario(Point direction) {

        Point destination = new Point(mMario.gamePosition.x + direction.x, mMario.gamePosition.y + direction.y);

        if (!isValidCoordinate(destination)) {
            return false; // reached limit of he world
        }

        if (mGame.getElement(destination) == GameMap.WALL) {
            return false; // blocked by a wall
        }

        if (mGame.getElement(destination) == GameMap.BOX
                || mGame.getElement(destination) == GameMap.BOX_OK) {

            Point behindDestination = new Point(destination.x + direction.x, destination.y + direction.y);

            if (!isValidCoordinate(behindDestination)) {
                return false; // reached limit of he world
            }

            if (mGame.getElement(behindDestination) == GameMap.WALL
                    || mGame.getElement(behindDestination) == GameMap.BOX
                    || mGame.getElement(behindDestination) == GameMap.BOX_OK) {
                return false; // blocked by a wall, box
            }
        }

        return true;
    }

    private void handleInput(Point vector) {

        if (mMario.moving) {
            return; // mario is busy
        }

        if (canMoveMario(vector)) {
            moveMario(vector);
        }
    }

    private void moveMario(final Point vector) {

        Point source = mMario.gamePosition;
        Point destination = new Point(mMario.gamePosition.x + vector.x, mMario.gamePosition.y + vector.y);

        if (mGame.getElement(destination) == GameMap.BOX || mGame.getElement(destination) == GameMap.BOX_OK) {

            Point behindDestination = new Point(destination.x + vector.x, destination.y + vector.y);
            animateBox(destination, behindDestination);
        }

        animateMario(source, destination);
    }

    private void animateMario(Point source, Point destination) {

        final int direction = getDirection(source, destination);

        if (mGame.getElement(source) == GameMap.PLAYER_ON_GOAL)
            mGame.setElement(source, GameMap.GOAL);
        else
            mGame.setElement(source, GameMap.EMPTY);

        if (mGame.getElement(destination) == GameMap.GOAL)
            mGame.setElement(destination, GameMap.PLAYER_ON_GOAL);
        else
            mGame.setElement(destination, GameMap.EMPTY);

        mMario.gamePosition = destination;

        float x1 = mMario.getX();
        float y1 = mMario.getY();
        float x2 = X0 + destination.x * BLOC_SIZE + BLOC_SIZE / 2;
        float y2 = Y0 + destination.y * BLOC_SIZE + BLOC_SIZE / 2;
        final Path marioPath = new Path(2).to(x1, y1).to(x2, y2);
        float pathAnimationDuration = (float) STEP_DURATION_MILLIS / 1000;
        mMario.registerEntityModifier(new PathModifier(pathAnimationDuration, marioPath, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

                mMario.moving = true;
                final long tileDuration = STEP_DURATION_MILLIS / 4;
                mMario.animate(new long[]{tileDuration, tileDuration, tileDuration, tileDuration}, direction * 4, direction * 4 + 3, true);
            }

            @Override
            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {

                mMario.moving = false;
                mMario.stopAnimation();
                mMario.setCurrentTileIndex(direction * 4);
            }
        }));
    }

    private void animateBox(Point source, Point destination) {

        GameCharacter box = getBoxAt(source);

        if (mGame.getElement(source) == GameMap.BOX_OK) {
            mGame.setElement(source, GameMap.GOAL);
            box.setCurrentTileIndex(0);
        } else {
            mGame.setElement(source, GameMap.EMPTY);
        }

        if (mGame.getElement(destination) == GameMap.GOAL) {
            mGame.setElement(destination, GameMap.BOX_OK);
            box.setCurrentTileIndex(1);
        } else {
            mGame.setElement(destination, GameMap.BOX);
        }

        box.gamePosition = destination;

        float x1 = box.getX();
        float y1 = box.getY();
        float x2 = X0 + destination.x * BLOC_SIZE + BLOC_SIZE / 2;
        float y2 = Y0 + destination.y * BLOC_SIZE + BLOC_SIZE / 2;
        final Path boxPath = new Path(2).to(x1, y1).to(x2, y2);
        float pathAnimationDuration = (float) STEP_DURATION_MILLIS / 1000;
        box.registerEntityModifier(new PathModifier(pathAnimationDuration, boxPath, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

            }

            @Override
            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {

            }
        }));

    }

    private GameCharacter getBoxAt(Point position) {

        for (GameCharacter box : mBoxes) {
            if (box.gamePosition.x == position.x && box.gamePosition.y == position.y)
                return box;
        }
        return null;
    }

    private static int getDirection(Point source, Point destination) {

        if (destination.x - source.x > 0) {
            return RIGHT;
        } else if (destination.x - source.x < 0) {
            return LEFT;
        } else if (destination.y - source.y > 0) {
            return UP;
        } else if (destination.y - source.y < 0) {
            return DOWN;
        } else {
            return NONE;
        }
    }

}
