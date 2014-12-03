package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;

/**
 * Created by stephanohuguestelolahy on 11/15/14.
 */
public class SplashScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Sprite mBackground;

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
    protected void onCreateScene(int... params) {

        mBackground = new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.splashTextureRegion, mVertexBufferObjectManager);
        attachChild(mBackground);

        mBackground.setAlpha(0);
        IEntityModifier sequenceModifier = new SequenceEntityModifier(new AlphaModifier(1.f, 0.f, 0.f), new AlphaModifier(1.f, 0.f, 1.f), new AlphaModifier(1.f, 1.f, 1.f));
        sequenceModifier.addModifierListener(new IEntityModifier.IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                SceneManager.getInstance().createMenuScene();
            }
        });
        mBackground.registerEntityModifier(sequenceModifier);
    }

    @Override
    protected void onDisposeScene() {

        mBackground.detachSelf();
    }

    @Override
    public void onBackKeyPressed() {

    }
}
