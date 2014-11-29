package com.telolahy.mariosokoban.scene;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.GameManager;
import com.telolahy.mariosokoban.manager.SceneManager;
import com.telolahy.mariosokoban.object.GameCharacter;
import com.telolahy.mariosokoban.object.GameMap;
import com.telolahy.mariosokoban.utils.LevelCompletedWindow;
import com.telolahy.mariosokoban.utils.LongScrollDetector;

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

    private static final int WORLD_MARGIN_LEFT = 100;
    private static final int WORLD_MARGIN = 40;
    private static final int BLOC_SIZE = 58;
    private static final int STEP_DURATION_MILLIS = 600; // time to move one block

    private static final int NONE = -1;
    private static final int DOWN = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int UP = 3;

    // ===========================================================
    // Fields
    // ===========================================================

    private int mLevel;
    private boolean mLevelCompleted;

    private GameMap mGame;
    private GameCharacter mMario;
    private ArrayList<GameCharacter> mBoxes;
    private LongScrollDetector mLongScrollDetector;
    private static int mX0;
    private static int mY0;

    private LevelCompletedWindow mLevelCompletedWindow;

    public GameScene(int... params) {
        super(params);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

        if (mLevelCompleted) {
            return super.onSceneTouchEvent(pSceneTouchEvent);
        }

        mLongScrollDetector.onManagedTouchEvent(pSceneTouchEvent);
        return true;
    }


    @Override
    protected void onCreateScene(int... params) {

        mLevel = params[0];
        createBackground();
        createHUD();
        loadLevel(mLevel);
        setupGestureDetector();
        createLevelCompletedWindow();
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

        SceneManager.getInstance().loadMenuScene();
    }

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

    }

    private void createLevelCompletedWindow() {

        mLevelCompletedWindow = new LevelCompletedWindow(new LevelCompletedWindow.LevelCompleteWindowListener() {
            @Override
            public void levelCompleteWindowNextButtonClicked() {
                SceneManager.getInstance().loadMenuScene();
            }

            @Override
            public void levelCompleteWindowReplayButtonClicked() {
                reloadGame();
            }
        });
    }

    private void loadLevel(int level) {

        String levelFile = "level/level" + level + ".txt";
        mGame = new GameMap();
        boolean levelSuccessfullyLoaded = mGame.loadLevel(levelFile, mResourcesManager.activity);

        if (!levelSuccessfullyLoaded) {
            displayErrorLoadingLevel(levelFile);
            return;
        }

        int worldWidth = BLOC_SIZE * mGame.getSizeX() + WORLD_MARGIN;
        int worldHeight = BLOC_SIZE * mGame.getSizeY() + WORLD_MARGIN;
        mX0 = WORLD_MARGIN_LEFT;
        mY0 = WORLD_MARGIN;

        if (mX0 + worldWidth < Constants.SCREEN_WIDTH) {
            mX0 = ((Constants.SCREEN_WIDTH - mX0) - worldWidth) / 2 + mX0;
        }
        if (mY0 + worldHeight < Constants.SCREEN_HEIGHT) {
            mY0 = (Constants.SCREEN_HEIGHT - worldHeight) / 2;
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

                int posX = mX0 + x * BLOC_SIZE + BLOC_SIZE / 2;
                int posY = mY0 + y * BLOC_SIZE + BLOC_SIZE / 2;

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
                        GameCharacter box = new GameCharacter(posX, posY, mResourcesManager.gameCowTextureRegion, mVertexBufferObjectManager, x, y);
                        box.setCurrentTileIndex(6);
                        mBoxes.add(box);
                        break;
                    case GameMap.BOX_OK:
                        Sprite goalOK = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
                        attachChild(goalOK);
                        GameCharacter boxOk = new GameCharacter(posX, posY, mResourcesManager.gameCowTextureRegion, mVertexBufferObjectManager, x, y);
                        boxOk.setCurrentTileIndex(6);
                        mBoxes.add(boxOk);
                        break;

                    case GameMap.PLAYER:
                        GameCharacter player = new GameCharacter(posX, posY, mResourcesManager.gameMarioTextureRegion, mVertexBufferObjectManager, x, y);
                        mMario = player;
                        break;

                    case GameMap.PLAYER_ON_GOAL:
                        Sprite goalPlayer = new Sprite(posX, posY, mResourcesManager.gameTargetTextureRegion, mVertexBufferObjectManager);
                        attachChild(goalPlayer);
                        GameCharacter playerGoal = new GameCharacter(posX, posY, mResourcesManager.gameMarioTextureRegion, mVertexBufferObjectManager, x, y);
                        mMario = playerGoal;
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

    private boolean canMoveMario(final Point direction) {

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

    private void moveMario(final Point vector) {

        Point source = mMario.gamePosition;
        Point destination = new Point(mMario.gamePosition.x + vector.x, mMario.gamePosition.y + vector.y);

        if (mGame.getElement(destination) == GameMap.BOX || mGame.getElement(destination) == GameMap.BOX_OK) {

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
        float x2 = mX0 + destination.x * BLOC_SIZE + BLOC_SIZE / 2;
        float y2 = mY0 + destination.y * BLOC_SIZE + BLOC_SIZE / 2;
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

        if (mGame.getElement(source) == GameMap.BOX_OK) {
            mGame.setElement(source, GameMap.GOAL);
        } else {
            mGame.setElement(source, GameMap.EMPTY);
        }

        if (mGame.getElement(destination) == GameMap.GOAL) {
            mGame.setElement(destination, GameMap.BOX_OK);
        } else {
            mGame.setElement(destination, GameMap.BOX);
        }

        final int direction = getDirection(source, destination);
        box.gamePosition = destination;

        float x1 = box.getX();
        float y1 = box.getY();
        float x2 = mX0 + destination.x * BLOC_SIZE + BLOC_SIZE / 2;
        float y2 = mY0 + destination.y * BLOC_SIZE + BLOC_SIZE / 2;
        final Path boxPath = new Path(2).to(x1, y1).to(x2, y2);
        float pathAnimationDuration = (float) STEP_DURATION_MILLIS / 1000;
        box.registerEntityModifier(new PathModifier(pathAnimationDuration, boxPath, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

                box.moving = true;
                long tileDuration = STEP_DURATION_MILLIS / 4;
                box.animate(new long[]{tileDuration, tileDuration, tileDuration}, direction * 3, direction * 3 + 2, true);
            }

            @Override
            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

            }

            @Override
            public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {

                // finish animation
                box.moving = false;
                box.stopAnimation();
                box.setCurrentTileIndex(direction * 3);

                checkGameOver();
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

    private void checkGameOver() {

        mLevelCompleted = mGame.isLevelCompleted();
        if (mLevelCompleted) {
            showGameCompleted();
        }
    }

    private void showGameCompleted() {

        int starsCount = GameManager.getInstance().retriesForLevel(mLevel) == 0 ? 3 : 2;
        mLevelCompletedWindow.display(starsCount, this, mCamera);
    }

    private void reloadGame() {
        // TODO implement
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
                        SceneManager.getInstance().loadMenuScene();
                    }
                });
                ad.show();
            }
        });
    }


}
