package com.telolahy.mariosokoban.object;

import android.util.Log;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;
import com.telolahy.mariosokoban.manager.GameManager;
import com.telolahy.mariosokoban.manager.ResourcesManager;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.security.InvalidParameterException;

/**
 * Created by stephanohuguestelolahy on 11/27/14.
 */
public class LevelCompletedWindow extends Sprite {


    public interface LevelCompleteWindowListener {

        void levelCompleteWindowNextButtonClicked();

        void levelCompleteWindowReplayButtonClicked();
    }

    private TiledSprite mStars[] = new TiledSprite[3];
    private LevelCompleteWindowListener mListener;

    public LevelCompletedWindow(VertexBufferObjectManager pSpriteVertexBufferObject, Scene scene, LevelCompleteWindowListener listener) {

        super(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, ResourcesManager.getInstance().levelCompletedBackgroundTextureRegion, pSpriteVertexBufferObject);
        mListener = listener;
        attachStars(pSpriteVertexBufferObject, scene);
    }

    private void attachStars(VertexBufferObjectManager pSpriteVertexBufferObject, Scene scene) {

        ResourcesManager resourcesManager = ResourcesManager.getInstance();

        String text = resourcesManager.activity.getResources().getString(R.string.level_completed);
        boolean isOnLastLevel = GameManager.getInstance().isOnLastLevel();
        if (isOnLastLevel) {
            text = resourcesManager.activity.getResources().getString(R.string.last_level_completed);
        }

        attachChild(new Text(Constants.SCREEN_WIDTH / 2, 380, resourcesManager.font, text, resourcesManager.vertexBufferObjectManager));

        mStars[0] = new TiledSprite(275, 260, resourcesManager.levelCompletedStarsTextureRegion, pSpriteVertexBufferObject);
        mStars[1] = new TiledSprite(400, 260, resourcesManager.levelCompletedStarsTextureRegion, pSpriteVertexBufferObject);
        mStars[2] = new TiledSprite(525, 260, resourcesManager.levelCompletedStarsTextureRegion, pSpriteVertexBufferObject);
        attachChild(mStars[0]);
        attachChild(mStars[1]);
        attachChild(mStars[2]);

//        Sprite retryButton = new Sprite(260, 120, resourcesManager.gameCompleteRetryRegion, pSpriteVertexBufferObject) {
//            @Override
//            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
//                mListener.levelCompleteWindowReplayButtonClicked();
//                return true;
//            }
//        };
//        attachChild(retryButton);
//        scene.registerTouchArea(retryButton);
//
//
//        if (isOnLastLevel) {
//
//            retryButton.setPosition(400, retryButton.getY());
//            return;
//        }
//
//        Sprite nextButton = new Sprite(540, 120, resourcesManager.gameCompleteNextRegion, pSpriteVertexBufferObject) {
//            @Override
//            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
//                mListener.levelCompleteWindowNextButtonClicked();
//                return true;
//            }
//        };
//        attachChild(nextButton);
//        scene.registerTouchArea(nextButton);
    }

    public void display(int starsCount, Scene scene, Camera camera) {

        if (starsCount < 1 || starsCount > 3) {
            throw new InvalidParameterException("stars count should be in (1-3)");
        }
        Log.i("", "starsCount " + starsCount);

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

