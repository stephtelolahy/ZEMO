package com.telolahy.mariosokoban.scene;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLES20;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.GameManager;
import com.telolahy.mariosokoban.manager.SceneManager;
import com.telolahy.mariosokoban.utils.LevelSelectorMenuScene;
import com.telolahy.mariosokoban.utils.snow.RegisterXSwingEntityModifierInitializer;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.particle.BatchedPseudoSpriteParticleSystem;
import org.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
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
    public static final int MENU_TYPE_CREDITS = 3;

    // ===========================================================
    // Fields
    // ===========================================================

    private HUD mHUD;
    private Text mTitle;
    private Sprite mBackButton;
    private TextMenuItem mMusicTextMenuItem;
    private MenuScene mHomeMenuScene;
    private LevelSelectorMenuScene mLevelSelectorMenuScene;
    private CreditsScene mCreditsScene;
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

        int currentLevel = params[0];
        int maxLevelReached = params[1];
        int menuType = params[2];
        createBackground();
        createHomeMenuChildScene();
        createOptionsMenuChildScene();
        createLevelSelectorChildScene(maxLevelReached, currentLevel);
        createCreditsChildScene();

        if (GameManager.getInstance().isSnowEnabled()) {
            createSnowScene();
        }

        createHUD();
        setupTouchGesture();

        if (menuType == MENU_TYPE_HOME) {
            displayHomeMenu();
        } else if (menuType == MENU_TYPE_LEVEL_SELECTOR) {
            displayLevelSelector();
        }
        playMusic();
    }

    @Override
    protected void onDisposeScene() {

        mCamera.setHUD(null);
        mHUD.detachSelf();

        mCreditsScene.onDisposeScene();
        mLevelSelectorMenuScene.dispose();
        mHomeMenuScene.dispose();
        mOptionsMenuScene.dispose();


    }

    @Override
    public void onBackKeyPressed() {

        if (mCurrentMenuType == MENU_TYPE_HOME) {
            displayExitDialog();
        } else if (mCurrentMenuType == MENU_TYPE_CREDITS) {
            displayOptionsMenu();
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

        if (mResourcesManager.menuMusic != null && !mResourcesManager.menuMusic.isPlaying() && GameManager.getInstance().isMusicEnabled()) {
            mResourcesManager.menuMusic.setLooping(true);
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
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT - mResourcesManager.menuParallaxLayerBackRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerBackRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-1.f, 2.67f, new Sprite(Constants.SCREEN_WIDTH / 2, mResourcesManager.menuParallaxLayerMidRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerMidRegion, mVertexBufferObjectManager)));

        if (GameManager.getInstance().isCloudEnabled()) {
            autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-1.f, new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT - mResourcesManager.menuParallaxLayerMidRegionCloud.getHeight() / 2, mResourcesManager.menuParallaxLayerMidRegionCloud, mVertexBufferObjectManager)));
        }

        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.f, 1.3f, new Sprite(Constants.SCREEN_WIDTH / 2, mResourcesManager.menuParallaxLayerFrontRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerFrontRegion, mVertexBufferObjectManager)));
        setBackground(autoParallaxBackground);

        final int groundY = 28;
        final AnimatedSprite player = new AnimatedSprite(Constants.SCREEN_WIDTH / 2, mResourcesManager.menuPlayerTextureRegion.getHeight() / 2 + groundY, mResourcesManager.menuPlayerTextureRegion, mVertexBufferObjectManager);
        final int tileDelay = 300;
        player.animate(new long[]{tileDelay, tileDelay, tileDelay, tileDelay}, 0, 3, true);
        attachChild(player);
    }

    private void createHUD() {

        mHUD = new HUD();

        mTitle = new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT * 3 / 4, mResourcesManager.menuTitleFont, "abcdefghijklmnopqrstuvwxyz", new TextOptions(HorizontalAlign.CENTER), mVertexBufferObjectManager);
        mHUD.attachChild(mTitle);

        final int LEFT_MARGIN = 64;
        final int TOP_MARGIN = 48;
        mBackButton = new Sprite(LEFT_MARGIN, Constants.SCREEN_HEIGHT - TOP_MARGIN, mResourcesManager.menuBackButtonTextureRegion, mVertexBufferObjectManager) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

                if (mBackButton.isVisible() && pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
                    mResourcesManager.menuItemClickedSound.play();
                    onBackKeyPressed();
                }
                return true;
            }
        };
        mHUD.attachChild(mBackButton);
        mHUD.registerTouchArea(mBackButton);

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

                mResourcesManager.menuItemClickedSound.play();
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

                mResourcesManager.menuItemClickedSound.play();
                switch (pMenuItem.getID()) {
                    case MENU_ITEM_MUSIC:
                        toggleMusic();
                        return true;
                    case MENU_ITEM_CREDITS:
                        displayCreditsScene();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void createSnowScene() {

        final BatchedPseudoSpriteParticleSystem particleSystem = new BatchedPseudoSpriteParticleSystem(
                new RectangleParticleEmitter(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT, Constants.SCREEN_WIDTH, 1),
                2, 5, 100, mResourcesManager.menuSnowParticleTextureRegion,
                mVertexBufferObjectManager);
        particleSystem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
        particleSystem.addParticleInitializer(new VelocityParticleInitializer<Entity>(-3, 3, -20, -40));
        particleSystem.addParticleInitializer(new AccelerationParticleInitializer<Entity>(-3, 3, -3, -5));
        particleSystem.addParticleInitializer(new RotationParticleInitializer<Entity>(0.0f, 360.0f));
        particleSystem.addParticleInitializer(new ExpireParticleInitializer<Entity>(10f));
        particleSystem.addParticleInitializer(new ScaleParticleInitializer<Entity>(0.2f, 0.5f));
        particleSystem.addParticleInitializer(new RegisterXSwingEntityModifierInitializer<Entity>(10f, 0f, (float) Math.PI * 8, 3f, 25f, true));

        particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(6f, 10f, 1.0f, 0.0f));

        attachChild(particleSystem);
    }

    private void createLevelSelectorChildScene(int maxLevelReached, int currentLevel) {

        int displayedLevelsCount = GameManager.getInstance().displayedLevelsCount();
        mLevelSelectorMenuScene = new LevelSelectorMenuScene(
                mCamera,
                currentLevel,
                maxLevelReached,
                displayedLevelsCount,
                Constants.LEVEL_ROWS_PER_SCREEN,
                Constants.LEVEL_COLUMNS_PER_SCREEN,
                this,
                new LevelSelectorMenuScene.LevelSelectorMenuSceneListener() {
                    @Override
                    public void levelSelectorItemClicked(int level) {

                        mResourcesManager.menuItemClickedSound.play();
                        SceneManager.getInstance().createGameScene(level);
                    }
                });
    }

    private void createCreditsChildScene() {
        mCreditsScene = new CreditsScene();
    }

    private void displayLevelSelector() {

        clearChildScene();
        setChildScene(mLevelSelectorMenuScene);
        mTitle.setText(mActivity.getResources().getString(R.string.play));
        mLevelSelectorMenuScene.setEnabled(true);
        mBackButton.setVisible(true);
        mCurrentMenuType = MENU_TYPE_LEVEL_SELECTOR;
    }

    private void displayHomeMenu() {

        if (mCurrentMenuType == MENU_TYPE_LEVEL_SELECTOR) {
            mLevelSelectorMenuScene.setEnabled(false);
        }

        clearChildScene();
        setChildScene(mHomeMenuScene);
        mTitle.setText(mResourcesManager.activity.getResources().getString(R.string.app_name));
        mBackButton.setVisible(false);
        mCurrentMenuType = MENU_TYPE_HOME;
    }

    private void displayOptionsMenu() {

        clearChildScene();
        setChildScene(mOptionsMenuScene);
        mTitle.setText(mResourcesManager.activity.getResources().getString(R.string.options));
        mBackButton.setVisible(true);
        mCurrentMenuType = MENU_TYPE_OPTIONS;
    }

    private void displayCreditsScene() {
        clearChildScene();
        setChildScene(mCreditsScene);
        mTitle.setText(mResourcesManager.activity.getResources().getString(R.string.credits));
        mBackButton.setVisible(true);
        mCurrentMenuType = MENU_TYPE_CREDITS;
    }

    private void displayExitDialog() {

        mActivity.runOnUiThread(new Runnable() {
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mResourcesManager.activity.getResources().getString(R.string.exit));
                builder.setMessage(mResourcesManager.activity.getResources().getString(R.string.exit_message));
                builder.setPositiveButton((mResourcesManager.activity.getResources().getString(R.string.yes)), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                });
                builder.setNegativeButton(mResourcesManager.activity.getResources().getString(R.string.no), null);
                builder.setCancelable(false);
                builder.show();
            }
        });
    }

    private void toggleMusic() {

        boolean musicEnabled = GameManager.getInstance().isMusicEnabled();
        musicEnabled = !musicEnabled;
        String musicText = musicEnabled ? mActivity.getResources().getString(R.string.music_on) : mActivity.getResources().getString(R.string.music_off);
        mMusicTextMenuItem.setText(musicText);
        GameManager.getInstance().setMusicEnabled(musicEnabled);
        if (musicEnabled) {
            playMusic();
        } else {
            pauseMusic();
        }
    }

    // ===========================================================
    // Implemented interfaces
    // ===========================================================


}
