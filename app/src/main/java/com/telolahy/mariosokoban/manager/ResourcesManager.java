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
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
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
    private BitmapTextureAtlas splashTextureAtlas;
    public ITextureRegion splashTextureRegion;

    // menu resources
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    public ITextureRegion menuParallaxLayerBackRegion;
    public ITextureRegion menuParallaxLayerMidRegion;
    public ITextureRegion menuParallaxLayerFrontRegion;
    public ITextureRegion menuPlayTextureRegion;
    public ITextureRegion menuHelpTextureRegion;
    public TiledTextureRegion menuPlayerTextureRegion;

    public Font font;

    public Music menuMusic;

    // game resources
    private BuildableBitmapTextureAtlas gameTextureAtlas;
    public ITextureRegion gameGrassBackgroundTextureRegion;
    public ITextureRegion gameBoxTextureRegion;
    public ITextureRegion gameBoxOKTextureRegion;
    public ITextureRegion gameWallTextureRegion;
    public ITextureRegion gameTargetTextureRegion;
    public TiledTextureRegion gamePlayerTextureRegion;


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

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");
        splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "creative_games_logo.png", 0, 0);
        splashTextureAtlas.load();
    }

    public void unloadSplashResources() {

        splashTextureAtlas.unload();
        splashTextureRegion = null;
    }

    public void loadMenuResources() {

        loadMenuGraphics();
        loadMenuFonts();
        loadMenuMusics();
    }

    private void loadMenuGraphics() {

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

        menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        menuParallaxLayerFrontRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "parallax_background_layer_front.png");
        menuParallaxLayerBackRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "parallax_background_layer_back.png");
        menuParallaxLayerMidRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "parallax_background_layer_mid.png");
        menuPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuTextureAtlas, activity, "player.png", 4, 4);
        menuPlayTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
        menuHelpTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "help.png");
        try {
            menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            menuTextureAtlas.load();
        } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Debug.e(e);
        }
    }

    private void loadMenuFonts() {

        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        font.load();
    }

    private void loadMenuMusics() {

        MusicFactory.setAssetBasePath("mfx/");
        try {
            menuMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mainscreen.ogg");
        } catch (final IOException e) {
            Debug.e(e);
        }
    }

    public void unloadMenuTextures() {
        menuTextureAtlas.unload();
    }

    public void loadMenuTextures() {
        menuTextureAtlas.load();
    }


    public void loadGameResources() {

        loadGameGraphics();
    }

    private void loadGameGraphics() {

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 256, 256);
        gameGrassBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "background_grass.png");
        gameBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "box.png");
        gameBoxOKTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "box_ok.png");
        gameWallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "wall.png");
        gameTargetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "target.png");
        gamePlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 4, 1);
        try {
            gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            gameTextureAtlas.load();
        } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Debug.e(e);
        }
    }

    public void unloadGameTextures() {

        gameTextureAtlas.unload();
    }
}
