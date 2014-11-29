package com.telolahy.mariosokoban.scene;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

/**
 * Created by stephanohuguestelolahy on 11/16/14.
 */
public class LoadingScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

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

        setBackground(new Background(Color.BLACK));
        String text = mActivity.getResources().getString(R.string.loading);
        attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.font, text, mVertexBufferObjectManager));
    }

    @Override
    protected void onDisposeScene() {

    }

    @Override
    public void onBackKeyPressed() {

    }

    // ===========================================================
    // Methods
    // ===========================================================
}
