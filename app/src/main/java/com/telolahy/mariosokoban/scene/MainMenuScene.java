package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;

import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
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
    }

    private void createBackground() {

//        mBackground = new Sprite(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT / 2, mResourcesManager.menuBackgroundTextureRegion, mVertexBufferObjectManager);
//        attachChild(mBackground);

        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.0f, new Sprite(Constants.CAMERA_WIDTH / 2, Constants.CAMERA_HEIGHT / 2, mResourcesManager.menuBackgroundTextureRegion, mVertexBufferObjectManager)));
        setBackground(autoParallaxBackground);

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

        mBackground.detachSelf();
        mBackground.dispose();
        this.detachSelf();
        this.dispose();
    }
}
