package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;

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
    private static final int LEVEL_PAGE_WIDTH = 600;
    private static final int LEVELS_COUNT = 30;

    private static final int MENU_PLAY = 0;
    private static final int MENU_OPTIONS = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    // main menu scene
    private HUD mHUD;
    private MenuScene mHomeMenuScene;
    private MenuScene mOptionsMenuScene;

    //This value will be loaded from whatever method used to store data.
    private int mMaxLevelReached;

    private Entity mLevelSelectorLayer;
    private boolean mLevelSelectorIsAnimating;
    private float mLevelSelectorMinX;
    private float mLevelSelectorMaxX;
    private MenuScene mLevelSelectorMenuScene;

    public MainMenuScene(String... params) {
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
    public void createScene(String... params) {

        mMaxLevelReached = Integer.parseInt(params[0]);
        createBackground();
        createMenuChildScene();
        createLevelSelection();
        setupTouchGesture();
        startMusic();
        createHUD();
    }

    @Override
    public void onBackKeyPressed() {

        if (!mHomeMenuScene.isVisible()) {
            mHomeMenuScene.setVisible(true);
            mLevelSelectorLayer.setVisible(false);
        } else {
            System.exit(0);
        }
    }

    @Override
    public void disposeScene() {

        mHUD.detachSelf();

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

    private void createHUD() {

        mHUD = new HUD();

        String gameTitle = mResourcesManager.activity.getResources().getString(R.string.app_name);
        mHUD.attachChild(new Text(Constants.SCREEN_WIDTH / 2, 400, mResourcesManager.font, gameTitle, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));

        mCamera.setHUD(mHUD);
    }

    private void createMenuChildScene() {

        mHomeMenuScene = new MenuScene(mCamera);

        TextMenuItem playTextMenuItem = new TextMenuItem(MENU_PLAY, mResourcesManager.font, mActivity.getResources().getString(R.string.play), mVertexBufferObjectManager);
        IMenuItem playMenuItem = new ScaleMenuItemDecorator(playTextMenuItem, 1.2f, 1);
        TextMenuItem optionsTextMenuItem = new TextMenuItem(MENU_OPTIONS, mResourcesManager.font, mActivity.getResources().getString(R.string.options), mVertexBufferObjectManager);
        IMenuItem helpMenuItem = new ScaleMenuItemDecorator(optionsTextMenuItem, 1.2f, 1);
        mHomeMenuScene.addMenuItem(playMenuItem);
        mHomeMenuScene.addMenuItem(helpMenuItem);

        mHomeMenuScene.buildAnimations();
        mHomeMenuScene.setBackgroundEnabled(false);

        mHomeMenuScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case MENU_PLAY:
                        mLevelSelectorLayer.setVisible(true);
                        mHomeMenuScene.setVisible(false);
                        return true;
                    case MENU_OPTIONS:
                        return true;
                    default:
                        return false;
                }
            }
        });

        setChildScene(mHomeMenuScene);
    }

    private void createLevelSelection() {

        mLevelSelectorLayer = new Entity(0, 0);
        mLevelSelectorLayer.setVisible(false);
        attachChild(mLevelSelectorLayer);

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

            int pageX = page * LEVEL_PAGE_WIDTH;

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

                    mLevelSelectorLayer.attachChild(box);

                    // display level number
                    if (isUnlocked) {
                        mLevelSelectorLayer.attachChild(new Text(boxX, boxY, mResourcesManager.menuLevelFont, String.valueOf(iLevel), mVertexBufferObjectManager));
                    }

                    this.registerTouchArea(box);

                    iLevel++;
                }
            }
        }

        //Set the max scroll possible, so it does not go over the boundaries.
        mLevelSelectorMinX = -(totalPages - 1) * LEVEL_PAGE_WIDTH;
        mLevelSelectorMaxX = 0;
    }

    //Here is where you call the level load.
    private void loadLevel(final int iLevel) {

        SceneManager.getInstance().createGameScene(iLevel);
    }


    // ===========================================================
    // Implemented interfaces
    // ===========================================================


    @Override
    public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

        if (!mLevelSelectorLayer.isVisible() || mLevelSelectorIsAnimating) {
            return;
        }

        float currentX = mLevelSelectorLayer.getX();
        float newX = currentX + pDistanceX;

        if (newX < mLevelSelectorMinX || newX > mLevelSelectorMaxX) return;

        mLevelSelectorLayer.setPosition(newX, 0);
    }

    @Override
    public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

    }

    @Override
    public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

        if (!mLevelSelectorLayer.isVisible() || mLevelSelectorIsAnimating) {
            return;
        }


        // move to nearest offset
        float currentX = mLevelSelectorLayer.getX();
        float nearestX = Math.round(currentX / LEVEL_PAGE_WIDTH) * LEVEL_PAGE_WIDTH;

        if (nearestX == currentX) return;

        final PathModifier.Path path = new PathModifier.Path(2).to(currentX, 0).to(nearestX, 0);
        float pathAnimationDuration = .4f;
        mLevelSelectorIsAnimating = true;
        mLevelSelectorLayer.registerEntityModifier(new PathModifier(pathAnimationDuration, path, null, new PathModifier.IPathModifierListener() {

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

                mLevelSelectorIsAnimating = false;
            }
        }));

    }
}
