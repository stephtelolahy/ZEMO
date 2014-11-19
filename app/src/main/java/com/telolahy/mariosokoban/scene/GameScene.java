package com.telolahy.mariosokoban.scene;

import android.graphics.Point;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.core.BoxSprite;
import com.telolahy.mariosokoban.core.Game;
import com.telolahy.mariosokoban.core.MarioSprite;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceGestureDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

import java.util.ArrayList;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class GameScene extends BaseScene {

    private static final int X0 = 100;
    private static final int Y0 = 40;
    private static final int BLOC_SIZE = 58;
    private static final int STEP_DURATION_MILLIS = 800; // time to move one block

    private static final int NONE = -1;
    private static final int DOWN = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int UP = 3;

    private Game mGame;
    private MarioSprite mMario;
    private ArrayList<BoxSprite> mBoxes;

    private SurfaceGestureDetector gestureDetector;
    private SurfaceScrollDetector scrollDetector;

    public GameScene() {
        super();
        setupGestureDetector();
    }

    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

//        gestureDetector.onTouchEvent(pSceneTouchEvent);
        scrollDetector.onManagedTouchEvent(pSceneTouchEvent);
        return true;
    }

    @Override
    public void createScene() {

        createBackground();
        createHUD();
        loadLevel(1);
    }

    private void setupGestureDetector() {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                scrollDetector = new SurfaceScrollDetector(25, new ScrollDetector.IScrollDetectorListener() {
                    @Override
                    public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

                    }

                    @Override
                    public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

                        if (Math.abs(pDistanceX) > Math.abs(pDistanceY)) {
                            if (pDistanceX > 0)
                                handleInput(new Point(1, 0));
                            else
                                handleInput(new Point(-1, 0));
                        } else {
                            if (pDistanceY > 0)
                                handleInput(new Point(0, -1));
                            else
                                handleInput(new Point(0, 1));
                        }
                    }

                    @Override
                    public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

                    }
                });

                gestureDetector = new SurfaceGestureDetector(mActivity, 60) {
                    @Override
                    protected boolean onSingleTap() {
                        return false;
                    }

                    @Override
                    protected boolean onDoubleTap() {
                        return false;
                    }

                    @Override
                    protected boolean onSwipeUp() {
                        handleInput(new Point(0, 1));
                        return true;
                    }

                    @Override
                    protected boolean onSwipeDown() {
                        handleInput(new Point(0, -1));
                        return true;
                    }

                    @Override
                    protected boolean onSwipeLeft() {
                        handleInput(new Point(-1, 0));
                        return true;
                    }

                    @Override
                    protected boolean onSwipeRight() {
                        handleInput(new Point(1, 0));
                        return true;
                    }
                };
            }
        });
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

        Sprite repeatingBackground = new Sprite(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT / 2, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
        attachChild(repeatingBackground);

//        RepeatingSpriteBackground grassBackground = new RepeatingSpriteBackground(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
//        setBackground(grassBackground);
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

                switch (mGame.getElement(new Point(x, y))) {

                    case Game.WALL:
                        Sprite wall = new Sprite(posX, posY, mResourcesManager.gameWallTextureRegion, mVertexBufferObjectManager);
                        attachChild(wall);
                        break;

                    case Game.GOAL:
                        Sprite goal = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
                        attachChild(goal);
                        break;

                    case Game.BOX:
                        BoxSprite box = new BoxSprite(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager, x, y, false);
                        mBoxes.add(box);
                        break;

                    case Game.BOX_OK:
                        BoxSprite boxOk = new BoxSprite(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager, x, y, true);
                        boxOk.setCurrentTileIndex(1);
                        mBoxes.add(boxOk);
                        break;

                    case Game.PLAYER:
                        MarioSprite player = new MarioSprite(posX, posY, mResourcesManager.gamePlayerTextureRegion, mVertexBufferObjectManager, x, y);
                        mMario = player;
                        break;

                    case Game.PLAYER_ON_GOAL:
                        break;

                    default:
                        break;
                }
            }
        }

        for (BoxSprite box : mBoxes) {
            attachChild(box);
        }

        attachChild(mMario);

        mCamera.setChaseEntity(mMario);
    }

    private void handleInput(Point direction) {

        if (mMario.active) {
            return; // mario is busy
        }

        Point destination = new Point(mMario.position.x + direction.x, mMario.position.y + direction.y);

        if (destination.x < 0 || destination.x > mGame.getSizeX() - 1 || destination.y < 0 || destination.y > mGame.getSizeY() - 1) {
            return; // reached limit of he world
        }

        if (mGame.getElement(destination) == Game.WALL) {
            return; // blocked by a wall
        }

        if (mGame.getElement(destination) == Game.BOX
                || mGame.getElement(destination) == Game.BOX_OK) {

            Point behindDestination = new Point(destination.x + direction.x, destination.y + direction.y);

            if (behindDestination.x < 0 || behindDestination.x > mGame.getSizeX() - 1 || behindDestination.y < 0 || behindDestination.y > mGame.getSizeY() - 1) {
                return; // reached limit of he world
            }

            if (mGame.getElement(behindDestination) == Game.WALL
                    || mGame.getElement(behindDestination) == Game.BOX
                    || mGame.getElement(behindDestination) == Game.BOX_OK) {
                return; // blocked by a wall, box
            }

            moveBox(destination, behindDestination);
        }

        moveMario(destination);
    }

    private void moveMario(Point destination) {

        Point source = mMario.position;

        final int direction = getDirection(source, destination);

        if (mGame.getElement(source) == Game.PLAYER_ON_GOAL)
            mGame.setElement(source, Game.GOAL);
        else
            mGame.setElement(source, Game.EMPTY);

        if (mGame.getElement(destination) == Game.GOAL)
            mGame.setElement(destination, Game.PLAYER_ON_GOAL);
        else
            mGame.setElement(destination, Game.EMPTY);

        mMario.position = destination;

        float x1 = mMario.getX();
        float y1 = mMario.getY();
        float x2 = X0 + destination.x * BLOC_SIZE + BLOC_SIZE / 2;
        float y2 = Y0 + destination.y * BLOC_SIZE + BLOC_SIZE / 2;
        final Path marioPath = new Path(2).to(x1, y1).to(x2, y2);
        float pathAnimationDuration = (float) STEP_DURATION_MILLIS / 1000;
        mMario.registerEntityModifier(new PathModifier(pathAnimationDuration, marioPath, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

                mMario.active = true;
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

                mMario.active = false;
                mMario.stopAnimation();
                mMario.setCurrentTileIndex(direction * 4);
            }
        }));
    }

    private void moveBox(Point source, Point destination) {

        BoxSprite box = getBoxAt(source);

        if (mGame.getElement(source) == Game.BOX_OK) {
            mGame.setElement(source, Game.GOAL);
            box.setCurrentTileIndex(0);
        } else {
            mGame.setElement(source, Game.EMPTY);
        }

        if (mGame.getElement(destination) == Game.GOAL) {
            mGame.setElement(destination, Game.BOX_OK);
            box.setCurrentTileIndex(1);
        } else {
            mGame.setElement(destination, Game.BOX);
        }

        box.position = destination;

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

    private BoxSprite getBoxAt(Point position) {

        for (BoxSprite box : mBoxes) {
            if (box.position.x == position.x && box.position.y == position.y)
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
