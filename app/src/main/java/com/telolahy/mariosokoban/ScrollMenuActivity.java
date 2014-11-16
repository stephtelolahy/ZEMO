package com.telolahy.mariosokoban;

import android.widget.Toast;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class ScrollMenuActivity extends SimpleBaseGameActivity implements IScrollDetectorListener, IOnSceneTouchListener, IClickDetectorListener {

    // ===========================================================
    // Constants
    // ===========================================================
    protected static int CAMERA_WIDTH = 480;
    protected static int CAMERA_HEIGHT = 320;
    protected static int PADDING = 50;
    protected static int MENUITEMS = 7;


    // ===========================================================
    // Fields
    // ===========================================================
    private Scene mScene;
    private Camera mCamera;

    private List<TextureRegion> menuItemTextureRegions = new ArrayList<TextureRegion>();

    // Scrolling
    private SurfaceScrollDetector mScrollDetector;
    private ClickDetector mClickDetector;

    private float mMinX = 0;
    private float mMaxX = 0;
    private float mCurrentX = 0;
    private int iItemClicked = -1;

    private Rectangle scrollBar;


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

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/scrollmenu/");

        //Images for the menu
        for (int i = 0; i < MENUITEMS; i++) {
            BitmapTextureAtlas mMenuBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            ITextureRegion mMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBitmapTextureAtlas, this, "menu" + i + ".png", 0, 0);

            this.mEngine.getTextureManager().loadTexture(mMenuBitmapTextureAtlas);
            menuItemTextureRegions.add((TextureRegion) mMenuTextureRegion);
        }
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.mCamera);
        engineOptions.getTouchOptions().setNeedsMultiTouch(true);

        return engineOptions;
    }

    @Override
    protected Scene onCreateScene() {

        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        this.mScene.setBackground(new Background(0, 0, 0));

        this.mScrollDetector = new SurfaceScrollDetector(this);
        this.mClickDetector = new ClickDetector(this);

        this.mScene.setOnSceneTouchListener(this);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);
        this.mScene.setTouchAreaBindingOnActionMoveEnabled(true);
        this.mScene.setOnSceneTouchListenerBindingOnActionDownEnabled(true);

        CreateMenuBoxes();

        return this.mScene;

    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        this.mClickDetector.onTouchEvent(pSceneTouchEvent);
        this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
        return true;
    }

    @Override
    public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

        //Return if ends are reached
        if (((mCurrentX - pDistanceX) < mMinX)) {
            return;
        } else if ((mCurrentX - pDistanceX) > mMaxX) {
            return;
        }

        //Center camera to the current point
        this.mCamera.offsetCenter(-pDistanceX, 0);
        mCurrentX -= pDistanceX;


        //Set the scrollbar with the camera
        float tempX = mCamera.getCenterX() - CAMERA_WIDTH / 2;
        // add the % part to the position
        tempX += (tempX / (mMaxX + CAMERA_WIDTH)) * CAMERA_WIDTH;
        //set the position
        scrollBar.setPosition(tempX, scrollBar.getY());

        //Because Camera can have negativ X values, so set to 0
        if (this.mCamera.getXMin() < 0) {
            this.mCamera.offsetCenter(0, 0);
            mCurrentX = 0;
        }


    }

    @Override
    public void onClick(ClickDetector pClickDetector, int pPointerID, float pSceneX, float pSceneY) {
        loadLevel(iItemClicked);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void CreateMenuBoxes() {

        int spriteX = PADDING;
        int spriteY = CAMERA_HEIGHT / 2;

        //current item counter
        int iItem = 1;

        for (int x = 0; x < menuItemTextureRegions.size(); x++) {

            //On Touch, save the clicked item in case it's a click and not a scroll.
            final int itemToLoad = iItem;

            Sprite sprite = new Sprite(spriteX, spriteY, (ITextureRegion) menuItemTextureRegions.get(x), this.getVertexBufferObjectManager()) {

                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    iItemClicked = itemToLoad;
                    return false;
                }
            };
            iItem++;


            this.mScene.attachChild(sprite);
            this.mScene.registerTouchArea(sprite);

            spriteX += 20 + PADDING + sprite.getWidth();
        }

        mMaxX = spriteX - CAMERA_WIDTH;

        //set the size of the scrollbar
        float scrollbarsize = CAMERA_WIDTH / ((mMaxX + CAMERA_WIDTH) / CAMERA_WIDTH);
        scrollBar = new Rectangle(0, CAMERA_HEIGHT - 20, scrollbarsize, 20, this.getVertexBufferObjectManager());
        scrollBar.setColor(1, 0, 0);
        this.mScene.attachChild(scrollBar);
    }


    //Here is where you call the item load.
    private void loadLevel(final int iLevel) {
        if (iLevel != -1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ScrollMenuActivity.this, "Load level" + String.valueOf(iLevel), Toast.LENGTH_SHORT).show();
                    iItemClicked = -1;
                }
            });
        }
    }


    @Override
    public void onScrollStarted(ScrollDetector pScollDetector,
                                int pPointerID, float pDistanceX, float pDistanceY) {

    }


    @Override
    public void onScrollFinished(ScrollDetector pScollDetector,
                                 int pPointerID, float pDistanceX, float pDistanceY) {

    }


}
