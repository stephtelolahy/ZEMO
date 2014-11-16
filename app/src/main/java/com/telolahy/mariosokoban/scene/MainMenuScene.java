package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;

import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
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

    private Sprite mBackground;

    @Override
    public void createScene() {

        createBackground();
        createMenuChildScene();

        if(! mResourcesManager.menuMusic.isPlaying()) {
            mResourcesManager.menuMusic.play();
        }
    }

    private void createBackground() {

        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, new Sprite(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT / 2, mResourcesManager.menuParallaxLayerBackRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-3.f, new Sprite(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT - mResourcesManager.menuParallaxLayerMidRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerMidRegion, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-5.f, new Sprite(Constants.CAMERA_WIDTH / 2, mResourcesManager.menuParallaxLayerFrontRegion.getHeight() / 2, mResourcesManager.menuParallaxLayerFrontRegion, mVertexBufferObjectManager)));
        setBackground(autoParallaxBackground);

        final int groundY = 32;
        final AnimatedSprite player = new AnimatedSprite(Constants.CAMERA_WIDTH / 2, mResourcesManager.menuPlayerTextureRegion.getHeight() / 2 + groundY, mResourcesManager.menuPlayerTextureRegion, mVertexBufferObjectManager);
        player.animate(new long[]{200, 200, 200,200, 200, 200,200, 200}, 24, 31, true);
        attachChild(player);

        final Sprite box = new Sprite(player.getX() + player.getWidth() / 2 + mResourcesManager.menuBoxTextureRegion.getWidth() / 2, mResourcesManager.menuBoxTextureRegion.getHeight() / 2 + groundY, mResourcesManager.menuBoxTextureRegion, mVertexBufferObjectManager);
//        attachChild(box);
    }

    private void createMenuChildScene() {

        mMenuChildScene = new MenuScene(mCamera);
        mMenuChildScene.setPosition(0, 0);

        IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, mResourcesManager.menuPlayTextureRegion, mVertexBufferObjectManager), 1.2f, 1);
        IMenuItem helpMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HELP, mResourcesManager.menuHelpTextureRegion, mVertexBufferObjectManager), 1.2f, 1);
        mMenuChildScene.addMenuItem(playMenuItem);
        mMenuChildScene.addMenuItem(helpMenuItem);

        mMenuChildScene.buildAnimations();
        mMenuChildScene.setBackgroundEnabled(false);

        mMenuChildScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

                switch (pMenuItem.getID()) {
                    case MENU_PLAY:
//                        SceneManager.getInstance().loadGameScene();
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

        if(mResourcesManager.menuMusic.isPlaying()) {
            mResourcesManager.menuMusic.pause();
        }

        mBackground.detachSelf();
        mBackground.dispose();
        this.detachSelf();
        this.dispose();
    }
}
