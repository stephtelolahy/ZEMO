package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.sprite.Sprite;

/**
 * Created by stephanohuguestelolahy on 11/15/14.
 */
public class SplashScene extends BaseScene {

    private Sprite mBackground;

    @Override
    protected void onCreateScene(int... params) {

        mBackground = new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.splashTextureRegion, mVertexBufferObjectManager);
        mBackground.setAlpha(0);
        attachChild(mBackground);

        mResourcesManager.engine.registerUpdateHandler(new TimerHandler(0.4f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                FadeInModifier fadeInModifier = new FadeInModifier(1f);
                mBackground.registerEntityModifier(fadeInModifier);
            }
        }));
    }

    @Override
    protected void onDisposeScene() {

        mBackground.detachSelf();
        mBackground.dispose();
    }

    @Override
    public void onBackKeyPressed() {

    }
}
