package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.entity.scene.background.RepeatingSpriteBackground;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class GameScene extends BaseScene {

    private RepeatingSpriteBackground mGrassBackground;

    @Override
    public void createScene() {

        createHUD();
        createBackground();
        loadLevel(1);
    }


    @Override
    public void disposeScene() {

    }

    @Override
    public void onBackKeyPressed() {

        SceneManager.getInstance().loadMenuScene();
    }

    private void createBackground() {

    }

    private void createHUD() {

    }

    private void loadLevel(int level) {

    }

}
