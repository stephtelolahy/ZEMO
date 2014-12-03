package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

/**
 * Created by stephanohuguestelolahy on 12/3/14.
 */
public class CreditsScene extends BaseScene {

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

        mBackground = new Sprite(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.menuCreditsBackgroundTextureRegion, mVertexBufferObjectManager);
        attachChild(mBackground);

        attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 4, mResourcesManager.menuCreditsFont, mActivity.getResources().getString(R.string.game_designer), mVertexBufferObjectManager));
        attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 4 - 44, mResourcesManager.menuLevelFont, mActivity.getResources().getString(R.string.game_designer_value), mVertexBufferObjectManager));
    }

    @Override
    protected void onDisposeScene() {

    }

    @Override
    public void onBackKeyPressed() {

    }
}
