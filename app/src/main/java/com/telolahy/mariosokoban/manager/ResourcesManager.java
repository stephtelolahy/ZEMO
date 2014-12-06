package com.telolahy.mariosokoban.manager;

import android.graphics.Color;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.MainActivity;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;

/**
 * Created by stephanohuguestelolahy on 11/15/14.
 */
public class ResourcesManager {

    private static final ResourcesManager INSTANCE = new ResourcesManager();

    private static final int GAME_WALL_TEXTURES_COUNT = 7;

    public Engine engine;
    public MainActivity activity;
    public ZoomCamera camera;
    public VertexBufferObjectManager vertexBufferObjectManager;

    // splash resources
    private ITexture splashTexture;
    public ITextureRegion splashTextureRegion;

    // common resources
    private ITexture commonBackButtonTexture;
    public ITextureRegion commonBackButtonTextureRegion;

    // menu resources
    private ITexture menuParallaxLayerBackTexture;
    private ITexture menuParallaxLayerMidTexture;
    private ITexture menuLevelLockedTexture;
    private ITexture menuLevelUnlockedTexture;
    private ITexture menuParallaxLayerFrontTexture;
    private ITexture menuPlayerTexture;
    public ITextureRegion menuParallaxLayerBackRegion;
    public ITextureRegion menuParallaxLayerMidRegion;
    public ITextureRegion menuParallaxLayerFrontRegion;
    public TiledTextureRegion menuPlayerTextureRegion;
    public ITextureRegion menuLevelLockedRegion;
    public ITextureRegion menuLevelUnlockedRegion;
    private ITexture menuCurrentLevelUnlockedTexture;
    public ITextureRegion menuCurrentLevelUnlockedRegion;
    private ITexture menuCreditsBackgroundTexture;
    public ITextureRegion menuCreditsBackgroundTextureRegion;
    private ITexture menuSnowParticleTexture;
    public ITextureRegion menuSnowParticleTextureRegion;

    public Font menuItemFont;
    public Font menuLevelFont;
    public Font menuTitleFont;
    public Font gameTitleFont;
    public Font menuCreditsFont;
    public Font menuCreditsTinyFont;

    public Music menuMusic;

    // game resources

    private ITexture gameWallTexture[] = new ITexture[GAME_WALL_TEXTURES_COUNT];
    public ITextureRegion gameWallTextureRegion[] = new ITextureRegion[GAME_WALL_TEXTURES_COUNT];

    private ITexture gameTargetTexture;
    private ITexture gameBoxTexture;
    private ITexture gameMarioTexture;
    public ITextureRegion gameTargetTextureRegion;
    public TiledTextureRegion gameBoxTextureRegion;
    public TiledTextureRegion gameMarioTextureRegion;
    private ITexture gameBackgroundTexture;
    public ITextureRegion gameGrassBackgroundTextureRegion;
    private ITexture gameScrollCoachMarkerTexture;
    public ITextureRegion gameScrollCoachMarkerRegion;

    private ITexture gameReplayTexture;
    public ITextureRegion gameReplayTextureRegion;

    private ITexture levelCompletedBackgroundTexture;
    public ITextureRegion levelCompletedBackgroundTextureRegion;
    private ITexture levelCompletedStarsTexture;
    public TiledTextureRegion levelCompletedStarsTextureRegion;

    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------

    public static ResourcesManager getInstance() {
        return INSTANCE;
    }

    public static void prepareManager(Engine engine, MainActivity activity, ZoomCamera camera, VertexBufferObjectManager vertexBufferObjectManager) {

        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vertexBufferObjectManager = vertexBufferObjectManager;
    }

    public void loadSplashResources() {

        try {
            splashTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/splash/creative_games_logo.png", TextureOptions.BILINEAR);
            splashTextureRegion = TextureRegionFactory.extractFromTexture(splashTexture);
            splashTexture.load();
        } catch (IOException e) {
            Debug.e(e);
        }
    }

    public void unloadSplashResources() {

        splashTexture.unload();
        splashTextureRegion = null;
    }

    public void loadCommonResources() {

        try {
            commonBackButtonTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/common/back_button.png", TextureOptions.BILINEAR);
            commonBackButtonTextureRegion = TextureRegionFactory.extractFromTexture(commonBackButtonTexture);
            commonBackButtonTexture.load();
        } catch (IOException e) {
            Debug.e(e);
        }
    }

    public void loadMenuResources() {

        loadMenuGraphics();
        loadMenuFonts();
        loadMenuMusics();
    }

    private void loadMenuGraphics() {

        try {
            menuParallaxLayerFrontTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/parallax_background_layer_front.png", TextureOptions.BILINEAR);
            menuParallaxLayerFrontRegion = TextureRegionFactory.extractFromTexture(menuParallaxLayerFrontTexture);
            menuParallaxLayerFrontTexture.load();

            menuParallaxLayerMidTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/parallax_background_layer_mid.png", TextureOptions.BILINEAR);
            menuParallaxLayerMidRegion = TextureRegionFactory.extractFromTexture(menuParallaxLayerMidTexture);
            menuParallaxLayerMidTexture.load();

            menuParallaxLayerBackTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/parallax_background_layer_back.png", TextureOptions.BILINEAR);
            menuParallaxLayerBackRegion = TextureRegionFactory.extractFromTexture(menuParallaxLayerBackTexture);
            menuParallaxLayerBackTexture.load();

            menuPlayerTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/player.png", TextureOptions.BILINEAR);
            menuPlayerTextureRegion = TextureRegionFactory.extractTiledFromTexture(menuPlayerTexture, 4, 1);
            menuPlayerTexture.load();

            menuLevelLockedTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/level_locked.png", TextureOptions.BILINEAR);
            menuLevelLockedRegion = TextureRegionFactory.extractFromTexture(menuLevelLockedTexture);
            menuLevelLockedTexture.load();

            menuLevelUnlockedTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/level_unlocked.png", TextureOptions.BILINEAR);
            menuLevelUnlockedRegion = TextureRegionFactory.extractFromTexture(menuLevelUnlockedTexture);
            menuLevelUnlockedTexture.load();

            menuCurrentLevelUnlockedTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/current_level_unlocked.png", TextureOptions.BILINEAR);
            menuCurrentLevelUnlockedRegion = TextureRegionFactory.extractFromTexture(menuCurrentLevelUnlockedTexture);
            menuCurrentLevelUnlockedTexture.load();

            menuCreditsBackgroundTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/credits_background.png", TextureOptions.BILINEAR);
            menuCreditsBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(menuCreditsBackgroundTexture);
            menuCreditsBackgroundTexture.load();

            menuSnowParticleTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/snow-particle.png", TextureOptions.BILINEAR);
            menuSnowParticleTextureRegion = TextureRegionFactory.extractFromTexture(menuSnowParticleTexture);
            menuSnowParticleTexture.load();

        } catch (IOException e) {
            Debug.e(e);
        }
    }

    private void loadMenuFonts() {

        final ITexture menuItemFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuItemFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), menuItemFontTexture, activity.getAssets(), "font/font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        menuItemFont.load();

        final ITexture menuLevelFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuLevelFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), menuLevelFontTexture, activity.getAssets(), "font/font.ttf", 24, true, Color.WHITE, 2, Color.TRANSPARENT);
        menuLevelFont.load();

        final ITexture menuTitleFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuTitleFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), menuTitleFontTexture, activity.getAssets(), "font/font.ttf", 64, true, Color.WHITE, 2, Color.BLACK);
        menuTitleFont.load();

        final ITexture gameTitleFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        gameTitleFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameTitleFontTexture, activity.getAssets(), "font/font.ttf", 38, true, Color.argb(256 * 6 / 10, 255, 255, 255), 2, Color.TRANSPARENT);
        gameTitleFont.load();

        final ITexture menuCreditsFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuCreditsFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), menuCreditsFontTexture, activity.getAssets(), "font/font.ttf", 24, true, Color.GRAY, 2, Color.TRANSPARENT);
        menuCreditsFont.load();

        final ITexture menuCreditsTinyFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuCreditsTinyFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), menuCreditsTinyFontTexture, activity.getAssets(), "font/font.ttf", 20, true, Color.GRAY, 2, Color.TRANSPARENT);
        menuCreditsTinyFont.load();
    }

    private void loadMenuMusics() {

        try {
            menuMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/mainscreen.ogg");
        } catch (final IOException e) {
            Debug.e(e);
        }
    }

    public void unloadMenuTextures() {

        menuParallaxLayerBackTexture.unload();
        menuParallaxLayerMidTexture.unload();
        menuParallaxLayerFrontTexture.unload();
        menuPlayerTexture.unload();
        menuLevelLockedTexture.unload();
        menuLevelUnlockedTexture.unload();
        menuCurrentLevelUnlockedTexture.unload();
        menuCreditsBackgroundTexture.unload();

        menuParallaxLayerBackRegion = null;
        menuParallaxLayerMidRegion = null;
        menuParallaxLayerFrontRegion = null;
        menuPlayerTextureRegion = null;
        menuLevelLockedRegion = null;
        menuLevelUnlockedRegion = null;
        menuCurrentLevelUnlockedRegion = null;
        menuCreditsBackgroundTextureRegion = null;
    }

    public void loadMenuTextures() {

        loadMenuGraphics();
    }


    public void loadGameResources() {

        loadGameGraphics();
    }

    private void loadGameGraphics() {

        try {

            for (int i = 0; i < GAME_WALL_TEXTURES_COUNT; i++) {
                gameWallTexture[i] = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/wall" + i + ".png", TextureOptions.BILINEAR);
                gameWallTextureRegion[i] = TextureRegionFactory.extractFromTexture(gameWallTexture[i]);
                gameWallTexture[i].load();
            }

            gameTargetTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/target.png", TextureOptions.BILINEAR);
            gameTargetTextureRegion = TextureRegionFactory.extractFromTexture(gameTargetTexture);
            gameTargetTexture.load();

            gameBoxTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/box.png", TextureOptions.BILINEAR);
            gameBoxTextureRegion = TextureRegionFactory.extractTiledFromTexture(gameBoxTexture, 2, 1);
            gameBoxTexture.load();

            gameMarioTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/player.png", TextureOptions.BILINEAR);
            gameMarioTextureRegion = TextureRegionFactory.extractTiledFromTexture(gameMarioTexture, 4, 4);
            gameMarioTexture.load();

            gameBackgroundTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/grass.png", TextureOptions.REPEATING_NEAREST_PREMULTIPLYALPHA);
            gameGrassBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(gameBackgroundTexture);
            gameGrassBackgroundTextureRegion.setTextureWidth(Constants.SCREEN_WIDTH * 10);
            gameGrassBackgroundTextureRegion.setTextureHeight(Constants.SCREEN_HEIGHT * 5);
            gameBackgroundTexture.load();

            levelCompletedBackgroundTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/level_completed/background.png", TextureOptions.BILINEAR);
            levelCompletedBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(levelCompletedBackgroundTexture);
            levelCompletedBackgroundTexture.load();

            levelCompletedStarsTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/level_completed/star.png", TextureOptions.BILINEAR);
            levelCompletedStarsTextureRegion = TextureRegionFactory.extractTiledFromTexture(levelCompletedStarsTexture, 2, 1);
            levelCompletedStarsTexture.load();

            gameScrollCoachMarkerTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/game_scroll_coach_marker.png", TextureOptions.BILINEAR);
            gameScrollCoachMarkerRegion = TextureRegionFactory.extractFromTexture(gameScrollCoachMarkerTexture);
            gameScrollCoachMarkerTexture.load();

            gameReplayTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/retry.png", TextureOptions.BILINEAR);
            gameReplayTextureRegion = TextureRegionFactory.extractFromTexture(gameReplayTexture);
            gameReplayTexture.load();

        } catch (IOException e) {
            Debug.e(e);
        }
    }

    public void unloadGameTextures() {

        for (int i = 0; i < GAME_WALL_TEXTURES_COUNT; i++) {
            gameWallTexture[i].unload();
            gameWallTextureRegion[i] = null;
        }
        gameBoxTexture.unload();
        gameTargetTexture.unload();
        gameMarioTexture.unload();
        gameBackgroundTexture.unload();
        levelCompletedBackgroundTexture.unload();
        levelCompletedStarsTexture.unload();
        gameScrollCoachMarkerTexture.unload();
        gameReplayTexture.unload();

        gameBoxTextureRegion = null;
        gameTargetTextureRegion = null;
        gameMarioTextureRegion = null;
        gameGrassBackgroundTextureRegion = null;
        levelCompletedBackgroundTextureRegion = null;
        levelCompletedStarsTextureRegion = null;
        gameScrollCoachMarkerRegion = null;
        gameReplayTextureRegion = null;
    }
}
