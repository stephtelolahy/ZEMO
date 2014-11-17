package com.telolahy.mariosokoban.scene;

import android.util.Log;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class GameScene extends BaseScene {

    private RepeatingSpriteBackground mGrassBackground;

    @Override
    public void createScene() {

        createBackground();
        createAnalogOnScreenControl();
        loadLevel(1);
        createHUD();
    }

    private void createAnalogOnScreenControl() {

        final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0, 0, this.mCamera, mResourcesManager.gameOnScreenControlBaseTextureRegion, mResourcesManager.gameOnScreenControlKnobTextureRegion, 0.1f, 200, mVertexBufferObjectManager, new AnalogOnScreenControl.IAnalogOnScreenControlListener() {
            @Override
            public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
                Log.i("", "onControlChange " + pValueX + " " + pValueY);
            }

            @Override
            public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
                Log.i("", "onControlClick");
            }
        });

        analogOnScreenControl.getControlBase().setAlpha(0.5f);
        analogOnScreenControl.getControlBase().setOffsetCenter(0, 0);
        analogOnScreenControl.getControlKnob().setScale(1.25f);

        setChildScene(analogOnScreenControl);
    }


    @Override
    public void disposeScene() {

        this.detachSelf();
        this.dispose();
    }

    @Override
    public void onBackKeyPressed() {

        SceneManager.getInstance().loadMenuScene();
    }

    private void createBackground() {

        mGrassBackground = new RepeatingSpriteBackground(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, mResourcesManager.gameGrassBackgroundTextureRegion, mVertexBufferObjectManager);
        setBackground(mGrassBackground);
    }

    private void createHUD() {

    }

    private void loadLevel(int level) {

    }

}
