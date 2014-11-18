package com.telolahy.mariosokoban.manager;

import android.graphics.Color;

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
    private ITexture menuParallaxLayerFrontTexture;
    private ITexture menuPlayTexture;
    private ITexture menuHelpTexture;
    private ITexture menuPlayerTexture;
    public ITextureRegion menuParallaxLayerBackRegion;
    public ITextureRegion menuParallaxLayerMidRegion;
    public ITextureRegion menuParallaxLayerFrontRegion;
    public ITextureRegion menuPlayTextureRegion;
    public ITextureRegion menuHelpTextureRegion;
    public TiledTextureRegion menuPlayerTextureRegion;

    public Font font;
    public Music menuMusic;

    // game resources
    private ITexture gameWallTexture;
    private ITexture gameTargetTexture;
    private ITexture gameBoxTexture;
    private ITexture gamePlayerTexture;
    public ITextureRegion gameWallTextureRegion;
    public ITextureRegion gameTargetTextureRegion;
    public TiledTextureRegion gameBoxTextureRegion;
    public TiledTextureRegion gamePlayerTextureRegion;

    private ITexture gameBackgroundTexture;
    public ITextureRegion gameGrassBackgroundTextureRegion;

    private ITexture gameOnScreenControlBaseTexture;
    public ITextureRegion gameOnScreenControlBaseTextureRegion;
    private ITexture gameOnScreenControlKnobTexture;
    public ITextureRegion gameOnScreenControlKnobTextureRegion;

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

            menuPlayTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/play.png", TextureOptions.BILINEAR);
            menuPlayTextureRegion = TextureRegionFactory.extractFromTexture(menuPlayTexture);
            menuPlayTexture.load();

            menuHelpTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/help.png", TextureOptions.BILINEAR);
            menuHelpTextureRegion = TextureRegionFactory.extractFromTexture(menuHelpTexture);
            menuHelpTexture.load();

            menuPlayerTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/menu/player.png", TextureOptions.BILINEAR);
            menuPlayerTextureRegion = TextureRegionFactory.extractTiledFromTexture(menuPlayerTexture, 4, 4);
            menuPlayerTexture.load();

        } catch (IOException e) {
            Debug.e(e);
        }
    }

    private void loadMenuFonts() {

        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font/font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        font.load();
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
        menuPlayTexture.unload();
        menuHelpTexture.unload();
        menuPlayerTexture.unload();

        menuParallaxLayerBackRegion = null;
        menuParallaxLayerMidRegion = null;
        menuParallaxLayerFrontRegion = null;
        menuPlayTextureRegion = null;
        menuHelpTextureRegion = null;
        menuPlayerTextureRegion = null;
    }

    public void loadMenuTextures() {

        menuParallaxLayerBackTexture.load();
        menuParallaxLayerMidTexture.load();
        menuParallaxLayerFrontTexture.load();
        menuPlayTexture.load();
        menuHelpTexture.load();
        menuPlayerTexture.load();
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

            gameBoxTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/box.png", TextureOptions.BILINEAR);
            gameBoxTextureRegion = TextureRegionFactory.extractTiledFromTexture(gameBoxTexture, 2, 1);
            gameBoxTexture.load();

            gamePlayerTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/player.png", TextureOptions.BILINEAR);
            gamePlayerTextureRegion = TextureRegionFactory.extractTiledFromTexture(gamePlayerTexture, 4, 1);
            gamePlayerTexture.load();

            gameBackgroundTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/background_grass.png", TextureOptions.REPEATING_NEAREST);
            gameGrassBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(gameBackgroundTexture);
            gameBackgroundTexture.load();

            gameOnScreenControlBaseTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/onscreen_control_base.png", TextureOptions.BILINEAR);
            gameOnScreenControlBaseTextureRegion = TextureRegionFactory.extractFromTexture(gameOnScreenControlBaseTexture);
            gameOnScreenControlBaseTexture.load();

            gameOnScreenControlKnobTexture = new AssetBitmapTexture(engine.getTextureManager(), activity.getAssets(), "gfx/game/onscreen_control_knob.png", TextureOptions.BILINEAR);
            gameOnScreenControlKnobTextureRegion = TextureRegionFactory.extractFromTexture(gameOnScreenControlKnobTexture);
            gameOnScreenControlKnobTexture.load();

        } catch (IOException e) {
            Debug.e(e);
        }
    }

    public void unloadGameTextures() {

        gameBoxTexture.unload();
        gameWallTexture.unload();
        gameTargetTexture.unload();
        gamePlayerTexture.unload();
        gameBackgroundTexture.unload();
        gameOnScreenControlBaseTexture.unload();
        gameOnScreenControlKnobTexture.unload();

        gameBoxTextureRegion = null;
        gameWallTextureRegion = null;
        gameTargetTextureRegion = null;
        gamePlayerTextureRegion = null;
        gameGrassBackgroundTextureRegion = null;
        gameOnScreenControlBaseTextureRegion = null;
        gameOnScreenControlKnobTextureRegion = null;
    }
}
