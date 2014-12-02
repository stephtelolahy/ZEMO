package com.telolahy.mariosokoban.scene;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.GameManager;
import com.telolahy.mariosokoban.manager.ResourcesManager;
import com.telolahy.mariosokoban.manager.SceneManager;
import com.telolahy.mariosokoban.object.GameCharacter;
import com.telolahy.mariosokoban.object.GameMap;
import com.telolahy.mariosokoban.utils.LevelCompletedWindow;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.modifier.ease.IEaseFunction;

import java.util.ArrayList;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class GameScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

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

    private static int mX0;
    private static int mY0;
    private static int mBlocSize;

    private LevelCompletedWindow mLevelCompletedWindow;

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

        mLongScrollDetector.onTouchEvent(pSceneTouchEvent);
        return super.onSceneTouchEvent(pSceneTouchEvent);
    }


    @Override
    protected void onCreateScene(int... params) {

        mLevel = params[0];
        createBackground();
        createHUD();
        loadLevel(mLevel);
        setupGestureDetector();
        createLevelCompletedWindow();
        if (mLevel == 1) {
            mResourcesManager.engine.registerUpdateHandler(new TimerHandler(1.f, new ITimerCallback() {
                public void onTimePassed(final TimerHandler pTimerHandler) {
                    createLevel1CoachMarker();
                }
            }));
        }
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

        if (mLevelCompletedWindow != null) {
            mLevelCompletedWindow.detachSelf();
        }

        clearChildScene();

        // Hide HUD
        if (mCamera.getHUD() != null) {
            mCamera.getHUD().setVisible(false);
        }

        // Disable camera chase entity
        mCamera.setChaseEntity(null);

        // Reset camera position
        mCamera.setCenter(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2);
    }

    @Override
    public void onBackKeyPressed() {

        exitGame();
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

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

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mLongScrollDetector = new LongScrollDetector(new LongScrollDetector.IScrollDetectorListener() {

                    @Override
                    public void onScrollVector(LongScrollDetector pScollDetector, int pPointerID, Point vector) {

                        if (mMario.moving) {
                            return; // mario is busy
                        }

                        if (canMoveMario(vector)) {
                            moveMario(vector);
                        }
                    }
                });
            }
        });
    }

    private void createBackground() {

        Sprite repeatingBackground = new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
        attachChild(repeatingBackground);

//        RepeatingSpriteBackground grassBackground = new RepeatingSpriteBackground(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
//        setBackground(grassBackground);
    }

    private void createHUD() {

        final int TOP_MARGIN = 48;
        final int LEFT_MARGIN = 64;

        HUD gameHUD = new HUD();
        Text levelText = new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT - TOP_MARGIN, mResourcesManager.gameTitleFont, "Level 0123456789", new TextOptions(HorizontalAlign.CENTER), mVertexBufferObjectManager);
        levelText.setText(mActivity.getResources().getString(R.string.level) + " " + mLevel);
        gameHUD.attachChild(levelText);
        mCamera.setHUD(gameHUD);

        MenuScene menuScene = new MenuScene(ResourcesManager.getInstance().camera);
        IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(BACK_MENU_ITEM, mResourcesManager.gameBackTextureRegion, mVertexBufferObjectManager), 1.2f, 1);
        IMenuItem retryMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(REPLAY_MENU_ITEM, mResourcesManager.gameReplayTextureRegion, mVertexBufferObjectManager), 1.2f, 1);
        menuScene.addMenuItem(retryMenuItem);
        menuScene.addMenuItem(backMenuItem);

        menuScene.buildAnimations();
        menuScene.setBackgroundEnabled(false);
        retryMenuItem.setPosition(Constants.SCREEN_WIDTH - LEFT_MARGIN, Constants.SCREEN_HEIGHT - TOP_MARGIN);
        backMenuItem.setPosition(LEFT_MARGIN, Constants.SCREEN_HEIGHT - TOP_MARGIN);
        menuScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case BACK_MENU_ITEM:
                        exitGame();
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

    private void createLevelCompletedWindow() {

        mLevelCompletedWindow = new LevelCompletedWindow(new LevelCompletedWindow.LevelCompleteWindowListener() {
            @Override
            public void levelCompleteWindowNextButtonClicked() {
                GameManager.getInstance().completedLevel(mLevel);
                exitGame();
            }

            @Override
            public void levelCompleteWindowReplayButtonClicked() {
                reloadGame();
            }
        });
    }

    private void createLevel1CoachMarker() {

        final Sprite scrollCoachMarker = new Sprite(Constants.SCREEN_WIDTH / 5, Constants.SCREEN_HEIGHT / 2 - mResourcesManager.gameScrollCoachMarkerRegion.getHeight(), mResourcesManager.gameScrollCoachMarkerRegion, mVertexBufferObjectManager);
        attachChild(scrollCoachMarker);

        float x1 = scrollCoachMarker.getX();
        float x2 = Constants.SCREEN_WIDTH * 4 / 5;
        float y = scrollCoachMarker.getY();
        final Path path = new Path(2).to(x1, y).to(x2, y);
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

        if (mGame.getElement(new Point(x - 1, y)) != GameMap.WALL && mGame.getElement(new Point(x, y + 1)) != GameMap.WALL && mGame.getElement(new Point(x + 1, y)) == GameMap.WALL && mGame.getElement(new Point(x, y - 1)) == GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[0];
        } else if (mGame.getElement(new Point(x - 1, y)) == GameMap.WALL && mGame.getElement(new Point(x, y + 1)) != GameMap.WALL && mGame.getElement(new Point(x + 1, y)) != GameMap.WALL && mGame.getElement(new Point(x, y - 1)) == GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[2];
        } else if (mGame.getElement(new Point(x - 1, y)) != GameMap.WALL && mGame.getElement(new Point(x, y + 1)) == GameMap.WALL && mGame.getElement(new Point(x + 1, y)) == GameMap.WALL && mGame.getElement(new Point(x, y - 1)) != GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[5];
        } else if (mGame.getElement(new Point(x - 1, y)) == GameMap.WALL && mGame.getElement(new Point(x, y + 1)) == GameMap.WALL && mGame.getElement(new Point(x + 1, y)) != GameMap.WALL && mGame.getElement(new Point(x, y - 1)) != GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[6];
        } else if (mGame.getElement(new Point(x - 1, y)) == GameMap.WALL && mGame.getElement(new Point(x + 1, y)) == GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[1];
        } else if (mGame.getElement(new Point(x, y + 1)) == GameMap.WALL && mGame.getElement(new Point(x, y - 1)) == GameMap.WALL) {
            return mResourcesManager.gameWallTextureRegion[3];
        } else {
            return mResourcesManager.gameWallTextureRegion[4];
        }
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

        int starsCount = mRetries == 0 ? 3 : 2;
        mLevelCompletedWindow.display(starsCount, this, mCamera);
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

    private void exitGame() {
        SceneManager.getInstance().loadMenuScene();
    }

    private void displayErrorLoadingLevel(final String levelFile) {

        mActivity.runOnUiThread(new Runnable() {
            public void run() {

                String title = mActivity.getResources().getString(R.string.error_loading_level);
                String message = mActivity.getResources().getString(R.string.cannot_load_level) + levelFile;
                String positiveText = mActivity.getResources().getString(R.string.close);
                AlertDialog.Builder ad = new AlertDialog.Builder(mActivity);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setIcon(android.R.drawable.ic_dialog_alert);
                ad.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        exitGame();
                    }
                });
                ad.show();
            }
        });
    }


}
