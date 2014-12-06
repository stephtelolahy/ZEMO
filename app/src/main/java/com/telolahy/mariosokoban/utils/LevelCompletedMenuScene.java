package com.telolahy.mariosokoban.utils;

import android.util.Log;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.ResourcesManager;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;

import java.security.InvalidParameterException;

/**
 * Created by stephanohuguestelolahy on 11/27/14.
 */
public class LevelCompletedMenuScene extends MenuScene {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MENU_ITEM_REPLAY = 1;
    private static final int MENU_ITEM_NEXT = 2;

    // ===========================================================
    // Fields
    // ===========================================================

    private Sprite mBackgroundSprite;
    private TiledSprite mStars[] = new TiledSprite[3];
    private Text mTitleText;
    private LevelCompletedMenuSceneListener mListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public LevelCompletedMenuScene(Camera pCamera, LevelCompletedMenuSceneListener listener) {

        super(pCamera);
        mListener = listener;
        createBackground();
        createStars();
        createMenu();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        Log.i("", "finalize " + this.getClass().getName());
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void display(int starsCount, final Scene parentScene) {

        if (starsCount < 1 || starsCount > 3) {
            throw new InvalidParameterException("stars count should be in (1-3)");
        }

        for (int i = 0; i < 3; i++) {
            if (i < starsCount) {
                mStars[i].setCurrentTileIndex(0);
            } else {
                mStars[i].setCurrentTileIndex(1);
            }
        }

        // Hide HUD
        if (mCamera.getHUD() != null) {
            mCamera.getHUD().setVisible(false);
        }

        // Disable camera chase entity
        mCamera.setChaseEntity(null);

        // Attach our level complete panel in the middle of camera
        mBackgroundSprite.setAlpha(0);
        parentScene.attachChild(mBackgroundSprite);
        mBackgroundSprite.registerEntityModifier(new FadeInModifier(1.f, new IEntityModifier.IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {

                parentScene.setChildScene(LevelCompletedMenuScene.this);
            }
        }));
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void createBackground() {

        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        mBackgroundSprite = new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, resourcesManager.levelCompletedBackgroundTextureRegion, resourcesManager.vertexBufferObjectManager);
    }

    private void createMenu() {

        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        TextMenuItem textMenuItem = new TextMenuItem(MENU_ITEM_NEXT, resourcesManager.menuItemFont, resourcesManager.activity.getResources().getString(R.string.next), resourcesManager.vertexBufferObjectManager);
        IMenuItem menuItem = new ScaleMenuItemDecorator(textMenuItem, 1.2f, 1);
        addMenuItem(menuItem);

        buildAnimations();
        setBackgroundEnabled(false);
        menuItem.setPosition(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 4);

        setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case MENU_ITEM_REPLAY:
                        mListener.levelCompletedMenuSceneReplayButtonClicked();
                        return true;
                    case MENU_ITEM_NEXT:
                        mListener.levelCompletedMenuSceneNextButtonClicked();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void createStars() {

        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        mTitleText = new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT * 3 / 4, resourcesManager.menuItemFont, resourcesManager.activity.getResources().getString(R.string.level_completed), resourcesManager.vertexBufferObjectManager);
        attachChild(mTitleText);

        int padding = (int) resourcesManager.levelCompletedStarsTextureRegion.getWidth() * 3 / 2;
        mStars[0] = new TiledSprite(Constants.SCREEN_WIDTH / 2 - padding, Constants.SCREEN_HEIGHT / 2, resourcesManager.levelCompletedStarsTextureRegion, resourcesManager.vertexBufferObjectManager);
        mStars[1] = new TiledSprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, resourcesManager.levelCompletedStarsTextureRegion, resourcesManager.vertexBufferObjectManager);
        mStars[2] = new TiledSprite(Constants.SCREEN_WIDTH / 2 + padding, Constants.SCREEN_HEIGHT / 2, resourcesManager.levelCompletedStarsTextureRegion, resourcesManager.vertexBufferObjectManager);
        attachChild(mStars[0]);
        attachChild(mStars[1]);
        attachChild(mStars[2]);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

    public interface LevelCompletedMenuSceneListener {

        void levelCompletedMenuSceneNextButtonClicked();

        void levelCompletedMenuSceneReplayButtonClicked();
    }

}

