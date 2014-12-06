package com.telolahy.mariosokoban.scene;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.Log;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.GameManager;
import com.telolahy.mariosokoban.manager.ResourcesManager;
import com.telolahy.mariosokoban.manager.SceneManager;
import com.telolahy.mariosokoban.object.GameCharacter;
import com.telolahy.mariosokoban.object.GameMap;
import com.telolahy.mariosokoban.utils.LevelCompletedMenuScene;
import com.telolahy.mariosokoban.utils.LongScrollDetector;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.modifier.ease.IEaseFunction;

import java.util.ArrayList;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class GameScene extends BaseScene implements LongScrollDetector.IScrollDetectorListener, PinchZoomDetector.IPinchZoomDetectorListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MARGIN = 48;

    private static final int REPLAY_MENU_ITEM = 1;
    private static final int BACK_MENU_ITEM = 2;

    private static final int STEP_DURATION_MILLIS = 600; // time to move one block

    private static final int DIRECTION_NONE = -1;
    private static final int DIRECTION_DOWN = 0;
    private static final int DIRECTION_LEFT = 1;
    private static final int DIRECTION_RIGHT = 2;
    private static final int DIRECTION_UP = 3;

    // ===========================================================
    // Fields
    // ===========================================================

    private int mLevel;
    private int mRetries;

    private GameMap mGame;
    private GameCharacter mMario;
    private ArrayList<GameCharacter> mBoxes;

    private LongScrollDetector mLongScrollDetector;
    private PinchZoomDetector mPinchZoomDetector;
    private float mPinchZoomStartedCameraZoomFactor;

    private static int mX0;
    private static int mY0;
    private static int mBlocSize;

    private LevelCompletedMenuScene mLevelCompletedMenuScene;

    // ===========================================================
    // Constructors
    // ===========================================================

    public GameScene(int... params) {
        super(params);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

        this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

        if (this.mPinchZoomDetector.isZooming()) {
            mLongScrollDetector.setEnabled(false);
        } else {
            if (pSceneTouchEvent.isActionDown()) {
                mLongScrollDetector.setEnabled(true);
            }
            mLongScrollDetector.onTouchEvent(pSceneTouchEvent);
        }

        return super.onSceneTouchEvent(pSceneTouchEvent);
    }


    @Override
    protected void onCreateScene(int... params) {

        mLevel = params[0];

        mCamera.setZoomFactor(Constants.GAME_MAX_SCENE_SCALE);

        createBackground();
        createHUD();
        loadLevel(mLevel);
        createLevelCompletedChildScene();
        if (mLevel == 1) {
            mResourcesManager.engine.registerUpdateHandler(new TimerHandler(1.f, new ITimerCallback() {
                public void onTimePassed(final TimerHandler pTimerHandler) {
                    createLevel1CoachMarker();
                }
            }));
        }
        setupGestureDetector();
    }

    @Override
    protected void onDisposeScene() {

        if (mMario != null) {
            mMario.detachSelf();
        }

        if (mBoxes != null) {
            for (Sprite sprite : mBoxes)
                sprite.detachSelf();
        }

        if (mLevelCompletedMenuScene != null) {
            mLevelCompletedMenuScene.detachSelf();
        }

        clearChildScene();

        // Hide HUD
        if (mCamera.getHUD() != null) {
            mCamera.getHUD().setVisible(false);
        }

        // Disable camera chase entity
        mCamera.setChaseEntity(null);

        mCamera.setZoomFactor(1f);

        // Reset camera position
        mCamera.setCenter(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2);
    }

    @Override
    public void onBackKeyPressed() {

        exitGame(false);
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    @Override
    public void onScrollVector(LongScrollDetector pScollDetector, int pPointerID, Point vector) {

        if (!mMario.moving && canMoveMario(vector)) {
            moveMario(vector);
        }
    }

    @Override
    public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
        mPinchZoomStartedCameraZoomFactor = mCamera.getZoomFactor();
    }

    @Override
    public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
        scaleCamera(pZoomFactor);
    }

    @Override
    public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
        scaleCamera(pZoomFactor);
    }

    private void scaleCamera(float factor) {

        float scale = Math.min(Math.max(mPinchZoomStartedCameraZoomFactor * factor, Constants.GAME_MIN_SCENE_SCALE), Constants.GAME_MAX_SCENE_SCALE);
        mCamera.setZoomFactor(scale);
    }

    // ===========================================================
    // Methods from Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void setupGestureDetector() {

        mLongScrollDetector = new LongScrollDetector(this);
        mPinchZoomDetector = new PinchZoomDetector(this);
        setTouchAreaBindingOnActionDownEnabled(true);
    }

    private void createBackground() {

        Sprite repeatingBackground = new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
        attachChild(repeatingBackground);

//        RepeatingSpriteBackground grassBackground = new RepeatingSpriteBackground(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
//        setBackground(grassBackground);
    }

    private void createHUD() {

        HUD gameHUD = new HUD();
        Text levelText = new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT - MARGIN, mResourcesManager.gameTitleFont, "Level 0123456789", new TextOptions(HorizontalAlign.CENTER), mVertexBufferObjectManager);
        levelText.setText(mActivity.getResources().getString(R.string.level) + " " + mLevel);
        gameHUD.attachChild(levelText);
        mCamera.setHUD(gameHUD);

        MenuScene menuScene = new MenuScene(ResourcesManager.getInstance().camera);
        IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(BACK_MENU_ITEM, mResourcesManager.gameBackButtonTextureRegion, mVertexBufferObjectManager), 1.2f, 1);
        IMenuItem retryMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(REPLAY_MENU_ITEM, mResourcesManager.gameReplayTextureRegion, mVertexBufferObjectManager), 1.2f, 1);
        menuScene.addMenuItem(retryMenuItem);
        menuScene.addMenuItem(backMenuItem);

        menuScene.buildAnimations();
        menuScene.setBackgroundEnabled(false);
        retryMenuItem.setPosition(Constants.SCREEN_WIDTH - MARGIN, Constants.SCREEN_HEIGHT - MARGIN);
        backMenuItem.setPosition(MARGIN, Constants.SCREEN_HEIGHT - MARGIN);
        menuScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case BACK_MENU_ITEM:
                        exitGame(false);
                        break;
                    case REPLAY_MENU_ITEM:
                        reloadGame();
                        break;
                }

                return true;
            }
        });
        setChildScene(menuScene);
    }

    private void createLevelCompletedChildScene() {

        mLevelCompletedMenuScene = new LevelCompletedMenuScene(mCamera, new LevelCompletedMenuScene.LevelCompletedMenuSceneListener() {
            @Override
            public void levelCompletedMenuSceneNextButtonClicked() {
                GameManager.getInstance().setLevelCompleted(mLevel);
                exitGame(true);
            }

            @Override
            public void levelCompletedMenuSceneReplayButtonClicked() {
                reloadGame();
            }
        });
    }

    private void createLevel1CoachMarker() {

        final Sprite scrollCoachMarker = new Sprite(Constants.SCREEN_WIDTH / 5, Constants.SCREEN_HEIGHT / 2 - mResourcesManager.gameScrollCoachMarkerRegion.getHeight(), mResourcesManager.gameScrollCoachMarkerRegion, mVertexBufferObjectManager);
        attachChild(scrollCoachMarker);

        int x1 = mX0 + 1 * mBlocSize + mBlocSize / 2;
        int y1 = mY0 + 1 * mBlocSize + mBlocSize / 2 - (int) mResourcesManager.gameScrollCoachMarkerRegion.getHeight();
        float x2 = mX0 + (mGame.getSizeX() - 1) * mBlocSize + mBlocSize / 2;
        float y2 = y1;
        final Path path = new Path(2).to(x1, y1).to(x2, y2);
        scrollCoachMarker.registerEntityModifier(new PathModifier(2.f, path, null, new PathModifier.IPathModifierListener() {
            @Override
            public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {

            }

            @Override
            public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {

            }

            @Override
            public void onPathWaypointFinished(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {

            }

            @Override
            public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {

                scrollCoachMarker.detachSelf();
            }
        }));
    }

    private void loadLevel(int level) {

        String levelFile = "level/level" + level + ".txt";
        mGame = new GameMap();
        boolean levelSuccessfullyLoaded = mGame.loadLevel(levelFile, mResourcesManager.activity);

        if (!levelSuccessfullyLoaded) {
            displayErrorLoadingLevel(levelFile);
            return;
        }

        mBlocSize = (int) mResourcesManager.gameTargetTextureRegion.getWidth();
        int worldWidth = mBlocSize * mGame.getSizeX();
        int worldHeight = mBlocSize * mGame.getSizeY();

        if (worldWidth < Constants.SCREEN_WIDTH) {
            mX0 = (Constants.SCREEN_WIDTH - worldWidth) / 2;
        } else {
            worldWidth += mBlocSize;
            mX0 = mBlocSize / 2;
        }
        if (worldHeight < Constants.SCREEN_HEIGHT) {
            mY0 = (Constants.SCREEN_HEIGHT - worldHeight) / 2;
        } else {
            worldHeight += mBlocSize;
            mY0 = mBlocSize / 2;
        }

        int cameraMinX = 0;
        int cameraMinY = 0;
        int cameraMaxX = Math.max(mX0 + worldWidth, Constants.SCREEN_WIDTH);
        int cameraMaxY = Math.max(mY0 + worldHeight, Constants.SCREEN_HEIGHT);
        mCamera.setBounds(cameraMinX, cameraMinY, cameraMaxX, cameraMaxY);
        mCamera.setBoundsEnabled(true);

        mBoxes = new ArrayList<GameCharacter>();

        for (int y = 0; y < mGame.getSizeY(); y++) {
            for (int x = 0; x < mGame.getSizeX(); x++) {

                int posX = mX0 + x * mBlocSize + mBlocSize / 2;
                int posY = mY0 + y * mBlocSize + mBlocSize / 2;

                switch (mGame.getElement(new Point(x, y))) {

                    case GameMap.WALL:
                        ITextureRegion textureRegion = wallTextureForWallAtPosition(x, y);
                        Sprite wall = new Sprite(posX, posY, textureRegion, mVertexBufferObjectManager);
                        attachChild(wall);
                        break;

                    case GameMap.GOAL:
                        Sprite goal = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
                        attachChild(goal);
                        break;

                    case GameMap.BOX:
                        GameCharacter box = new GameCharacter(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager, x, y);
                        box.setCurrentTileIndex(0);
                        mBoxes.add(box);
                        break;

                    case GameMap.PLAYER:
                        GameCharacter player = new GameCharacter(posX, posY, mResourcesManager.gameMarioTextureRegion, mVertexBufferObjectManager, x, y);
                        mMario = player;
                        break;

                    case GameMap.BOX_ON_GOAL:
                        Sprite goal2 = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
                        attachChild(goal2);
                        GameCharacter box2 = new GameCharacter(posX, posY, mResourcesManager.gameBoxTextureRegion, mVertexBufferObjectManager, x, y);
                        box2.setCurrentTileIndex(1);
                        mBoxes.add(box2);
                        break;

                    case GameMap.PLAYER_ON_GOAL:
                        Sprite goal3 = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
                        attachChild(goal3);
                        GameCharacter player3 = new GameCharacter(posX, posY, mResourcesManager.gameMarioTextureRegion, mVertexBufferObjectManager, x, y);
                        mMario = player3;
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

    private ITextureRegion wallTextureForWallAtPosition(int x, int y) {

        Point cur = new Point(x, y);
        Point up = new Point(x, y + 1);
        Point down = new Point(x, y - 1);
        Point left = new Point(x - 1, y);
        Point right = new Point(x + 1, y);

        if (mGame.getWall(cur) == GameMap.INTERNAL_WALL) {
            return mResourcesManager.gameWallTextureRegion[6];
        }

        if (mGame.getWall(left) != GameMap.WALL
                && mGame.getWall(up) != GameMap.WALL
                && mGame.getWall(right) == GameMap.WALL
                && mGame.getWall(down) == GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[0];
        }

        if (mGame.getWall(left) == GameMap.WALL
                && mGame.getWall(up) != GameMap.WALL
                && mGame.getWall(right) != GameMap.WALL
                && mGame.getWall(down) == GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[1];
        }

        if (mGame.getWall(left) == GameMap.WALL
                && mGame.getWall(up) == GameMap.WALL
                && mGame.getWall(right) != GameMap.WALL
                && mGame.getWall(down) != GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[2];
        }

        if (mGame.getWall(left) != GameMap.WALL
                && mGame.getWall(up) == GameMap.WALL
                && mGame.getWall(right) == GameMap.WALL
                && mGame.getWall(down) != GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[3];
        }

        if (mGame.getWall(left) == GameMap.WALL
                && mGame.getWall(right) == GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[4];
        }

        if (mGame.getWall(up) == GameMap.WALL
                && mGame.getWall(down) == GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[5];
        }

        return mResourcesManager.gameWallTextureRegion[6];

    }

    private boolean canMoveMario(final Point direction) {

        Point destination = new Point(mMario.gamePosition.x + direction.x, mMario.gamePosition.y + direction.y);

        if (!mGame.isValidCoordinate(destination)) {
            return false; // reached limit of he world
        }

        if (mGame.getElement(destination) == GameMap.WALL) {
            return false; // blocked by a wall
        }

        if (mGame.getElement(destination) == GameMap.BOX
                || mGame.getElement(destination) == GameMap.BOX_ON_GOAL) {

            Point behindDestination = new Point(destination.x + direction.x, destination.y + direction.y);

            if (!mGame.isValidCoordinate(behindDestination)) {
                return false; // reached limit of he world
            }

            if (mGame.getElement(behindDestination) == GameMap.WALL
                    || mGame.getElement(behindDestination) == GameMap.BOX
                    || mGame.getElement(behindDestination) == GameMap.BOX_ON_GOAL) {
                return false; // blocked by a wall, box
            }
        }

        return true;
    }

    private void moveMario(final Point vector) {

        Point source = mMario.gamePosition;
        Point destination = new Point(mMario.gamePosition.x + vector.x, mMario.gamePosition.y + vector.y);

        if (mGame.getElement(destination) == GameMap.BOX || mGame.getElement(destination) == GameMap.BOX_ON_GOAL) {

            Point behindDestination = new Point(destination.x + vector.x, destination.y + vector.y);
            animateBox(destination, behindDestination);
        }

        animateMario(source, destination);
    }

    private void animateMario(final Point source, final Point destination) {

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
        float x2 = mX0 + destination.x * mBlocSize + mBlocSize / 2;
        float y2 = mY0 + destination.y * mBlocSize + mBlocSize / 2;
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

                Point vector = mLongScrollDetector.getVector();
                if (vector != null && canMoveMario(vector)) {
                    moveMario(vector);
                } else {
                    // finish animation
                    mMario.moving = false;
                    mMario.stopAnimation();
                    mMario.setCurrentTileIndex(direction * 4);
                }
            }
        }));
    }

    private void animateBox(final Point source, final Point destination) {

        final GameCharacter box = getBoxAt(source);

        if (mGame.getElement(source) == GameMap.BOX_ON_GOAL) {
            mGame.setElement(source, GameMap.GOAL);
        } else {
            mGame.setElement(source, GameMap.EMPTY);
        }

        if (mGame.getElement(destination) == GameMap.GOAL) {
            mGame.setElement(destination, GameMap.BOX_ON_GOAL);
        } else {
            mGame.setElement(destination, GameMap.BOX);
        }

        checkGameOver();

        box.gamePosition = destination;

        float x1 = box.getX();
        float y1 = box.getY();
        float x2 = mX0 + destination.x * mBlocSize + mBlocSize / 2;
        float y2 = mY0 + destination.y * mBlocSize + mBlocSize / 2;
        final Path boxPath = new Path(2).to(x1, y1).to(x2, y2);
        float pathAnimationDuration = (float) STEP_DURATION_MILLIS / 1000;
        final IEaseFunction easeFunction = EaseStrongIn.getInstance();
        box.registerEntityModifier(new PathModifier(pathAnimationDuration, boxPath, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

                box.moving = true;
            }

            @Override
            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {

                box.moving = false;
                if (mGame.getElement(destination) == GameMap.BOX_ON_GOAL) {
                    box.setCurrentTileIndex(1);
                } else {
                    box.setCurrentTileIndex(0);
                }
            }
        }, easeFunction));

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
            return DIRECTION_RIGHT;
        } else if (destination.x - source.x < 0) {
            return DIRECTION_LEFT;
        } else if (destination.y - source.y > 0) {
            return DIRECTION_UP;
        } else if (destination.y - source.y < 0) {
            return DIRECTION_DOWN;
        } else {
            return DIRECTION_NONE;
        }
    }

    private void checkGameOver() {

        if (mGame.isLevelCompleted()) {
            mLongScrollDetector.setEnabled(false);
            mResourcesManager.engine.registerUpdateHandler(new TimerHandler(1.f, new ITimerCallback() {
                public void onTimePassed(final TimerHandler pTimerHandler) {
                    showGameCompleted();
                }
            }));
        }
    }

    private void showGameCompleted() {

        clearChildScene();
        int starsCount = mRetries == 0 ? 3 : 2;
        mLevelCompletedMenuScene.display(starsCount, this, mCamera);
    }

    private void reloadGame() {

        Engine.EngineLock engineLock = mActivity.getEngine().getEngineLock();
        engineLock.lock();

        if (mMario != null) {
            mMario.detachSelf();
        }

        if (mBoxes != null) {
            for (Sprite sprite : mBoxes)
                sprite.detachSelf();
        }

        engineLock.unlock();

        loadLevel(mLevel);

        mRetries++;
    }

    private void exitGame(boolean completed) {
        SceneManager.getInstance().loadMenuScene(completed ? (mLevel + 1) : mLevel);
    }

    private void displayErrorLoadingLevel(final String levelFile) {

        mActivity.runOnUiThread(new Runnable() {
            public void run() {

                String title = mActivity.getResources().getString(R.string.error_loading_level);
                String message = mActivity.getResources().getString(R.string.cannot_load_level) + ": " + levelFile;
                String positiveText = mActivity.getResources().getString(R.string.close);
                AlertDialog.Builder ad = new AlertDialog.Builder(mActivity);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setIcon(android.R.drawable.ic_dialog_alert);
                ad.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        exitGame(false);
                    }
                });
                ad.show();
            }
        });
    }


}
