package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class MainMenuScene extends BaseScene {

    private MenuScene mMenuChildScene;

    private static final int MENU_PLAY = 0;
    private static final int MENU_HELP = 1;

    @Override
    public void createScene() {

        createBackground();
        createMenuChildScene();

        if (!mResourcesManager.menuMusic.isPlaying()) {
            mResourcesManager.menuMusic.setLooping(true);
            mResourcesManager.menuMusic.setVolume(0.4f);
            mResourcesManager.menuMusic.play();
        }
    }

    private void createBackground() {

        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, new Sprite(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT / 2, mResourcesManager.menuParallaxLayerBackRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-2.f, new Sprite(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT - mResourcesManager.menuParallaxLayerMidRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerMidRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-5.f, new Sprite(Constants.CAMERA_WIDTH / 2, mResourcesManager.menuParallaxLayerFrontRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerFrontRegion, mVertexBufferObjectManager)));
        setBackground(autoParallaxBackground);

        final int groundY = 28;
        final AnimatedSprite player = new AnimatedSprite(Constants.CAMERA_WIDTH / 2, mResourcesManager.menuPlayerTextureRegion.getHeight() / 2 + groundY, mResourcesManager.menuPlayerTextureRegion, mVertexBufferObjectManager);
        player.animate(new long[]{250, 250, 250, 250}, 8, 11, true);
        attachChild(player);
    }

    private void createMenuChildScene() {

        mMenuChildScene = new MenuScene(mCamera);

        TextMenuItem playTextMenuItem = new TextMenuItem(MENU_PLAY, mResourcesManager.font, mActivity.getResources().getString(R.string.play), mVertexBufferObjectManager);
        IMenuItem playMenuItem = new ScaleMenuItemDecorator(playTextMenuItem, 1.2f, 1);
        TextMenuItem helpTextMenuItem = new TextMenuItem(MENU_HELP, mResourcesManager.font, mActivity.getResources().getString(R.string.options), mVertexBufferObjectManager);
        IMenuItem helpMenuItem = new ScaleMenuItemDecorator(helpTextMenuItem, 1.2f, 1);
        mMenuChildScene.addMenuItem(playMenuItem);
        mMenuChildScene.addMenuItem(helpMenuItem);

        mMenuChildScene.buildAnimations();
        mMenuChildScene.setBackgroundEnabled(false);

        mMenuChildScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case MENU_PLAY:
                        SceneManager.getInstance().createGameScene();
                        return true;
                    case MENU_HELP:
                        return true;
                    default:
                        return false;
                }
            }
        });

        setChildScene(mMenuChildScene);
    }


    @Override
    public void onBackKeyPressed() {

        System.exit(0);
    }

    @Override
    public void disposeScene() {

        if (mResourcesManager.menuMusic.isPlaying()) {
            mResourcesManager.menuMusic.pause();
        }

        this.detachSelf();
        this.dispose();
    }
}
