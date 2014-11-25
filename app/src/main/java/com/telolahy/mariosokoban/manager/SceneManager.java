package com.telolahy.mariosokoban.manager;

import com.telolahy.mariosokoban.scene.BaseScene;
import com.telolahy.mariosokoban.scene.GameScene;
import com.telolahy.mariosokoban.scene.LoadingScene;
import com.telolahy.mariosokoban.scene.MainMenuScene;
import com.telolahy.mariosokoban.scene.SplashScene;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface;

/**
 * Created by stephanohuguestelolahy on 11/15/14.
 */
public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    private Engine mEngine = ResourcesManager.getInstance().engine;
    private BaseScene mCurrentScene;
    private BaseScene mSplashScene;
    private BaseScene mMenuScene;
    private BaseScene mLoadingScene;
    private BaseScene mGameScene;

    public static SceneManager getInstance() {
        return INSTANCE;
    }

    private void setScene(BaseScene scene) {

        mEngine.setScene(scene);
        mCurrentScene = scene;
    }

    public BaseScene getCurrentScene() {

        return mCurrentScene;
    }

    public void createSplashScene(IGameInterface.OnCreateSceneCallback pOnCreateSceneCallback) {

        ResourcesManager.getInstance().loadSplashResources();
        mSplashScene = new SplashScene();
        mCurrentScene = mSplashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(mSplashScene);
    }

    private void disposeSplashScene() {

        ResourcesManager.getInstance().unloadSplashResources();
        mSplashScene.disposeScene();
        mSplashScene = null;
    }

    public void createMenuScene() {

        ResourcesManager.getInstance().loadMenuResources();
        mMenuScene = new MainMenuScene("" + 2);
        mLoadingScene = new LoadingScene();
        setScene(mMenuScene);
        disposeSplashScene();
    }

    public void createGameScene(final int level) {

        setScene(mLoadingScene);
        ResourcesManager.getInstance().unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadGameResources();
                mGameScene = new GameScene("" + level);
                setScene(mGameScene);
            }
        }));
    }

    public void loadMenuScene() {

        setScene(mLoadingScene);
        mGameScene.disposeScene();
        ResourcesManager.getInstance().unloadGameTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadMenuTextures();
                setScene(mMenuScene);
            }
        }));
    }
}
