package com.telolahy.mariosokoban;


import android.graphics.Color;
import android.widget.Toast;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class ScrollMenuActivity extends SimpleBaseGameActivity implements IScrollDetectorListener, IOnSceneTouchListener {

    // ===========================================================
    // Constants
    // ===========================================================
    protected static int CAMERA_WIDTH = 800;
    protected static int CAMERA_HEIGHT = 480;

    protected static int FONT_SIZE = 26;

    protected static int LEVELS = 30;
    protected static int LEVEL_COLUMNS_PER_SCREEN = 4;
    protected static int LEVEL_ROWS_PER_SCREEN = 3;
    protected static int LEVEL_PADDING = 50;
    protected static int LEVEL_BOX_SIZE = 50;

    // ===========================================================
    // Fields
    // ===========================================================
    private Scene mScene;
    private Camera mCamera;

    private Font mDroidFont;
    private Texture mDroidFontTexture;

    // Scrolling
    private SurfaceScrollDetector mScrollDetector;

    private float mMinY = 0;
    private float mMaxY = 0;
    private float mCurrentY = 0;

    //This value will be loaded from whatever method used to store data.
    private int mMaxLevelReached = 7;

    private Entity mMenuLayer;

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
    protected void onCreateResources() {
        // Paths
        FontFactory.setAssetBasePath("font/");

        // Font

        this.mDroidFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.mDroidFont = FontFactory.createFromAsset(this.getFontManager(), mDroidFontTexture, this.getAssets(), "font.ttf", FONT_SIZE, true, Color.WHITE);
        this.mDroidFont.load();

        this.mEngine.getTextureManager().loadTexture(mDroidFontTexture);
        this.mEngine.getFontManager().loadFonts(this.mDroidFont);

    }

    @Override
    protected Scene onCreateScene() {

        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();

        this.mScrollDetector = new SurfaceScrollDetector(this);

        this.mScene.setOnSceneTouchListener(this);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);
        this.mScene.setTouchAreaBindingOnActionMoveEnabled(true);
        this.mScene.setOnSceneTouchListenerBindingOnActionDownEnabled(true);

        CreateLevelBoxes();

        return this.mScene;
    }


    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {

        this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
        return true;
    }


    // ===========================================================
    // Methods
    // ===========================================================

    private void CreateLevelBoxes() {


        mMenuLayer = new Entity(0, 0);
        this.mScene.attachChild(mMenuLayer);

//        final Rectangle coloredRect = new Rectangle(0, 0, 1024, 1024, this.getVertexBufferObjectManager());
//        coloredRect.setColor(1, 1, 0, 1);
//        layer.attachChild(coloredRect);

        // calculate the amount of required columns for the level count
        int totalRows = (LEVELS / LEVEL_COLUMNS_PER_SCREEN) + 1;

        // Calculate space between each level square
        int spaceBetweenRows = (CAMERA_HEIGHT / LEVEL_ROWS_PER_SCREEN) - LEVEL_PADDING;
        int spaceBetweenColumns = (CAMERA_WIDTH / LEVEL_COLUMNS_PER_SCREEN) - LEVEL_PADDING;

        //Current Level Counter
        int iLevel = 1;

        //Create the Level selectors, one row at a time.
        int boxX = LEVEL_PADDING, boxY = LEVEL_PADDING;
        for (int y = 0; y < totalRows; y++) {
            for (int x = 0; x < LEVEL_COLUMNS_PER_SCREEN; x++) {

                //On Touch, save the clicked level in case it's a click and not a scroll.
                final int levelToLoad = iLevel;

                // Create the rectangle. If the level selected
                // has not been unlocked yet, don't allow loading.
                Rectangle box = new Rectangle(boxX, boxY, LEVEL_BOX_SIZE, LEVEL_BOX_SIZE, this.getVertexBufferObjectManager()) {
                    @Override
                    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

                        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {

                            if (levelToLoad < mMaxLevelReached) {
                                loadLevel(levelToLoad);
                            }
                            return true;
                        }

                        return false;
                    }
                };

                if (iLevel >= mMaxLevelReached)
                    box.setColor(0, 0, 0.9f);
                else
                    box.setColor(0, 0.9f, 0f);
                box.setAlpha(0.5f);

                mMenuLayer.attachChild(box);

                //Center for different font size
                int textMargin = iLevel < 10 ? 18 : 10;
                mMenuLayer.attachChild(new Text(boxX + textMargin, boxY + 15, this.mDroidFont, String.valueOf(iLevel), this.getVertexBufferObjectManager()));


                this.mScene.registerTouchArea(box);

                iLevel++;
                boxX += spaceBetweenColumns + LEVEL_PADDING;

                if (iLevel > LEVELS)
                    break;
            }

            if (iLevel > LEVELS)
                break;

            boxY += spaceBetweenRows + LEVEL_PADDING;
            boxX = LEVEL_BOX_SIZE;
        }

        //Set the max scroll possible, so it does not go over the boundaries.
        mMaxY = boxY - CAMERA_HEIGHT + 200;
    }


    //Here is where you call the level load.
    private void loadLevel(final int iLevel) {
        if (iLevel != -1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ScrollMenuActivity.this, "Loads Level " + String.valueOf(iLevel), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onScrollStarted(ScrollDetector pScollDetector,
                                int pPointerID, float pDistanceX, float pDistanceY) {

    }

    @Override
    public void onScroll(ScrollDetector pScollDetector, int pPointerID,
                         float pDistanceX, float pDistanceY) {

        pDistanceY *= -1;

        if (((mCurrentY - pDistanceY) < mMinY) || ((mCurrentY - pDistanceY) > mMaxY))
            return;

        mMenuLayer.setPosition(0, mMenuLayer.getY() + pDistanceY);

        mCurrentY -= pDistanceY;
    }

    @Override
    public void onScrollFinished(ScrollDetector pScollDetector,
                                 int pPointerID, float pDistanceX, float pDistanceY) {

    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.mCamera);

        return engineOptions;
    }


}
