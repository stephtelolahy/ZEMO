package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.GameManager;
import com.telolahy.mariosokoban.manager.SceneManager;
import com.telolahy.mariosokoban.utils.LevelSelectorMenuScene;

import org.andengine.engine.camera.hud.HUD;
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
import org.andengine.util.adt.align.HorizontalAlign;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class MainMenuScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MENU_ITEM_PLAY = 0;
    private static final int MENU_ITEM_OPTIONS = 1;
    private static final int MENU_ITEM_MUSIC = 2;
    private static final int MENU_ITEM_CREDITS = 3;

    public static final int MENU_TYPE_HOME = 0;
    public static final int MENU_TYPE_LEVEL_SELECTOR = 1;
    public static final int MENU_TYPE_OPTIONS = 2;

    // ===========================================================
    // Fields
    // ===========================================================

    private HUD mHUD;
    private Text mTitle;
    private TextMenuItem mMusicTextMenuItem;
    private MenuScene mHomeMenuScene;
    private LevelSelectorMenuScene mLevelSelectorMenuScene;
    private MenuScene mOptionsMenuScene;

    private int mCurrentMenuType;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MainMenuScene(int... params) {
        super(params);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    @Override
    protected void onCreateScene(int... params) {

        int maxLevelReached = params[0];
        int menuType = params[1];
        createBackground();
        createHomeMenuChildScene();
        createOptionsMenuChildScene();
        createLevelSelectorChildScene(maxLevelReached);
        createHUD();
        setupTouchGesture();

        if (menuType == MENU_TYPE_HOME) {
            displayHomeMenu();
        } else if (menuType == MENU_TYPE_LEVEL_SELECTOR) {
            displayLevelSelector();
        }

        if (GameManager.getInstance().isMusicEnabled()){
            playMusic();
        }
    }

    @Override
    protected void onDisposeScene() {

        mCamera.setHUD(null);
        mHUD.detachSelf();
    }

    @Override
    public void onBackKeyPressed() {

        if (mCurrentMenuType == MENU_TYPE_HOME) {
            System.exit(0);
        } else {
            displayHomeMenu();
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setupTouchGesture() {

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
    }

    private void playMusic() {

        if (mResourcesManager.menuMusic != null && !mResourcesManager.menuMusic.isPlaying()) {
            mResourcesManager.menuMusic.setLooping(true);
            mResourcesManager.menuMusic.setVolume(0.4f);
            mResourcesManager.menuMusic.play();
        }
    }

    private void pauseMusic() {

        if (mResourcesManager.menuMusic != null && mResourcesManager.menuMusic.isPlaying()) {
            mResourcesManager.menuMusic.pause();
        }
    }

    private void createBackground() {

        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.menuParallaxLayerBackRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-2.f, 2.f, new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT - mResourcesManager.menuParallaxLayerMidRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerMidRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.f, new Sprite(Constants.SCREEN_WIDTH / 2, mResourcesManager.menuParallaxLayerFrontRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerFrontRegion, mVertexBufferObjectManager)));
        setBackground(autoParallaxBackground);

        final int groundY = 28;
        final AnimatedSprite player = new AnimatedSprite(Constants.SCREEN_WIDTH / 2, mResourcesManager.menuPlayerTextureRegion.getHeight() / 2 + groundY, mResourcesManager.menuPlayerTextureRegion, mVertexBufferObjectManager);
        final int tileDelay = 300;
        player.animate(new long[]{tileDelay, tileDelay, tileDelay, tileDelay}, 0, 3, true);
        attachChild(player);
    }

    private void createHUD() {

        mHUD = new HUD();

        String gameTitle = mResourcesManager.activity.getResources().getString(R.string.app_name);
        mTitle = new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT * 3 / 4, mResourcesManager.menuTitleFont, gameTitle, new TextOptions(HorizontalAlign.CENTER), mVertexBufferObjectManager);
        mHUD.attachChild(mTitle);

        mCamera.setHUD(mHUD);
    }

    private void createHomeMenuChildScene() {

        mHomeMenuScene = new MenuScene(mCamera);

        TextMenuItem playTextMenuItem = new TextMenuItem(MENU_ITEM_PLAY, mResourcesManager.menuItemFont, mActivity.getResources().getString(R.string.play), mVertexBufferObjectManager);
        IMenuItem playMenuItem = new ScaleMenuItemDecorator(playTextMenuItem, 1.2f, 1);
        TextMenuItem optionsTextMenuItem = new TextMenuItem(MENU_ITEM_OPTIONS, mResourcesManager.menuItemFont, mActivity.getResources().getString(R.string.options), mVertexBufferObjectManager);
        IMenuItem helpMenuItem = new ScaleMenuItemDecorator(optionsTextMenuItem, 1.2f, 1);
        mHomeMenuScene.addMenuItem(playMenuItem);
        mHomeMenuScene.addMenuItem(helpMenuItem);

        mHomeMenuScene.buildAnimations();
        mHomeMenuScene.setBackgroundEnabled(false);

        mHomeMenuScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case MENU_ITEM_PLAY:
                        displayLevelSelector();
                        return true;
                    case MENU_ITEM_OPTIONS:
                        displayOptionsMenu();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void createOptionsMenuChildScene() {

        mOptionsMenuScene = new MenuScene(mCamera);

        boolean musicEnabled = GameManager.getInstance().isMusicEnabled();
        String musicText = musicEnabled ? mActivity.getResources().getString(R.string.music_on) : mActivity.getResources().getString(R.string.music_off);
        mMusicTextMenuItem = new TextMenuItem(MENU_ITEM_MUSIC, mResourcesManager.menuItemFont, musicText, mVertexBufferObjectManager);
        IMenuItem musicMenuItem = new ScaleMenuItemDecorator(mMusicTextMenuItem, 1.2f, 1);
        TextMenuItem creditsTextMenuItem = new TextMenuItem(MENU_ITEM_CREDITS, mResourcesManager.menuItemFont, mActivity.getResources().getString(R.string.credits), mVertexBufferObjectManager);
        IMenuItem creditsMenuItem = new ScaleMenuItemDecorator(creditsTextMenuItem, 1.2f, 1);
        mOptionsMenuScene.addMenuItem(musicMenuItem);
        mOptionsMenuScene.addMenuItem(creditsMenuItem);

        mOptionsMenuScene.buildAnimations();
        mOptionsMenuScene.setBackgroundEnabled(false);

        mOptionsMenuScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case MENU_ITEM_MUSIC:
                        boolean musicEnabled = GameManager.getInstance().isMusicEnabled();
                        musicEnabled = !musicEnabled;
                        String musicText = musicEnabled ? mActivity.getResources().getString(R.string.music_on) : mActivity.getResources().getString(R.string.music_off);
                        mMusicTextMenuItem.setText(musicText);
                        GameManager.getInstance().setMusicEnabled(musicEnabled);
                        if (musicEnabled){
                            playMusic();
                        } else {
                            pauseMusic();
                        }
                        return true;
                    case MENU_ITEM_CREDITS:
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void createLevelSelectorChildScene(int maxLevelReached) {

        int displayedLevelsCount = GameManager.getInstance().displayedLevelsCount();
        mLevelSelectorMenuScene = new LevelSelectorMenuScene(
                mCamera,
                maxLevelReached,
                displayedLevelsCount,
                Constants.LEVEL_ROWS_PER_SCREEN,
                Constants.LEVEL_COLUMNS_PER_SCREEN,
                this,
                new LevelSelectorMenuScene.LevelSelectorMenuSceneListener() {
                    @Override
                    public void levelSelectorItemClicked(int level) {

                        SceneManager.getInstance().createGameScene(level);
                    }
                });
    }

    private void displayLevelSelector() {

        clearChildScene();
        setChildScene(mLevelSelectorMenuScene);
        mTitle.setText(mResourcesManager.activity.getResources().getString(R.string.play));
        mLevelSelectorMenuScene.setEnabled(true);
        mCurrentMenuType = MENU_TYPE_LEVEL_SELECTOR;
    }

    private void displayHomeMenu() {

        if (mCurrentMenuType == MENU_TYPE_LEVEL_SELECTOR) {
            mLevelSelectorMenuScene.setEnabled(false);
        }

        clearChildScene();
        setChildScene(mHomeMenuScene);
        mTitle.setText(mResourcesManager.activity.getResources().getString(R.string.app_name));
        mCurrentMenuType = MENU_TYPE_HOME;
    }

    private void displayOptionsMenu() {

        clearChildScene();
        setChildScene(mOptionsMenuScene);
        mTitle.setText(mResourcesManager.activity.getResources().getString(R.string.options));
        mCurrentMenuType = MENU_TYPE_OPTIONS;
    }

    // ===========================================================
    // Implemented interfaces
    // ===========================================================


}
