package com.telolahy.mariosokoban.manager;

import android.graphics.Color;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.MainActivity;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
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

    public Engine engine;
    public MainActivity activity;
    public BoundCamera camera;
    public VertexBufferObjectManager vertexBufferObjectManager;

    // splash resources
    private ITexture splashTexture;
    public ITextureRegion splashTextureRegion;

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
    public Font menuLevelFont;
    public ITextureRegion menuLevelLockedRegion;
    public ITextureRegion menuLevelUnlockedRegion;
    private ITexture menuCurrentLevelUnlockedTexture;
    public ITextureRegion menuCurrentLevelUnlockedRegion;


    public Font font;
    public Music menuMusic;

    // game resources
    private ITexture gameWallTexture;
    private ITexture gameTargetTexture;
    private ITexture gameCowTexture;
    private ITexture gameMarioTexture;
    public ITextureRegion gameWallTextureRegion;
    public ITextureRegion gameTargetTextureRegion;
    public TiledTextureRegion gameCowTextureRegion;
    public TiledTextureRegion gameMarioTextureRegion;
    private ITexture gameBackgroundTexture;
    public ITextureRegion gameGrassBackgroundTextureRegion;

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

    public static void prepareManager(Engine engine, MainActivity activity, BoundCamera camera, VertexBufferObjectManager vertexBufferObjectManager) {

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

        } catch (IOException e) {
            Debug.e(e);
        }
    }

    private void loadMenuFonts() {

        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font/font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        font.load();

        final ITexture menuFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        menuLevelFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), menuFontTexture, activity.getAssets(), "font/font.ttf", 24, true, Color.WHITE, 2, Color.TRANSPARENT);
        menuLevelFont.load();
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

        menuParallaxLayerBackRegion = null;
        menuParallaxLayerMidRegion = null;
        menuParallaxLayerFrontRegion = null;
        menuPlayerTextureRegion = null;
        menuLevelLockedRegion = null;
        menuLevelUnlockedRegion = null;
        menuCurrentLevelUnlockedRegion = null;
    }

    public void loadMenuTextures() {

        loadMenuGraphics();
    }


    public void loadGameResources() {

        loadGameGraphics();
    }

    private void loadGameGraphics() {

        try {
            gameWallTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/wall.png", TextureOptions.BILINEAR);
            gameWallTextureRegion = TextureRegionFactory.extractFromTexture(gameWallTexture);
            gameWallTexture.load();

            gameTargetTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/target.png", TextureOptions.BILINEAR);
            gameTargetTextureRegion = TextureRegionFactory.extractFromTexture(gameTargetTexture);
            gameTargetTexture.load();

            gameCowTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/cow.png", TextureOptions.BILINEAR);
            gameCowTextureRegion = TextureRegionFactory.extractTiledFromTexture(gameCowTexture, 3, 4);
            gameCowTexture.load();

            gameMarioTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/player.png", TextureOptions.BILINEAR);
            gameMarioTextureRegion = TextureRegionFactory.extractTiledFromTexture(gameMarioTexture, 4, 4);
            gameMarioTexture.load();

            gameBackgroundTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/background_grass.png", TextureOptions.REPEATING_NEAREST_PREMULTIPLYALPHA);
            gameGrassBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(gameBackgroundTexture);
            gameGrassBackgroundTextureRegion.setTextureWidth(Constants.SCREEN_WIDTH * 4);
            gameGrassBackgroundTextureRegion.setTextureHeight(Constants.SCREEN_HEIGHT * 4);
            gameBackgroundTexture.load();

            levelCompletedBackgroundTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/level_completed_background.png", TextureOptions.BILINEAR);
            levelCompletedBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(levelCompletedBackgroundTexture);
            levelCompletedBackgroundTexture.load();

            levelCompletedStarsTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/star.png", TextureOptions.BILINEAR);
            levelCompletedStarsTextureRegion = TextureRegionFactory.extractTiledFromTexture(levelCompletedStarsTexture, 2, 1);
            levelCompletedStarsTexture.load();

        } catch (IOException e) {
            Debug.e(e);
        }
    }

    public void unloadGameTextures() {

        gameCowTexture.unload();
        gameWallTexture.unload();
        gameTargetTexture.unload();
        gameMarioTexture.unload();
        gameBackgroundTexture.unload();
        levelCompletedBackgroundTexture.unload();
        levelCompletedStarsTexture.unload();

        gameCowTextureRegion = null;
        gameWallTextureRegion = null;
        gameTargetTextureRegion = null;
        gameMarioTextureRegion = null;
        gameGrassBackgroundTextureRegion = null;
        levelCompletedBackgroundTextureRegion = null;
        levelCompletedStarsTextureRegion = null;
    }
}
