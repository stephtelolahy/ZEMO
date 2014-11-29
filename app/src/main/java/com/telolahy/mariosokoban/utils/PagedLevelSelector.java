package com.telolahy.mariosokoban.utils;

import android.graphics.Point;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.manager.ResourcesManager;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.ArrayList;

/**
 * Created by stephanohuguestelolahy on 11/29/14.
 */
public class PagedLevelSelector extends MenuScene implements ScrollDetector.IScrollDetectorListener {

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

    // ===========================================================
    // Fields
    // ===========================================================

    private final int mMaxLevelReached;
    private final int mLevelsCount;
    private boolean mIsDescelerating;
    private boolean mIsDragging;
    private float mMinX;
    private float mMaxX;

    private PagedLevelSelectorListener mListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PagedLevelSelector(Camera pCamera, int maxLevelReached, int levelsCount, PagedLevelSelectorListener listener) {

        super(pCamera);

        mMaxLevelReached = maxLevelReached;
        mLevelsCount = levelsCount;
        mListener = listener;
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        VertexBufferObjectManager vertexBufferManager = resourcesManager.vertexBufferObjectManager;

        this.setOnSceneTouchListener(new SurfaceScrollDetector(this));

        // calculate the amount of required columns for the level count
        final int levelsPerPage = LEVEL_ROWS_PER_SCREEN * LEVEL_COLUMNS_PER_SCREEN;
        final int totalPages = (levelsCount / levelsPerPage) + (levelsCount % levelsPerPage == 0 ? 0 : 1);

        // Calculate space between each level square
        int spaceBetweenRows = (Constants.SCREEN_HEIGHT - LEVEL_MARGIN_TOP - LEVEL_MARGIN_BOTTOM) / (LEVEL_ROWS_PER_SCREEN - 1);
        int spaceBetweenColumns = (Constants.SCREEN_WIDTH - LEVEL_MARGIN_LEFT - LEVEL_MARGIN_RIGHT) / (LEVEL_COLUMNS_PER_SCREEN - 1);

        //Current Level Counter
        int iLevel = 1;
        ArrayList<Point> levelPositions = new ArrayList<Point>();

        // Create the level selectors, one page at a time
        for (int page = 0; page < totalPages; page++) {

            int pageX = page * LEVEL_PAGE_WIDTH;

            //Create the Level selectors, one row at a time.
            for (int y = 0; y < LEVEL_ROWS_PER_SCREEN && iLevel <= levelsCount; y++) {

                int boxY = Constants.SCREEN_HEIGHT - LEVEL_MARGIN_TOP - spaceBetweenRows * y;

                for (int x = 0; x < LEVEL_COLUMNS_PER_SCREEN && iLevel <= levelsCount; x++) {

                    //On Touch, save the clicked level in case it's a click and not a scroll.
                    final int levelToLoad = iLevel;
                    final boolean isUnlocked = levelToLoad <= mMaxLevelReached;

                    int boxX = pageX + LEVEL_MARGIN_LEFT + spaceBetweenColumns * x;
                    levelPositions.add(new Point(boxX, boxY));

                    ITextureRegion textureRegion = isUnlocked ? resourcesManager.menuLevelUnlockedRegion : resourcesManager.menuLevelLockedRegion;

                    // Create the rectangle. If the level selected
                    // has not been unlocked yet, don't allow loading.
                    IMenuItem levelMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(iLevel, textureRegion, vertexBufferManager), 1.2f, 1);
                    if (isUnlocked) {
                        levelMenuItem.attachChild(new Text(42, 42, resourcesManager.menuLevelFont, String.valueOf(iLevel), vertexBufferManager));
                    }
                    addMenuItem(levelMenuItem);

                    iLevel++;
                }
            }
        }

        buildAnimations();
        setBackgroundEnabled(false);

        int i = 0;
        for (IMenuItem item : getMenuItems()) {

            Point position = levelPositions.get(i);
            item.setPosition(position.x, position.y);
            i++;
        }

        setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                if (mIsDescelerating || mIsDragging) {
                    return false;
                }

                int level = pMenuItem.getID();
                if (level <= mMaxLevelReached) {
                    mListener.levelSelectorItemClicked(level);
                }
                return true;
            }
        });

        //Set the max scroll possible, so it does not go over the boundaries.
        mMinX = -(totalPages - 1) * LEVEL_PAGE_WIDTH;
        mMaxX = 0;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    // ===========================================================
    // Methods from Interfaces
    // ===========================================================

    @Override
    public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {


        if (mParentScene == null || mIsDescelerating) {
            return;
        }

        Entity menuLayer = getLayer();
        float newX = menuLayer.getX() + pDistanceX;

        if (newX < mMinX || newX > mMaxX) {
            return;
        }

        menuLayer.setPosition(newX, 0);
    }

    @Override
    public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

        if (mParentScene == null || mIsDescelerating) {
            return;
        }

        mIsDragging = true;
    }

    @Override
    public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

        if (mParentScene == null || mIsDescelerating) {
            return;
        }

        mIsDragging = false;

        Entity menuLayer = getLayer();

        // move to nearest offset
        float sourceX = menuLayer.getX();
        float targetX = Math.round(sourceX / LEVEL_PAGE_WIDTH) * LEVEL_PAGE_WIDTH;

        if (targetX == sourceX) {
            return;
        }

        float dx = targetX - sourceX;
        final PathModifier.Path path = new PathModifier.Path(2).to(sourceX, 0).to(targetX, 0);
        float pathAnimationDuration = .4f;
        menuLayer.registerEntityModifier(new PathModifier(pathAnimationDuration, path, null, new PathModifier.IPathModifierListener() {

            @Override
            public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {

                mIsDescelerating = true;
            }

            @Override
            public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {

            }

            @Override
            public void onPathWaypointFinished(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {

            }

            @Override
            public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {

                mIsDescelerating = false;
            }
        }));

    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

    public interface PagedLevelSelectorListener {

        public void levelSelectorItemClicked(int level);
    }
}
