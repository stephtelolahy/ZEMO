package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.region.ITextureRegion;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class MainMenuScene extends BaseScene implements ScrollDetector.IScrollDetectorListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int LEVEL_COLUMNS_PER_SCREEN = 4;
    private static final int LEVEL_ROWS_PER_SCREEN = 2;
    private static final int LEVEL_MARGIN_TOP = 200;
    private static final int LEVEL_MARGIN_BOTTOM = 170;
    private static final int LEVEL_MARGIN_LEFT = 200;
    private static final int LEVEL_MARGIN_RIGHT = 200;
    private static final int LEVELS_COUNT = 30;

    private static final int MENU_PLAY = 0;
    private static final int MENU_OPTIONS = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    // main menu scene
    private MenuScene mMenuChildScene;

    private float mMinX;
    private float mMaxX;
    private float mOffsetX;

    //This value will be loaded from whatever method used to store data.
    private int mMaxLevelReached;

    private Entity mLevelSelectionLayer;

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
    public void createScene() {

        mMaxLevelReached = 7;
        createBackground();
        createMenuChildScene();
        createLevelSelection();
        setupTouchGesture();
        startMusic();
    }

    @Override
    public void onBackKeyPressed() {

        System.exit(0);
    }

    @Override
    public void disposeScene() {

        if (mResourcesManager.menuMusic.isPlaying()) {
            mResourcesManager.menuMusic.pause();
        }

        this.detachSelf();
        this.dispose();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setupTouchGesture() {

        this.setOnSceneTouchListener(new SurfaceScrollDetector(this));
        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
    }

    private void startMusic() {

        if (!mResourcesManager.menuMusic.isPlaying()) {
            mResourcesManager.menuMusic.setLooping(true);
            mResourcesManager.menuMusic.setVolume(0.4f);
            mResourcesManager.menuMusic.play();
        }
    }

    private void createBackground() {

        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.menuParallaxLayerBackRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-2.f, new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT - mResourcesManager.menuParallaxLayerMidRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerMidRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-5.f, new Sprite(Constants.SCREEN_WIDTH / 2, mResourcesManager.menuParallaxLayerFrontRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerFrontRegion, mVertexBufferObjectManager)));
        setBackground(autoParallaxBackground);

        final int groundY = 28;
        final AnimatedSprite player = new AnimatedSprite(Constants.SCREEN_WIDTH / 2, mResourcesManager.menuPlayerTextureRegion.getHeight() / 2 + groundY, mResourcesManager.menuPlayerTextureRegion, mVertexBufferObjectManager);
        player.animate(new long[]{250, 250, 250, 250}, 0, 3, true);
        attachChild(player);
    }

    private void createMenuChildScene() {

        mMenuChildScene = new MenuScene(mCamera);

        TextMenuItem playTextMenuItem = new TextMenuItem(MENU_PLAY, mResourcesManager.font, mActivity.getResources().getString(R.string.play), mVertexBufferObjectManager);
        IMenuItem playMenuItem = new ScaleMenuItemDecorator(playTextMenuItem, 1.2f, 1);
        TextMenuItem optionsTextMenuItem = new TextMenuItem(MENU_OPTIONS, mResourcesManager.font, mActivity.getResources().getString(R.string.options), mVertexBufferObjectManager);
        IMenuItem helpMenuItem = new ScaleMenuItemDecorator(optionsTextMenuItem, 1.2f, 1);
        mMenuChildScene.addMenuItem(playMenuItem);
        mMenuChildScene.addMenuItem(helpMenuItem);

        mMenuChildScene.buildAnimations();
        mMenuChildScene.setBackgroundEnabled(false);

        mMenuChildScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case MENU_PLAY:
                        mLevelSelectionLayer.setVisible(true);
                        mMenuChildScene.setVisible(false);
                        return true;
                    case MENU_OPTIONS:
                        return true;
                    default:
                        return false;
                }
            }
        });

        setChildScene(mMenuChildScene);
    }

    private void createLevelSelection() {

        mLevelSelectionLayer = new Entity(0, 0);
        mLevelSelectionLayer.setVisible(false);
        attachChild(mLevelSelectionLayer);

        // calculate the amount of required columns for the level count
        int levelsPerPage = LEVEL_ROWS_PER_SCREEN * LEVEL_COLUMNS_PER_SCREEN;
        int totalPages = (LEVELS_COUNT / levelsPerPage) + (LEVELS_COUNT % levelsPerPage == 0 ? 0 : 1);

        // Calculate space between each level square
        int spaceBetweenRows = (Constants.SCREEN_HEIGHT - LEVEL_MARGIN_TOP - LEVEL_MARGIN_BOTTOM) / (LEVEL_ROWS_PER_SCREEN - 1);
        int spaceBetweenColumns = (Constants.SCREEN_WIDTH - LEVEL_MARGIN_LEFT - LEVEL_MARGIN_RIGHT) / (LEVEL_COLUMNS_PER_SCREEN - 1);

        //Current Level Counter
        int iLevel = 1;

        // Create the level selectors, one page at a time
        for (int page = 0; page < totalPages; page++) {

            int pageX = page * Constants.SCREEN_WIDTH;

            //Create the Level selectors, one row at a time.
            for (int y = 0; y < LEVEL_ROWS_PER_SCREEN && iLevel <= LEVELS_COUNT; y++) {

                int boxY = Constants.SCREEN_HEIGHT - LEVEL_MARGIN_TOP - spaceBetweenRows * y;

                for (int x = 0; x < LEVEL_COLUMNS_PER_SCREEN && iLevel <= LEVELS_COUNT; x++) {

                    //On Touch, save the clicked level in case it's a click and not a scroll.
                    final int levelToLoad = iLevel;
                    final boolean isUnlocked = levelToLoad <= mMaxLevelReached;

                    int boxX = pageX + LEVEL_MARGIN_LEFT + spaceBetweenColumns * x;

                    ITextureRegion textureRegion = isUnlocked ? mResourcesManager.menuLevelUnlockedRegion : mResourcesManager.menuLevelLockedRegion;

                    // Create the rectangle. If the level selected
                    // has not been unlocked yet, don't allow loading.
                    Sprite box = new Sprite(boxX, boxY, textureRegion, mVertexBufferObjectManager) {

                        @Override
                        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                            if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {

                                if (isUnlocked) {
                                    loadLevel(levelToLoad);
                                }
                                return true;
                            }

                            return false;
                        }
                    };

                    mLevelSelectionLayer.attachChild(box);

                    // display level number
                    if (isUnlocked) {
                        mLevelSelectionLayer.attachChild(new Text(boxX, boxY, mResourcesManager.menuLevelFont, String.valueOf(iLevel), mVertexBufferObjectManager));
                    }

                    this.registerTouchArea(box);

                    iLevel++;
                }
            }
        }

        //Set the max scroll possible, so it does not go over the boundaries.
        mMinX = 0;
        mMaxX = (totalPages - 1) * Constants.SCREEN_WIDTH;
    }

    //Here is where you call the level load.
    private void loadLevel(final int iLevel) {
        if (iLevel != -1) {

            SceneManager.getInstance().createGameScene();
        }
    }


    // ===========================================================
    // Implemented interfaces
    // ===========================================================


    @Override
    public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

        if (!mLevelSelectionLayer.isVisible()) {
            return;
        }

        float newX = mOffsetX - pDistanceX;
        if (newX < mMinX || newX > mMaxX) {
            return;
        }

        mLevelSelectionLayer.setPosition(-newX, 0);
        mOffsetX = newX;
    }

    @Override
    public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

    }

    @Override
    public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

    }
}
