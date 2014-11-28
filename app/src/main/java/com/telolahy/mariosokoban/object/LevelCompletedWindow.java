package com.telolahy.mariosokoban.object;

import android.util.Log;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.GameManager;
import com.telolahy.mariosokoban.manager.ResourcesManager;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;

import java.security.InvalidParameterException;

/**
 * Created by stephanohuguestelolahy on 11/27/14.
 */
public class LevelCompletedWindow extends Sprite {

    private static final int MENU_RETRY = 1;
    private static final int MENU_NEXT = 2;

    IMenuItem mNextMenuItem;

    public interface LevelCompleteWindowListener {

        void levelCompleteWindowNextButtonClicked();

        void levelCompleteWindowReplayButtonClicked();
    }

    private TiledSprite mStars[] = new TiledSprite[3];
    private LevelCompleteWindowListener mListener;

    public LevelCompletedWindow(Scene scene, LevelCompleteWindowListener listener) {

        super(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, ResourcesManager.getInstance().levelCompletedBackgroundTextureRegion, ResourcesManager.getInstance().vertexBufferObjectManager);
        mListener = listener;
        attachStars(scene);
        attachMenu(scene);
    }

    private void attachMenu(Scene scene) {

        MenuScene menuScene = new MenuScene(ResourcesManager.getInstance().camera);

        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        TextMenuItem nextTextMenuItem = new TextMenuItem(MENU_NEXT, resourcesManager.font, resourcesManager.activity.getResources().getString(R.string.suivant), resourcesManager.vertexBufferObjectManager);
        mNextMenuItem = new ScaleMenuItemDecorator(nextTextMenuItem, 1.2f, 1);
        menuScene.addMenuItem(mNextMenuItem);

        menuScene.buildAnimations();
        menuScene.setBackgroundEnabled(false);
        mNextMenuItem.setPosition(Constants.SCREEN_WIDTH / 2, 120);
        mNextMenuItem.setVisible(false);

        menuScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case MENU_RETRY:
                        mListener.levelCompleteWindowReplayButtonClicked();
                        return true;
                    case MENU_NEXT:
                        mListener.levelCompleteWindowNextButtonClicked();
                        return true;
                    default:
                        return false;
                }
            }
        });

        scene.setChildScene(menuScene);
    }

    private void attachStars(Scene scene) {

        ResourcesManager resourcesManager = ResourcesManager.getInstance();

        String text = resourcesManager.activity.getResources().getString(R.string.level_completed);
        boolean isOnLastLevel = GameManager.getInstance().isOnLastLevel();
        if (isOnLastLevel) {
            text = resourcesManager.activity.getResources().getString(R.string.last_level_completed);
        }

        attachChild(new Text(Constants.SCREEN_WIDTH / 2, 380, resourcesManager.font, text, resourcesManager.vertexBufferObjectManager));

        mStars[0] = new TiledSprite(275, 260, resourcesManager.levelCompletedStarsTextureRegion, resourcesManager.vertexBufferObjectManager);
        mStars[1] = new TiledSprite(400, 260, resourcesManager.levelCompletedStarsTextureRegion, resourcesManager.vertexBufferObjectManager);
        mStars[2] = new TiledSprite(525, 260, resourcesManager.levelCompletedStarsTextureRegion, resourcesManager.vertexBufferObjectManager);
        attachChild(mStars[0]);
        attachChild(mStars[1]);
        attachChild(mStars[2]);
    }

    public void display(int starsCount, Scene scene, Camera camera) {

        if (starsCount < 1 || starsCount > 3) {
            throw new InvalidParameterException("stars count should be in (1-3)");
        }

        mNextMenuItem.setVisible(true);

        for (int i = 0; i < 3; i++) {
            if (i < starsCount) {
                mStars[i].setCurrentTileIndex(0);
            } else {
                mStars[i].setCurrentTileIndex(1);
            }
        }

        // Hide HUD
        if (camera.getHUD() != null) {
            camera.getHUD().setVisible(false);
        }

        // Disable camera chase entity
        camera.setChaseEntity(null);

        // Attach our level complete panel in the middle of camera
        setPosition(camera.getCenterX(), camera.getCenterY());
        scene.attachChild(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        Log.i("", "finalize " + this.getClass().getName());
    }
}

